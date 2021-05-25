package com.safetynet.alert.controller.admin;

import com.safetynet.alert.exceptions.person.PersonChangedNamesException;
import com.safetynet.alert.exceptions.person.PersonNotFoundException;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.PersonService;
import java.net.URI;
import java.util.Optional;
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
import javax.validation.Valid;

@RestController
@RequestMapping("/")
@Log4j2
public class PersonRestController {

  @Autowired
  private PersonService personService;

  @GetMapping("/person")
  public Iterable<Person> getPersons() {

    return personService.getPersons();

  }

  @GetMapping("/person/{id}")
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

  @PostMapping("/person")
  public ResponseEntity<Person> postPerson(@Valid
  @RequestBody Person personToAdd) {

    Person savedPerson = personService.savePerson(personToAdd);

    // return of savePerson will be never null so don't be obliged to check :
    // if (savedPerson == null) {
    // return ResponseEntity.noContent().build();
    // } else {

    URI locationUri =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedPerson.getIdPerson())
            .toUri();
    log.info("POST /person: Creation of Person {} sucessed with the locationId {}",
        savedPerson,
        locationUri.getPath());
    return ResponseEntity.created(locationUri)
        .body(savedPerson);

  }


  @PutMapping("/person/{id}")
  public ResponseEntity<Person> putPerson(@PathVariable Long id,
      @RequestBody
      @Valid Person updatedPerson)
      throws PersonNotFoundException,
      PersonChangedNamesException {

    Optional<Person> personToUpdate = personService.getPersonById(id);

    if (personToUpdate.isPresent()) {

      Person currentPerson = personToUpdate.get();

      if (updatedPerson.getFirstName()
          .equals(currentPerson.getFirstName())
          && updatedPerson.getLastName()
              .equals(currentPerson.getLastName())) {

        // a verifier mais comment faire pour changer la fireStation si
        // l'address change la fireStation aussi normalement?...

        // avec le @Valid dans le RequestBody , hibernate fait la verification
        // de savoir si adress est non null donc pour moi il est inutile de
        // refaire un check avec un if ...
        // if (address != null) {
        currentPerson.setBirthDate(updatedPerson.getBirthDate());

        currentPerson.setAddress(updatedPerson.getAddress());

        currentPerson.setCity(updatedPerson.getCity());

        currentPerson.setZip(updatedPerson.getZip());

        currentPerson.setPhone(updatedPerson.getPhone());

        currentPerson.setEmail(updatedPerson.getEmail());

        personService.savePerson(currentPerson);
        log.info("Person with id {} was correctly updated",
            id);
        return new ResponseEntity<>(currentPerson,
            HttpStatus.OK);

      } else {

        // log.error("unable to update person with {} "
        // + "because of names are not the same in the body request", id);
        throw new PersonChangedNamesException("When updating a person with id:"
            + id + " you can't change names");

      }

    } else {

      // log.error("unable to update person:{} with id {} because he was not
      // found in database",
      // id, updatedPerson);
      throw new PersonNotFoundException("PUT \"/person/{id}\" : Person to update with id: "
          + id + " was not found");

    }

  }

  @DeleteMapping("/person/{lastName}/{firstName}")
  public ResponseEntity deletePerson(@PathVariable String lastName,
      @PathVariable String firstName) {

    Optional<Person> personToDelete =
        personService.getPersonByNames(firstName,
            lastName);

    if (personToDelete.isPresent()) {

      personService.deletePerson(personToDelete.get());
      log.info(
          "DELETE \"/person/{lastName}/{firstName}\" : Person with lastName: {} and firstName: {} was successed",
          lastName,
          firstName);
      return new ResponseEntity<>(HttpStatus.OK);

    } else {

      // log.error("Deleting Person with lastName {} and FirstName {} was not
      // Found",
      // lastName, firstName);
      throw new PersonNotFoundException(
          "DELETE \"/person/{lastName}/{firstName}\" :Deleting Person with lastName: "
              + lastName + " and FirstName: " + firstName + " was not Found");

    }

  }

}
