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
  Iterable<Person> getPersonsByAddress(String addressFireStation);

  @Query("SELECT p From Person AS p WHERE p.firstName =?1 AND p.lastName=?2")
  Optional<Person> getOneByNames(String firstName, String lastName);

  @Query("SELECT p from Person as p "
      + " Join Fetch p.fireStation"
      + " where p.idPerson=?1")
  Optional<Person> getOneJoinFireStationById(long l);

  @Query("SELECT new Person(p.firstName, p.lastName, p.birthDate, p.address, p.phone) "
      + "from Person as p "
      + "Join p.fireStation as f "
      + "Where f.numberStation=?1 ")
  List<Person> getPersonsMappedByNumberstation(int numberStation);

  @Query("select distinct new Person(p.firstName,p.lastName,p.birthDate,p.phone,p.medicalRecord) "
      + "from Person as p "
      + " left join p.medicalRecord as pmr"
      + " left join pmr.medications as medications "
      + " left join  pmr.allergies as allergies "
      + " where p.address=?1 "
      + " order by p.lastName,p.phone,p.birthDate")
  List<Person> getPersonsWhenFire(String address);

  @Query("select distinct new Person"
      + "(p.firstName,p.lastName,p.address,p.birthDate,p.phone,p.medicalRecord) "
      + "from Person as p "
      + " left join p.medicalRecord as pmr"
      + " left join pmr.medications as medications "
      + " left join  pmr.allergies as allergies "
      + " where p.fireStation.numberStation=?1 "
      + " order by p.address,p.lastName,p.phone,p.birthDate")
  List<Person> getPersonsWhenFlood(int station);

  @Query("select distinct new Person"
      + "(p.firstName,p.lastName,p.birthDate,p.address,p.email,p.medicalRecord) "
      + " from Person as p "
      + " left join p.medicalRecord as pmr"
      + " left join pmr.medications as medications "
      + " left join  pmr.allergies as allergies "
      + " where p.firstName=?1 and p.lastName=?2 "
      + " order by p.address,p.birthDate")
  Iterable<Person> getPersonInfoByNames(String firstName, String lastName);

  @Query("select distinct p.email from Person as p where p.city=?1 order by p.email")
  List<String> getEmailsByCity(String city);

}
