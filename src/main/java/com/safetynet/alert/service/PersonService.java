package com.safetynet.alert.service;

import com.safetynet.alert.model.Person;
import com.safetynet.alert.repository.PersonRepository;
import java.util.Calendar;
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
  public Iterable<Person> getPersonByAddress(String addressFireStation) {

    return personRepository.getOneByAddress(addressFireStation);

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

    List<Person> persons = personRepository.getPersonsMappedByNumberstation(numberStation);

    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -18);

    for (Person person : persons) {

      if (person.getBirthDate().before(cal.getTime())) {

        adultCount++;
      } else {

        childrenCount++;
      }

      person.setBirthDate(null); // to not be displayed in responseBody
    }

    Map<String, Object> result = new LinkedHashMap<String, Object>();

    result.put("AdultCount", adultCount);
    result.put("ChildrenCount", childrenCount);
    result.put("persons", persons);

    return result;

  }
}
