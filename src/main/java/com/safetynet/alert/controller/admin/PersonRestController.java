package com.safetynet.alert.controller.admin;

import com.safetynet.alert.exceptions.person.PersonAlreadyExistedException;
import com.safetynet.alert.exceptions.person.PersonChangedNamesException;
import com.safetynet.alert.exceptions.person.PersonNotFoundException;
import com.safetynet.alert.exceptions.person.PersonWithIdException;
import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.FireStationService;
import com.safetynet.alert.service.PersonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.URI;
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
 * Rest Controller for entity {@link Person}.
 *
 * @author delaval
 *
 */
@RestController
@Api(description = "API to Manage Person")
@RequestMapping("/")
@Log4j2
public class PersonRestController {

  @Autowired
  private PersonService personService;

  @Autowired
  private FireStationService fireStationService;

  /**
   * Return all existed Persons.
   *
   * @return    a collection (Iterable) of all existed Persons.
   */

  @GetMapping(value = "/person", produces = "application/json")
  @ApiOperation(value = "Persons",
                notes = "Retrieve all existed Persons",
                response = Person.class)
  public Iterable<Person> getPersons() {

    return personService.getPersons();

  }

  /**
   * Return the existed Person with as identification a Id of type Long.
   *
   * @param id
   *          the identification of the Person in database.
   *
   * @return  a ResponseEntity with in body the existed Person.
   *
   * @throws  a {@link PersonNotFoundException} if there isn't Person with this Id.
   */

  @GetMapping(value = "/person/{id}", produces = "application/json")
  @ApiOperation(value = "Person with ID",
                notes = "Retrieve an existed Person with it's given ID",
                response = Person.class)
  public ResponseEntity<Person> getPersonById(@PathVariable Long id) {

    Optional<Person> person = personService.getPersonById(id);

    if (person.isPresent()) {

      log.info("Person with id {} was found and show in body response",
          id);
      return new ResponseEntity<Person>(person.get(),
                                        HttpStatus.OK);

    } else {

      throw new PersonNotFoundException("Unable to found a person with id:"
          + id);

    }

  }


  /**
   * Creation of new Person.
   *
   * @param personToAdd
   *              representation in json of new Object of Person to save.
   *
   * @return  a ResponseEntity with in body the new Person and its location URI.
   *
   * @throws    a {@link PersonAlreadyExistedException}
   *            if the person with LastName/FirstName given in personToAdd already exists.
   */

  @PostMapping(value = "/person", produces = "application/json")
  @ApiOperation(value = "Create a new Person",
                response = Person.class)
  public ResponseEntity<Person> postPerson(
      @Valid
      @RequestBody Person personToAdd) {

    if (personToAdd.getIdPerson() == null) {

      Optional<Person> existedPerson =
          personService.getPersonByNames(personToAdd.getFirstName(),
              personToAdd.getLastName());

      if (!existedPerson.isPresent()) {

        Person savedPerson = personService.savePerson(personToAdd); // save new Person

        //check if address of personToAdd have a address already mapped with a fireStation

        List<FireStation> fireStationMappedToAddress =
            fireStationService.getFireStationsMappedToAddress(personToAdd.getAddress());

        if (!fireStationMappedToAddress.isEmpty()) {

          savedPerson.addFireStations(fireStationMappedToAddress);
          personService.savePerson(savedPerson); // update of savedPerson to add firestations
        }

        URI locationUri = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedPerson.getIdPerson())
            .toUri();

        log.info("POST /person: Creation of Person {} sucessed with the locationId {}",
            savedPerson,
            locationUri.getPath());

        return ResponseEntity.created(locationUri).body(savedPerson);
      } else {

        Person currentPerson = existedPerson.get();
        throw new PersonAlreadyExistedException("this Person with firstname:"
            + currentPerson.getFirstName() + " and lastname:" + currentPerson.getLastName()
            + " already exist ! Can't add an already existed Person!");
      }
    } else {

      throw new PersonWithIdException("Don't need an id for Person to save it!");
    }

  }

  /**
   * Update a existed Person with identification Id given in parameter.
   *
   * @param id
   *          the identification of existed Person.
   *
   * @param updatedPerson
   *          the representation of Person once updated.
   *
   * @return  a ResponseEntity with in body the updated saved Person.
   *
   * @throws PersonNotFoundException
   *          if Person to update with Id is not found.
   *
   * @throws PersonChangedNamesException
   *          if updatedPerson has modify couple of LastName/FirstName.
   *
   */

  @PutMapping(value = "/person/{id}", produces = "application/json")
  @ApiOperation(value = "Update an existed Person by giving it's ID", response = Person.class)
  public ResponseEntity<Person> putPerson(
      @PathVariable Long id,
      @RequestBody
      @Valid Person updatedPerson)
      throws PersonNotFoundException, PersonChangedNamesException {

    if (updatedPerson.getIdPerson() == null) {

      Optional<Person> personToUpdate = personService.getPersonById(id);

      if (personToUpdate.isPresent()) {

        Person currentPerson = personToUpdate.get();
        String lastAddress = currentPerson.getAddress(); // save last address

        if (updatedPerson.getFirstName()
            .equals(currentPerson.getFirstName())
            && updatedPerson.getLastName()
                .equals(currentPerson.getLastName())) {

          currentPerson.setBirthDate(updatedPerson.getBirthDate());

          currentPerson.setAddress(updatedPerson.getAddress());

          currentPerson.setCity(updatedPerson.getCity());

          currentPerson.setZip(updatedPerson.getZip());

          currentPerson.setPhone(updatedPerson.getPhone());

          currentPerson.setEmail(updatedPerson.getEmail());

          Person savedPerson = personService.savePerson(currentPerson);

          //If Person.address change then need to map it with another fireStation if it exists
          if (!lastAddress.equals(savedPerson.getAddress())) {

            //check if address of updatedPerson have a address already mapped with a fireStation

            List<FireStation> fireStationMappedToAddress =
                fireStationService.getFireStationsMappedToAddress(savedPerson.getAddress());

            if (!fireStationMappedToAddress.isEmpty()) {

              //update fireStations for savedPerson
              savedPerson.clearFireStations(); // need to clear last fireStations mapped
              savedPerson.addFireStations(fireStationMappedToAddress);
              personService.savePerson(savedPerson);

            }
          }

          log.info("Person with id {} was correctly updated", id);

          return new ResponseEntity<Person>(currentPerson, HttpStatus.OK);

        } else {

          throw new PersonChangedNamesException("When updating a person with id:"
              + id + " you can't change names");
        }

      } else {

        throw new PersonNotFoundException("Person to update with id: "
            + id + " was not found");
      }
    } else {

      throw new PersonWithIdException("Don't need an id for Person to save it!");
    }

  }

  /**
   * Delete a existed Person with as identification couple FirstName/LastName.
   *
   * @param lastName
   *            lastName of existed Person to delete
   * @param firstName
   *            firstName of existed Person to delete.
   * @return    a ResponseEntity with status Ok if person was deleted.
   *
   * @throws PersonNotFoundException
   *          if Person to delete with Identification couple lastName/firstName is not found.
   */

  @DeleteMapping("/person/{lastName}/{firstName}")
  @ApiOperation(value = "Delete an existed Person by giving it's LastName and FirstName")
  public ResponseEntity<?> deletePerson(@PathVariable String lastName,
      @PathVariable String firstName) {

    Optional<Person> personToDelete = personService.getPersonByNames(firstName, lastName);

    if (personToDelete.isPresent()) {

      //For MedicalRecord relation 1:1 but in Cascade.ALL so:
      //    when delete Person, hibernate delete MedicalRecord
      //    update junction table with allergy and medication

      //For FireStation, as we delete Person, hibernate automatically
      //    remove this person in mapped fireStation without deleting fireStation
      //    cause of relation M:1 in Cascade without DELETE even if fetch.LAZY
      personService.deletePerson(personToDelete.get());

      log.info(
          "DELETE \"/person/{lastName}/{firstName}\" :"
              + " Person with lastName: {} and firstName: {} was successed",
          lastName,
          firstName);
      return new ResponseEntity<>(HttpStatus.OK);

    } else {

      throw new PersonNotFoundException("Deleting Person with lastName: " + lastName
          + " and FirstName: "
          + firstName + " was not Found");

    }

  }

}
