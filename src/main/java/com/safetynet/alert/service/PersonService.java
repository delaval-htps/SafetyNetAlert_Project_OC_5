package com.safetynet.alert.service;

import com.safetynet.alert.model.Person;
import com.safetynet.alert.repository.PersonRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

  @Autowired
  private PersonRepository personRepository;

  public Optional<Person> getPersonById(Long id) {

    return personRepository.findById(id);

  }

  public Iterable<Person> getPersons() {

    return personRepository.findAll();

  }

  public Person savePerson(Person person) {

    return personRepository.save(person);

  }

  public Iterable<Person> getPersonByAddress(String addressFireStation) {

    return personRepository.getOneByAddress(addressFireStation);

  }

  public Optional<Person> getPersonByNames(String firstName, String lastName) {

    return personRepository.getOneByNames(firstName, lastName);

  }

  public Optional<Person> getPersonJoinFireStationById(long l) {

    return personRepository.getOneJoinFireStationById(l);

  }

  public void deletePerson(Person person) {

    personRepository.delete(person);

  }

}
