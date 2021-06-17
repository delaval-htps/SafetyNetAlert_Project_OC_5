package com.safetynet.alert.repository;

import com.safetynet.alert.model.Person;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository for Entity Person.
 *
 * @author delaval
 *
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

  @Query("SELECT p FROM Person AS p WHERE p.address= ?1")
  Iterable<Person> getOneByAddress(String addressFireStation);

  @Query("SELECT p From Person AS p WHERE p.firstName =?1 AND p.lastName=?2")
  Optional<Person> getOneByNames(String firstName, String lastName);

  @Query("SELECT p from Person as p "
      + " Join Fetch p.fireStation"
      + " where p.idPerson=?1")
  Optional<Person> getOneJoinFireStationById(long l);

  @Query("SELECT new Person(p.firstName, p.lastName, p.address, p.phone, p.birthDate) "
      + "from Person as p "
      + "Join p.fireStation as f "
      + "Where f.numberStation=?1 and p.birthDate <= current_date")
  List<Person> getPersonsMappedByNumberstation(int numberStation);

}
