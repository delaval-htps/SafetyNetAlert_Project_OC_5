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
      + " join fetch p.fireStations f"
      + " join fetch f.addresses"
      + " where p.idPerson=?1")
  Optional<Person> getOneJoinFireStationsById(long l);

  // ******************** /fireStation?stationNumber = ******************
  @Query("SELECT new Person(p.firstName, p.lastName, p.birthDate, p.address, p.phone)"
      + " from Person as p "
      + " Join p.fireStations as f "
      + " Where f.numberStation=?1 ")
  List<Person> getPersonsMappedByNumberStation(int numberStation);

  // ******************** childrenAlert ******************
  @Query("select new Person(p.firstName, p.lastName, p.birthDate)"
      + " from Person as p"
      + " where p.address =?1 order by p.birthDate desc")
  Iterable<Person> getChildrenByAddress(String address);


  // ************************* phone Alert ***************************
  @Query("select distinct p.phone"
      + " from Person p "
      + " join  p.fireStations f"
      + " where f.numberStation = ?1"
      + " order by p.phone")
  List<String> getPhonesByNumberStation(int fireStationNumber);

  //**************** getPersonsWhenFire********************
  @Query("select distinct p"
      + " from Person as p"
      + " left join fetch p.fireStations as f"
      + " left join fetch p.medicalRecord pmr"
      + " left join fetch pmr.medications"
      + " left join fetch pmr.allergies"
      + " where p.address=?1 "
      + "order by p.lastName,p.phone,p.birthDate")
  List<Person> getPersonsWhenFire(String address);

  //********************************* persons FLOOD ****************************

  @Query("select distinct p"
      + " from Person p"
      + " left join fetch p.fireStations f"
      + " left join fetch p.medicalRecord pmr"
      + " left join fetch pmr.medications m"
      + " left join fetch pmr.allergies a"
      + " where f.numberStation =?1"
      + " order by p.address,p.birthDate")
  List<Person> getPersonsWhenFlood(int station);



  //****************************** person Info *************************
  @Query("select   p, case p.firstName when :firstName then 1 else 2 end as firstname"
      + " from Person  p"
      + " left join fetch p.medicalRecord mr "
      + " left join fetch mr.medications m"
      + " left join fetch mr.allergies a"
      + " where p.lastName=:lastName"
      + " order by firstname")
  List<Person> getPersonInfoByNames(String firstName, String lastName);


  //****************************** emails  *************************
  @Query("select distinct p.email from Person as p where p.city=?1 order by p.email")
  List<String> getEmailsByCity(String city);



}
