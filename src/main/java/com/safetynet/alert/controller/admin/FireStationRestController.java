package com.safetynet.alert.controller.admin;

import com.safetynet.alert.exceptions.address.AddressNotFoundException;
import com.safetynet.alert.exceptions.firestation.FireStationAlreadyExistedException;
import com.safetynet.alert.exceptions.firestation.FireStationNotFoundException;
import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.service.FireStationService;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
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
@RequestMapping("/")
@Log4j2
public class FireStationRestController {

  @Autowired
  FireStationService fireStationService;

  /**
   * Return all existed FireStation.
   *
   * @return    a collection of all FireStations
   */
  @GetMapping("/firestation")
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
  @GetMapping("/firestation/{id}")
  public ResponseEntity<FireStation> getFireStationById(@PathVariable Long id) {

    Optional<FireStation> fireStation = fireStationService.getFireStationById(id);

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
   * @param fireStation
             a representation in Json of the new Object of FireStation.

   * @return   a ResponseEntity containing in body the FireStation
   *            with its new identification Id and its LocationUri.
   *
   * @throws    a {@link FireStationAlreadyExistedException}
   *            if the FireStation already exists with the given numberStation.
   */
  @PostMapping("/firestation")
  public ResponseEntity<FireStation>
      postMappingStationAddress(@Valid @RequestBody FireStation fireStation) {

    Optional<FireStation> existedFireStation =
        fireStationService.getFireStationByNumberStation(fireStation.getNumberStation());

    if (!existedFireStation.isPresent()) {

      FireStation savedFireStation = fireStationService.saveFireStation(fireStation);

      URI locationUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
          .buildAndExpand(savedFireStation.getIdFireStation()).toUri();

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
  @PutMapping("/firestation/{address}")
  public ResponseEntity<Iterable<FireStation>> putMappingNumberStationAddress(
      @Valid @PathVariable String address,
      @Valid @RequestBody FireStation fireStationToMapWithAddress) {

    List<FireStation> bodyResponse = new ArrayList<FireStation>();

    //check if given body request fireStation exist

    Optional<FireStation> fireStationWithNumberStation = fireStationService
        .getFireStationByNumberStation(fireStationToMapWithAddress.getNumberStation());


    if (fireStationWithNumberStation.isPresent()) {

      FireStation existedFireStation = fireStationWithNumberStation.get();

      //check validation of all field for given fireStationToMapWithAddress
      if ((existedFireStation.getNumberStation() == fireStationToMapWithAddress
          .getNumberStation())
          && (existedFireStation.getIdFireStation() == fireStationToMapWithAddress
              .getIdFireStation())
          && (existedFireStation.getAddresses()
              .equals(fireStationToMapWithAddress.getAddresses()))) {

        if (existedFireStation.getAddresses().contains(address)) {

          throw new FireStationAlreadyExistedException("This FireStation "
              + "already mapped with given address."
              + "Please give a another fireStation to map !");
        } else {

          //check for firestation mapped with the address
          Optional<FireStation> fireStationMappedWithAddress =
              fireStationService.getFireStationMappedToAddress(address);

          if (fireStationMappedWithAddress.isPresent()) {

            //delete address from FireStationMAppedWithAddress
            FireStation fireStationToDeleteAddress = fireStationMappedWithAddress.get();
            fireStationToDeleteAddress.getAddresses().remove(address);
            fireStationService.saveFireStation(fireStationToDeleteAddress);
            bodyResponse.add(fireStationToDeleteAddress);
          }

          // add address to fireStationToMapWithAddress and save it .
          existedFireStation.getAddresses().add(address);
          fireStationService.saveFireStation(existedFireStation);

          bodyResponse.add(existedFireStation);
          return new ResponseEntity<Iterable<FireStation>>(bodyResponse, HttpStatus.OK);
        }


      } else {

        throw new FireStationNotFoundException("fireStation in body request"
            + " doesn't match with a existed fireStation !"
            + " Check fields are correctly entered");
      }

    } else {

      throw new FireStationNotFoundException(
                                             "fireStation given in body request with numberStation:"
                                                 + fireStationToMapWithAddress
                                                     .getNumberStation()
                                                 + " doesn't exist !");

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
  @DeleteMapping("/firestation/station/{numberStation}")
  public ResponseEntity<FireStation>

      deleteMappingFireStation(@Valid @PathVariable int numberStation) {

    Optional<FireStation> fireStationWithNumberStation =
        fireStationService.getFireStationByNumberStation(numberStation);

    if (fireStationWithNumberStation.isPresent()) {

      FireStation currentFireStation = fireStationWithNumberStation.get();

      currentFireStation.setAddresses(new HashSet<String>());

      fireStationService.saveFireStation(currentFireStation);

      return new ResponseEntity<FireStation>(currentFireStation, HttpStatus.OK);

    } else {

      throw new FireStationNotFoundException(
                                             "FireStation with NumberStation:" + numberStation
                                                 + " was not found");

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
  @DeleteMapping("/firestation/address/{address}")
  public ResponseEntity<FireStation>
      deleteAddressFromFireStations(@PathVariable @Valid String address) {

    Optional<FireStation> fireStationWithAddress =
        fireStationService.getFireStationMappedToAddress(address);

    if (fireStationWithAddress.isPresent()) {

      FireStation currentFireStation = fireStationWithAddress.get();

      currentFireStation.getAddresses().remove(address);


      fireStationService.saveFireStation(currentFireStation);


      return new ResponseEntity<FireStation>(currentFireStation, HttpStatus.OK);

    } else {

      throw new AddressNotFoundException("There is no FireStation mapped with this address:"
          + address);

    }

  }

}
