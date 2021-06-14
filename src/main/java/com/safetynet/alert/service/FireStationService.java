package com.safetynet.alert.service;

import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.repository.FireStationRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.validation.Valid;

/**
 * Service for Entity FireStation.
 *
 * @author delaval
 *
 */
@Service
public class FireStationService {
  @Autowired
  private FireStationRepository fireStationRepository;

  /**
   * return FireStation with given id.
   *
   *@param id  id of fireStation that we search
   *
   *@return Optional&lsaquo;FireStation&rsaquo; FireStation with given id
   */
  public Optional<FireStation> getFireStationById(Long id) {

    return fireStationRepository.findById(id);

  }

  /**
   * return the list of all existed fireStation.
   * If there is no fireStation return a empty Iterable.
   *
   * @return    Iterable&lsaquo;FireStation&rsaquo;  the collection of all FireStation
   */
  public Iterable<FireStation> getFireStations() {

    return fireStationRepository.findAll();

  }

  /**
   * return existed fireStation with the numberStation given in parameter
   * If there is no fireStation return a empty Optional(not null).
   *
   * @param  numberStation   the numberStation of FireStation to retrieve
   * @return    Optional&lsaquo;FireStation&rsaquo;  FireStation with this numberStation
   */
  public Optional<FireStation> getFireStationByNumberStation(
      int numberStation) {

    return fireStationRepository.getOneByNumberStation(numberStation);

  }

  /**
   * return existed fireStation mapped by address given in parameter
   * If there is no fireStation return a empty Optional(not null).
   *
   * @param  address
   *            address mapped with FireStation to retrieve
   * @return    Optional&lsaquo;FireStation&rsaquo;
   *                FireStation mapped by address given in parameter
   */
  public Optional<FireStation> getFireStationMappedToAddress(
      @Valid String address) {

    return fireStationRepository.getOneByAddress(address);

  }

  /**
   * save a FireStation given in parameter.
   *
   * @param  fireStation   FireStation to save
   * @return    FireStation Saved FireStation
   */
  public FireStation saveFireStation(FireStation fireStation) {

    return fireStationRepository.save(fireStation);

  }

  /**
   * retrieve fireStation with id in parameter but with all it's fields du to a inner join
   * To not have a lazyInitializationException with Hibernate.
   *
   * @param l   l'Id of FireStation
   *
   * @return  Optional firestation
   *          if fireStation present return firestation else a empty Optional Firestation
   */
  public Optional<FireStation> getFireStationJoinAllById(long l) {

    return fireStationRepository.getOneJoinAllById(l);

  }


}
