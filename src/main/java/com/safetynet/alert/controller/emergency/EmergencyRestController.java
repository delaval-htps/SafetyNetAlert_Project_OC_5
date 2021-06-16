package com.safetynet.alert.controller.emergency;

import com.safetynet.alert.exceptions.firestation.FireStationNotFoundException;
import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.service.FireStationService;
import com.safetynet.alert.service.PersonService;
import java.util.Map;
import java.util.Optional;
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
   *            <li> Persons/ List of persons mapped with Firestation</li>
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
}
