package com.safetynet.alert.controller.admin;

import com.safetynet.alert.exceptions.address.AddressNotFoundException;
import com.safetynet.alert.exceptions.firestation.FireStationAlreadyExistedException;
import com.safetynet.alert.exceptions.firestation.FireStationNotFoundException;
import com.safetynet.alert.exceptions.firestation.FireStationWithIdException;
import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.FireStationService;
import com.safetynet.alert.service.PersonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Rest Controller for entity {@link FireStation}.
 *
 * @author delaval
 *
 */
@RestController
@Api(description = "API to Manage FireStations")
@RequestMapping("/")
@Log4j2
public class FireStationRestController {

  @Autowired
  private FireStationService fireStationService;

  @Autowired
  private PersonService personService;

  /**
   * Return all existed FireStation.
   *
   * @return    a collection of all FireStations
   */
  @GetMapping(value = "/firestation", produces = "application/json")
  @ApiOperation(value = "Get all Firestations",
                notes = "Retrieve all fireStations",
                response = FireStation.class)
  public Iterable<FireStation> getFireStations() {

    return fireStationService.getFireStations();

  }

  /**
   * Return FireStation with identification Id.
   *
   * @param id
   *            the identification of the FireStation in database of type Long.
   *
   * @return    a ResponseEntity containing in body the FireStation with identification Id.
   *
   * @throws    a {@link FireStationNotFoundException}
   *            if there isn't a FireStation mapped by this Id.
   */
  @GetMapping(value = "/firestation/{id}", produces = "application/json")
  @ApiOperation(value = "Get FireStation by ID",
                notes = "Retrieve a Firestation by it's given ID",
                response = FireStation.class)
  public ResponseEntity<FireStation> getFireStationById(@PathVariable Long id) {

    Optional<FireStation> fireStation = fireStationService.getFireStationJoinAddressesById(id);

    if (fireStation.isPresent()) {

      log.info("FireStation with Id :{} was found and displayed in body response",
          id);
      return new ResponseEntity<FireStation>(fireStation.get(), HttpStatus.OK);

    } else {

      throw new FireStationNotFoundException("FireStation with Id:" + id
          + " was not found");

    }

  }

  /**
   * creation of a new FireStation.
   *
   * @param fireStationToSave
             a representation in Json of the new Object of FireStation.
  
   * @return   a ResponseEntity containing in body the FireStation
   *            with its new identification Id and its LocationUri.
   *
   * @throws    a {@link FireStationAlreadyExistedException}
   *            if the FireStation already exists with the given numberStation.
   */
  @PostMapping(value = "/firestation", produces = "application/json")
  @ApiOperation(value = "Creation of FireStation",
                notes = "Create a new FireStationby",
                response = FireStation.class)
  public ResponseEntity<FireStation>
      postMappingStationAddress(@Valid @RequestBody FireStation fireStationToSave) {

    if (fireStationToSave.getIdFireStation() == null) {

      Optional<FireStation> existedFireStation = fireStationService
          .getFireStationByNumberStation(fireStationToSave.getNumberStation());

      if (!existedFireStation.isPresent()) {

        FireStation savedFireStation = fireStationService.saveFireStation(fireStationToSave);

        URI locationUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(savedFireStation.getIdFireStation()).toUri();

        //check if for new saved fireSation, there is person to map with it

        if (!savedFireStation.getAddresses().isEmpty()) {

          boolean AddedPersons = false;

          for (String address : savedFireStation.getAddresses()) {

            Iterable<Person> personsMappedWithAddress =
                personService.getPersonsByAddress(address);

            if (personsMappedWithAddress.iterator().hasNext()) {

              AddedPersons = true;
              personsMappedWithAddress.forEach(person -> {

                savedFireStation.addPerson(person);
              });
            }
          }

          // to update junction table person_firestation.
          if (AddedPersons) {

            fireStationService.saveFireStation(savedFireStation);
          }
        }

        log.info("POST /fireStation: Creation of FireStation {} sucessed "
            + "with the locationId {} and mapping with address:{}",
            savedFireStation,
            savedFireStation.getIdFireStation(),
            savedFireStation.getAddresses());

        return ResponseEntity.created(locationUri).body(savedFireStation);

      } else {

        throw new FireStationAlreadyExistedException("this FireStation with NumberStation: "
            + existedFireStation.get().getNumberStation() + " already Existed");
      }

    } else {

      throw new FireStationWithIdException("Don't put a id in Body to save new FireStation!");
    }

  }


  /**
   * Allows to change the mapping of a address with a existed FireStation.
   * If the address is already mapped with another FireSation
   * then the existing mapping is deleted,
   * to create the new mapping with FireStation given in parameter.
   *
   * @param address
   *                  the address mapped with the FireStation.
   *
   * @param fireStationToMapWithAddress
   *                  the representation in Json of existed FireStation to be mapped
   *                  with the address given in parameter.
   *
   * @return          a ResponseEntity of the FireStation now mapped with address
   *
   * @throws          a {@link FireStationAlreadyExistedException}
   *                  if the existed FireStation given in body already mapped with address.
   *
   * @throws          a {@link FireStationNotFoundException} if FireStation given in body
   *                    doesn't exist.
   *
   */
  @PutMapping(value = "/firestation/{address}", produces = "application/json")
  @ApiOperation(value = "Update address/FireStation",
                notes = "Change the mapping of a address with a existed FireStation",
                response = FireStation.class)
  public ResponseEntity<FireStation> putMappingNumberStationAddress(
      @Valid @PathVariable String address,
      @Valid @RequestBody FireStation fireStationToMapWithAddress) {

    if (fireStationToMapWithAddress.getIdFireStation() == null) {
      //check if given body request fireStation exist

      Optional<FireStation> fireStationWithNumberStation =
          fireStationService.getFireStationAllFetchByNumberStation(
              fireStationToMapWithAddress.getNumberStation());


      if (fireStationWithNumberStation.isPresent()) {

        FireStation existedFireStation = fireStationWithNumberStation.get();

        //check validation of all field for given fireStationToMapWithAddress

        if (existedFireStation.getAddresses().contains(address)) {

          throw new FireStationAlreadyExistedException("This FireStation "
              + "already mapped with given address."
              + "Please give a another fireStation to map !");
        } else {

          // add address to fireStationToMapWithAddress and save it .

          existedFireStation.addAddress(address);

          //add persons Mapped by address to updated Firestation

          Iterable<Person> personsMappedByAddress =
              personService.getPersonsByAddress(address);

          if (personsMappedByAddress.iterator().hasNext()) {

            personsMappedByAddress.forEach(person -> {

              existedFireStation.addPerson(person);
            });
          }
          //update existedFireStation with new address
          // and new Person mapped with it

          fireStationService.saveFireStation(existedFireStation);

          log.info("PUT/firestation/address: the address {} was correctly mapped"
              + " with fireStation number{}",
              address,
              existedFireStation.getNumberStation());

          return new ResponseEntity<FireStation>(existedFireStation, HttpStatus.OK);
        }

      } else {

        throw new FireStationNotFoundException("fireStation given in body request"
            + " with numberStation:" + fireStationToMapWithAddress.getNumberStation()
            + " doesn't exist !");
      }

    } else {

      throw new FireStationWithIdException("Don't put a id in Body to save new FireStation!");
    }

  }

  /**
   * Delete the mapping of a existed FireStation with its address(es).
   *
   * @param numberStation
   *                      numberStation of FireStation to delete its mapping with its address(es).
   *
   * @return    ReponseEntity with in body the updated FireStation
   *            (without its mapping with any address).
   *
   * @throws    a {@link FireStationNotFoundException}
   *            if numberStation doesn't match with any fireStation
   */
  @DeleteMapping(value = "/firestation/station/{numberStation}", produces = "application/json")
  @ApiOperation(value = "Delete FireStation",
                notes = "Delete a FireStation and all mapping with it's addresses")
  public ResponseEntity<?> deleteMappingFireStation(@Valid @PathVariable int numberStation) {

    Optional<FireStation> fireStationWithNumberStation =
        fireStationService.getFireStationByNumberStation(numberStation);

    if (fireStationWithNumberStation.isPresent()) {

      FireStation currentFireStation = fireStationWithNumberStation.get();

      fireStationService.deleteFireStation(currentFireStation);

      log.info("FireStation with numberStation {} was deleted", numberStation);

      return new ResponseEntity<>("FireStation with NumberStation:"
          + numberStation + " was deleted!", HttpStatus.OK);

    } else {

      throw new FireStationNotFoundException("FireStation with NumberStation:"
          + numberStation + " was not found");

    }

  }

  /**
   * Delete the mapping of a existed address with its FireStation.
   *
   * @param address
                   address mapped with FireStation to delete its mapping with it.
   *
   * @return    ReponseEntity with in body the updated FireStation
   *            (without its mapping with this address).
   *
   * @throws    a {@link FireStationNotFoundException}
   *            if address isn't mapped with any fireStation
   */
  @DeleteMapping(value = "/firestation/address/{address}", produces = "application/json")
  @ApiOperation(value = "Delete address of FireStation",
                notes = "Delete mapping of address to FireStations")
  public ResponseEntity<?>
      deleteAddressFromFireStations(@PathVariable @Valid String address) {

    List<FireStation> fireStationWithAddress =
        fireStationService.getFireStationsMappedToAddress(address);

    List<Integer> numberStationsDeleted = new ArrayList<Integer>();

    if (!fireStationWithAddress.isEmpty()) {

      for (FireStation currentFireStation : fireStationWithAddress) {

        numberStationsDeleted.add(currentFireStation.getNumberStation());
        fireStationService.deleteFireStation(currentFireStation);

      }

      log.info("FireStation with numberStations = {} mapped to address was deleted",
          numberStationsDeleted);

      return new ResponseEntity<>("FireStation with numberStations = {" + numberStationsDeleted
          + "} mapped to address was deleted", HttpStatus.OK);

    } else {

      throw new AddressNotFoundException("There is no FireStation mapped with this address:"
          + address);

    }

  }

}
