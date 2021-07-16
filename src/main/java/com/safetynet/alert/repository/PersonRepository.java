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

  //  @Query("select p as person, "
  //      + " (select  count(*) as children from Person as p"
  //      + " where (p.birthDate >=:actualDate) and p.address =:address) as childrenCount"
  //      + " from Person p"
  //      + " where p.address =:address")
  //  List<Tuple> getPersonsCountAgeByAddress2(String address, Date actualDate);

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

  //  @Query("select distinct mr"
  //      + " from MedicalRecord as mr "
  //      + " left join mr.person as p"
  //      + " left join p.fireStations as f"
  //      + " left join fetch mr.medications as m"
  //      + " left join fetch mr.allergies as a "
  //      + " where f.numberStation=?1 "
  //      + " order by p.address")
  //  @QueryHints(value = @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH,
  //                                 value = "false"))
  //  List<MedicalRecord> getPersonsWithMedicalRecordWhenFlood(int station);
  //
  //  @Query("select distinct new com.safetynet.alert.DTO.PersonDto"
  //      + "(p.firstName,p.lastName,p.birthDate,p.address,p.phone)"
  //      + " from Person as p "
  //      + " left join p.fireStations as f "
  //      + " left join p.medicalRecord as mr"
  //      + " where f.numberStation = :station and mr is null")
  //  @QueryHints(value = @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH,
  //                                 value = "false"))
  //  List<PersonDto> getPersonsWithoutMedicalRecordWhenFlood(int station);

  //****************************** person Info *************************
  @Query("select   p, case p.firstName when :firstName then 1 else 2 end as firstname"
      + " from Person  p"
      + " left join fetch p.medicalRecord mr "
      + " left join fetch mr.medications m"
      + " left join fetch mr.allergies a"
      + " where p.lastName=:lastName"
      + " order by firstname")
  List<Person> getPersonInfoByNames(String firstName, String lastName);
  //  @Query(value = "select  p.*"
  //      + " from person as p"
  //      + " left join  medical_record as mr on mr.id_medical_record= p.id_medical_record"
  //      + " left join attribution_medication as am on am.id_medical_record = mr.id_medical_record"
  //      + " left join attribution_allergy as aa on aa.id_medical_record= mr.id_medical_record"
  //      + " left join medication as m on m.id_medication= am.id_medication"
  //      + " left join allergy as a on a.id_allergy = aa.id_allergy"
  //      + " where p.last_name = :lastName "
  //      + " order by case p.first_name  when :firstName then 1 else 2 end",
  //         nativeQuery = true)
  //  List<Person> getPersonInfoByNames(String firstName, String lastName);

  //****************************** emails  *************************
  @Query("select distinct p.email from Person as p where p.city=?1 order by p.email")
  List<String> getEmailsByCity(String city);



}
