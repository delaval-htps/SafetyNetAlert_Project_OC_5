package com.safetynet.alert.service;

import com.safetynet.alert.controller.emergency.EmergencyRestController;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.repository.PersonRepository;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for Entity {@link Person}.
 *
 * @author delaval
 *
 */
@Service
@Log4j2
public class PersonService {

  @Autowired
  private PersonRepository personRepository;

  /**
   * Retrieve a Entity by its Id.
   *
   * @param id
   *            id of type Long not null.
   * @return
   *        the entity with the given Id or Optional.empty() if doesn't exist.
   */
  public Optional<Person> getPersonById(Long id) {

    return personRepository.findById(id);

  }

  /**
   * Retrieve all existed instances of Person.
   *
   * @return  a collection of all existed instances of Person.
   *          a empty collection if there is no instances.
   */
  public Iterable<Person> getPersons() {

    return personRepository.findAll();

  }

  /**
   * retrieve all existed Persons that have given address.
   *
   * @param addressFireStation
   *            address that is mapped with a Firestation.
   *
   * @return    a collection of Persons with the given address.
   *            a empty collection if there is no instances
   *
   */
  public Iterable<Person> getPersonsByAddress(String addressFireStation) {

    return personRepository.getPersonsByAddress(addressFireStation);

  }

  /**
   * Retrieve a Person with Lastname and FirstName given in parameter.
   *
   * @param firstName
   *          the firstName of Person to search
   * @param lastName
   *          the lastName of person to search.
   *
   * @return  the Person with LastName and Firstname if its exists. Optional.empty() if not.
   */
  public Optional<Person> getPersonByNames(String firstName, String lastName) {

    return personRepository.getOneByNames(firstName, lastName);

  }

  /**
   * Retrieve Person with given Id and its mapped FireStation
   * ( idFireStation, numbnerStation, addresses...) because of using a inner join.
   *
   * @param l
   *           l Long Id identification Of Person.
   *
   * @return    Person with all informations of its FireStation.
   *               Optional.empty() if Person doesn't exist.
   */
  public Optional<Person> getPersonJoinFireStationById(long l) {

    return personRepository.getOneJoinFireStationsById(l);

  }

  /**
   * Save a instance of Person.
   *
   * @param person
   *          a instance of Person to save in database.
   *
   * @return  a saved Person with new Id.
   *
   * @throws a InvalidDataAccesApiUsageException if person is null.
   */
  public Person savePerson(Person person) {

    if (person != null) {

      return personRepository.save(person);
    } else {

      log.error("In PersonService.savePerson(person) : person is null!");
      throw new NullPointerException("In PersonService.save(person) : person is null!");
    }

  }

  /**
   * Delete a Person.
   *
   * @param person
   *          the Person to delete.
   */
  public void deletePerson(Person person) {

    if (person != null) {

      personRepository.delete(person);
    } else {

      log.error("In PersonService.deletePerson(person) : person is null!");
      throw new NullPointerException("In PersonService.delete(person) : person is null!");
    }

  }

  /**
   * Retrieve the list of persons mapped with a FireStation with the given station_number.
   * Retrieve also counts:<ul>
   * <li> AdultCount for persons 18 years old or more </li>
   * <li> ChildrenCount for persons 18 years old or less</li>
   * </ul>
   *
   * @param numberStation
   *                the number station of FireStation.
   *
   * @return a Map with result of counts and list of persons
   */
  public Map<String, Object> getPersonsMappedWithFireStation(int numberStation) {

    int adultCount = 0;
    int childrenCount = 0;
    int withoutBirthDate = 0;

    List<Person> persons = personRepository.getPersonsMappedByNumberStation(numberStation);

    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -18);

    for (Person person : persons) {

      if (person.getBirthDate() != null) {

        if (person.getBirthDate().before(cal.getTime())) {

          adultCount++;

        } else {

          childrenCount++;
        }

        person.setBirthDate(null); // to not be displayed in responseBody
      } else {

        withoutBirthDate++;
      }
    }

    Map<String, Object> result = new LinkedHashMap<String, Object>();

    result.put("AdultCount", adultCount);
    result.put("ChildrenCount", childrenCount);

    if (withoutBirthDate > 0) {

      result.put("BirthDateNotSpecified", withoutBirthDate);
    }

    result.put("persons", persons);

    return result;

  }

  /**
   * Retrieve list of children mapped with a given address plus list of other members of home.
   * The two list are ordered by age of Person.
   *
   * @param address   the address given in parameter
   *
   * @return  a Map with two list: children's list and otherMember's list.
   *          Return null if address is not existed.
   */
  public Map<String, List<Person>> getChildrenByAddress(String address) {

    Iterable<Person> persons = personRepository.getChildrenByAddress(address);

    Map<String, List<Person>> result = null;

    List<Person> children = new ArrayList<>();
    List<Person> otherPersons = new ArrayList<>();

    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -18);

    if (persons.iterator().hasNext()) {

      result = new LinkedHashMap<String, List<Person>>();

      for (Person person : persons) {

        if (person.getBirthDate() == null) {

          otherPersons.add(person);
        } else {

          if (person.getBirthDate().after(cal.getTime())) {

            children.add(person);

          } else {

            otherPersons.add(person);
          }
        }
      }
      result.put("children", children);
      result.put("otherMembers", otherPersons);

      return result;

    } else {

      return result;
    }

  }
  //  public Map<String, List<Person>> getChildrenByAddress(String address) {
  //
  //    Map<String, List<Person>> result = null;
  //
  //    Calendar cal = Calendar.getInstance();
  //    cal.add(Calendar.YEAR, -18);
  //
  //    java.sql.Date actualMinus18Years = new java.sql.Date(cal.getTimeInMillis());
  //
  //    List<Tuple> personsOrderByAge =
  //        personRepository.getPersonsCountAgeByAddress2(address, actualMinus18Years);
  //
  //    List<Person> children = new ArrayList();
  //    List<Person> otherPersons = new ArrayList();
  //
  //    long countChildren = (long) personsOrderByAge.get(0).get("childrenCount");
  //
  //    int sizeList = personsOrderByAge.size();
  //
  //    for (int i = 0; i < countChildren; i++) {
  //
  //      children.add(personsOrderByAge.get(i).get("person", Person.class));
  //    }
  //
  //    for (int j = (int) countChildren; j < sizeList; j++) {
  //
  //      otherPersons.add(personsOrderByAge.get(j).get("person", Person.class));
  //    }
  //
  //    result.put("children", children);
  //    result.put("otherPersons", otherPersons);
  //
  //    return result;
  //
  //  }

  /**
   * Retrieve the list of person's phones mapped  with the given FireStation's numberStation
   *
   * @param fireStationNumber   the numberStation of FireStation
   *
   * @return a list of person's phones mapped with the fireStation.
   */
  public List<String> getPhonesByNumberStation(int fireStationNumber) {

    List<String> phones =
        personRepository.getPhonesByNumberStation(fireStationNumber);

    return phones;

  }

  /**
   * Retrieve List of Person living at the given address with their numberStation of FireStation.
   * List contains the lastName, number of phone, age, medications and allergies for each Person.
   *
   * @param address the given address for research all persons living at.
   *
   * @return list of Person living at the address with their informations described before.
   */
  public List<Person>
      getPersonsWhenFireMappedByAddress(String address) {

    return personRepository.getPersonsWhenFire(address);



  }

  /**
   * Retrieve a list of homes(designed with their address), mapped by the given list of
   * NumberStation's FireStations, with all Persons living in it.
   * All numberStations are existed because check by {@link EmergencyRestController}.
   * For each person , list contains lastname, number of phone,age, medications and allergies.
   *
   * @param numberStations  the list of numberstation where research homes.
   *
   * @return a list of homes mapped with fireStation
   *            and with informations (described before) of persons living in.
   */

  public Map<String, List<Person>>
      getPersonsWhenFloodByStations(List<Integer> numberStations) {

    Map<String, List<Person>> result = new LinkedHashMap<>();

    String addressTemp = null;

    for (Integer station : numberStations) {

      List<Person> persons = personRepository.getPersonsWhenFlood(station);

      if (!persons.isEmpty()) {

        addressTemp = persons.get(0).getAddress();
        result.put(addressTemp, new ArrayList<Person>());

        for (Person person : persons) {

          System.out.println("\n persons dans Service\n" + person + "\n");

          if (person.getAddress().equals(addressTemp)) {

            result.get(addressTemp).add(person);
          } else {

            addressTemp = person.getAddress();
            result.put(addressTemp, new ArrayList<Person>());
            result.get(addressTemp).add(person);
          }
        }

      }
    }
    return result;

  }

  //    Map<String, List<PersonDto>> result = new LinkedHashMap<>();
  //
  //    String addressTemp = null;
  //
  //    for (Integer station : numberStations) {
  //
  //      // retrieve the list of "persons (medicalRecord)" with existed medicalRecords
  //      //    mapped with numberStation
  //
  //      List<MedicalRecord> personsWithMedicalRecord =
  //          personRepository.getPersonsWithMedicalRecordWhenFlood(station);
  //
  //      // retrieve the list of persons without MedicalRecord mapped with numberStation
  //
  //      List<PersonDto> personsWithoutMedicalRecord =
  //          personRepository.getPersonsWithoutMedicalRecordWhenFlood(station);
  //
  //      // add lists together casting all in PersonDTO
  //
  //      List<PersonDto> personsMappedByStation = new ArrayList<>();
  //
  //      for (MedicalRecord m : personsWithMedicalRecord) {
  //
  //        personsMappedByStation.add(new PersonDto(m));
  //      }
  //
  //      for (PersonDto p : personsWithoutMedicalRecord) {
  //
  //        personsMappedByStation.add(p);
  //      }
  //
  //      // check if list of PersonDto is not empty and fill result with PersonDto sorting by address
  //
  //      if (!personsMappedByStation.isEmpty()) {
  //
  //        addressTemp = personsMappedByStation.get(0).getAddress();
  //
  //        result.put(addressTemp, new ArrayList<PersonDto>());
  //
  //        for (PersonDto person : personsMappedByStation) {
  //
  //          if (result.containsKey(person.getAddress())) {
  //
  //            result.get(person.getAddress()).add(person);
  //
  //          } else {
  //
  //            addressTemp = person.getAddress();
  //            result.put(addressTemp, new ArrayList<PersonDto>());
  //            result.get(addressTemp).add(person);
  //          }
  //        }
  //      }
  //    }
  //    return result;



  /**
   * Retrieve informations of a Person with given lastname and firstname.
   * in this informations , we have lastName,address,age,email,medications and allergies.
   *
   * @param firstName   the firstName of person to search informations
   *
   * @param lastName    the lastName of person to search informations
   *
   * @return    list of informations of person with given lastname and firstname
   */
  public List<Person> getPersonInfoByNames(String firstName,
      String lastName) {

    return personRepository.getPersonInfoByNames(firstName, lastName);


  }

  /**
   * retrieve all Email of persons living in a city without duplicates.
   *
   * @param city  the city where to search all email.
   *
   * @return list of all email of persons living in the given city
   */
  public List<String> getEmailsByCity(String city) {

    return personRepository.getEmailsByCity(city);

  }



}
