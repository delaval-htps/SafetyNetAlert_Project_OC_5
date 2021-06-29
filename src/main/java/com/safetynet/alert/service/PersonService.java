package com.safetynet.alert.service;

import com.safetynet.alert.model.ModelResponse;
import com.safetynet.alert.model.ModelResponseWithMedicalRecord;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.repository.PersonRepository;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for Entity {@link Person}.
 *
 * @author delaval
 *
 */
@Service
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

    return personRepository.getOneJoinFireStationById(l);

  }

  /**
   * Save a instance of Person.
   *
   * @param person
   *          a instance of Person to save in database.
   *
   * @return  a saved Person with new Id.
   */
  public Person savePerson(Person person) {

    return personRepository.save(person);

  }

  /**
   * Delete a Person.
   *
   * @param person
   *          the Person to delete.
   */
  public void deletePerson(Person person) {

    personRepository.delete(person);

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

    List<Person> persons = personRepository.getPersonsMappedByNumberstation(numberStation);

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
   * @return  a Map with two list: children's list and otherMember's list
   */
  public Map<String, Object> getChildrenByAddress(String address) {

    Iterable<Person> persons = personRepository.getChildrenByAddress(address);

    Map<String, Object> result = null;

    List<Object> children = new ArrayList<>();
    List<Object> otherPersons = new ArrayList<>();

    if (persons.iterator().hasNext()) {

      result = new LinkedHashMap<String, Object>();

      for (Person person : persons) {

        ModelResponse modelPerson = new ModelResponse(person);

        int age = modelPerson.calculateAge(person.getBirthDate());

        if (age <= 18 && age > 0) {

          children.add(modelPerson.getResponseMap());

        } else {

          otherPersons.add(modelPerson.getResponseMap());
        }
      }

      result.put("Children", children);
      result.put("OtherMembers", otherPersons);

      return result;

    } else {

      return result;
    }

  }

  /**
   * Retrieve List of Person living at the given address with their numberStation of FireStation.
   * List contains the lastName, number of phone, age, medications and allergies for each Person.
   *
   * @param address the given address for research all persons living at.
   *
   * @return list of Person living at the address with their informations described before.
   */
  public List<Object> getPersonsWhenFireMappedByAddress(String address) {

    List<Person> persons = personRepository.getPersonsWhenFire(address);

    List<Object> result = new ArrayList<>();

    for (Person person : persons) {

      result.add(new ModelResponseWithMedicalRecord(person).getResponseMap());
    }
    return result;

  }

  /**
   * Retrieve a list of homes(designed with their address), mapped by the given list of
   * NumberStation's FireStations, with all Persons living in it.
   *  For each person , list contains lastname, number of phone,age, medications and allergies.
   *
   * @param numberStations  the list of numberstation where research homes.
   *
   * @return a list of homes mapped with fireStation
   *            and with informations (described before) of persons living in.
   */
  public Map<String, Object> getPersonsWhenFloodByStations(List<Integer> numberStations) {

    Map<String, Object> result = new LinkedHashMap<>();

    List<Object> persons = new ArrayList<>();

    for (Integer station : numberStations) {

      List<Person> personsMappedByStation = personRepository.getPersonsWhenFlood(station);

      String addressTemp = personsMappedByStation.get(0).getAddress();

      persons = new ArrayList<>();

      for (Person person : personsMappedByStation) {

        if (!(person.getAddress().equals(addressTemp))) {

          result.put(addressTemp, persons);
          persons = new ArrayList<>();
          addressTemp = person.getAddress();
        }

        persons.add(new ModelResponseWithMedicalRecord(person).getResponseMap());
      }

      result.put(addressTemp, persons);

    }
    return result;

  }

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
  public List<Object> getPersonInfoByNames(String firstName, String lastName) {

    Iterable<Person> persons = personRepository.getPersonInfoByNames(firstName, lastName);


    List<Object> result = new ArrayList<>();

    for (Person person : persons) {

      result.add(new ModelResponseWithMedicalRecord(person).getResponseMap());
    }
    return result;

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

  /**
   * Method to calculate age of Person with its given Date birhtDate.
   *
   * @param birthDate
   *              the birthDate of a Person
   *
   * @return  age of person in years
   */
  public int calculateAge(Date birthDate) {

    LocalDate birthDateLocalDate = new java.sql.Date(birthDate.getTime()).toLocalDate();
    return Period.between(birthDateLocalDate, LocalDate.now()).getYears();

  }



}
