package com.safetynet.alert.service;

import com.safetynet.alert.model.Person;
import com.safetynet.alert.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

  @Autowired
  private PersonRepository personRepository;

  public Person getPersonById(Long id) {
    return personRepository.getOne(id);
  }

  public Iterable<Person> getPersons() {
    return personRepository.findAll();
  }

  public Person savePerson(Person dorian) {
    return personRepository.save(dorian);
  }

}
