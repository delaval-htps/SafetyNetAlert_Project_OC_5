package com.safetynet.alert.controller.admin;

import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonRestcontroller {
  @Autowired
  private PersonService personService;

  @GetMapping("/persons")
  public Iterable<Person> getPersons() {
    return personService.getPersons();
  }
}
