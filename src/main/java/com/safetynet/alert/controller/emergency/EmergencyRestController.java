package com.safetynet.alert.controller.emergency;

import com.safetynet.alert.exceptions.address.AddressNotFoundException;
import com.safetynet.alert.exceptions.firestation.FireStationNotFoundException;
import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.FireStationService;
import com.safetynet.alert.service.PersonService;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for emergencies' services.
 *
 * @author delaval
 *
 */
@RestController
public class EmergencyRestController {

  @Autowired
  private PersonService personService;

  @Autowired
  private FireStationService fireStationService;

  /**
   * Retrieve the list of persons mapped with a FireStation with the given station_number.
   * Retrieve also counts:<ul>
   * <li> AdultCount for persons 18 years old or more </li>
   * <li> ChildrenCount for persons 18 years old or less</li>
   * </ul>
   *
   * @param stationNumber
   *            the numberStation of fireStation.
   *
   * @return     a ResponseEntity with as body a Map with Key/Value:
   *            <ul>
   *            <li> AdultCount/adultCount </li>
   *            <li> ChildrenCount/childrenCount</li><
   *            <li> Persons/ List of persons mapped with FireStation</li>
   *            </ul>
   */
  @GetMapping("/firestation/getpersons")
  public ResponseEntity<?>
      getPersonsMappedWithFireStation(
          @RequestParam(name = "stationNumber") int stationNumber) {

    Optional<FireStation> existedfireStation =
        fireStationService.getFireStationByNumberStation(stationNumber);

    if (existedfireStation.isPresent()) {

      Map<String, Object> personsMappedWithFireStation =
          personService.getPersonsMappedWithFireStation(stationNumber);

      return new ResponseEntity<>(personsMappedWithFireStation, HttpStatus.OK);

    } else {

      throw new FireStationNotFoundException("FireStation with number_station: "
          + stationNumber + " was not found! please choose another existed one!");
    }

  }

  /**
   * Retrieve the list of children Mapped with given address ,
   * and separated list of other members of home.
   *
   * @param address the address where children live
   *
   * @return        a ResponseEntity with list of children and a list of other members.
   *                If there is no children for this address return a empty list for children.
   *
   * @throws        a {@link AddressNotFoundException} if address is not found.
   */
  @GetMapping("/childAlert")
  public ResponseEntity<?> getChildAlert(@RequestParam(name = "address") String address) {

    Map<String, Object> childrenByAddress = personService.getChildrenByAddress(address);

    if (childrenByAddress != null) {

      return new ResponseEntity<>(childrenByAddress, HttpStatus.OK);

    } else {

      throw new AddressNotFoundException("this address : " + address
          + " was not found. Please choose a existed address");
    }

  }

  /**
   * Retrieve a list of phone of persons mapped with fireStation designed by given numberStation.
   *
   * @param fireStationNumber the number station of fireStation.
   *
   * @return  a ResponseEntity with a list of phones sorted and unique.
   */
  @GetMapping("/phoneAlert")
  public ResponseEntity<?>
      getPhoneAlert(@RequestParam(name = "firestation") int fireStationNumber) {

    Optional<FireStation> existedFireStation =
        fireStationService.getFireStationByNumberStation(fireStationNumber);

    SortedSet<String> phones = new TreeSet<>();

    if (existedFireStation.isPresent()) {

      for (Person person : existedFireStation.get().getPersons()) {

        phones.add(person.getPhone());

      }

      Map<String, Object> result = new LinkedHashMap<>();
      result.put("Phones", phones);

      return new ResponseEntity<>(result, HttpStatus.OK);
    } else {

      throw new FireStationNotFoundException("FireStation with numberStation: "
          + fireStationNumber + " was not found. Please choose a existed fireStation!");
    }

  }
}
