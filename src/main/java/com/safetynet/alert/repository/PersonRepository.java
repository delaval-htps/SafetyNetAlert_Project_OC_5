package com.safetynet.alert.repository;

import com.safetynet.alert.model.Person;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

  @Query("SELECT p FROM Person AS p WHERE p.address= ?1")
  Iterable<Person> getOneByAddress(String addressFireStation);

  @Query("SELECT p From Person AS p WHERE p.firstName =?1 AND p.lastName=?2")
  Optional<Person> getOneByNames(String firstName, String lastName);

}
