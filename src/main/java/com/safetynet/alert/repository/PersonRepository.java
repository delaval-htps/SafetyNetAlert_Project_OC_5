package com.safetynet.alert.repository;

import com.safetynet.alert.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

}
