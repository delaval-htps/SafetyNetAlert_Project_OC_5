package com.safetynet.alert.controller.admin;

import com.safetynet.alert.exceptions.medicalrecord.MedicalRecordAlreadyExistedException;
import com.safetynet.alert.exceptions.medicalrecord.MedicalRecordChangedNamesException;
import com.safetynet.alert.exceptions.medicalrecord.MedicalRecordNotFoundException;
import com.safetynet.alert.exceptions.medicalrecord.MedicalRecordWithIdException;
import com.safetynet.alert.model.Allergy;
import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.model.MedicalRecord;
import com.safetynet.alert.model.Medication;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.AllergyService;
import com.safetynet.alert.service.FireStationService;
import com.safetynet.alert.service.MedicalRecordService;
import com.safetynet.alert.service.MedicationService;
import com.safetynet.alert.service.PersonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Rest controller for entity {@link MedicalRecord}.
 *
 * @author delaval
 *
 */
@RestController
@Api(description = "Api to Manage MedicalRecord")
@RequestMapping("/")
@Log4j2
public class MedicalRecordRestController {

  @Autowired
  MedicalRecordService medicalRecordService;
  @Autowired
  PersonService personService;
  @Autowired
  AllergyService allergyService;
  @Autowired
  MedicationService medicationService;
  @Autowired
  FireStationService fireStationService;

  /**
   * Return the collection of all existed MedicalRecords.
   *
   *@return  a collection of MedicalRecord.
   */
  @GetMapping(value = "/medicalRecord", produces = "application/json")
  @ApiOperation(value = "MedicalRecords",
                notes = "Retrieve all existed MedicalRecords",
                response = MedicalRecord.class)
  public Iterable<MedicalRecord> getMedicalRecords() {

    return medicalRecordService.getMedicalRecords();

  }

  /**
   * Return the MedicalRecord with as identification in database Id of type Long.
   *
   * @param id
   *          the identification of MedicalRecord saved in database.
   *
   * @return  a ResponseEntity with in Body the MedicalRecord found.
   *
   * @throws    a {@link MedicalRecordNotFoundException} if MedicalRecord doesn't exist.
   */
  @GetMapping(value = "/medicalRecord/{id}", produces = "application/json")
  @ApiOperation(value = "MedicalRecord by Id",
                notes = "Retrieve an existed MedicalRecord by given ID",
                response = MedicalRecord.class)
  public ResponseEntity<MedicalRecord> getMedicalRecordById(@Valid @PathVariable Long id) {

    Optional<MedicalRecord> currentMedicalRecord =
        medicalRecordService.getMedicalRecordJoinAllById(id);

    if (currentMedicalRecord.isPresent()) {

      log.info("MedicalRecord with id {} is diplayed in response", id);
      return new ResponseEntity<MedicalRecord>(currentMedicalRecord.get(), HttpStatus.OK);
    } else {

      throw new MedicalRecordNotFoundException("MedicalRecord with id:" + id
          + " was not found!");
    }

  }

  /**
   * Creation of new MedicalRecord given in parameter.
   * All mapping and creation of other entities with a relationship are automatically realized.
   *
   * @param medicalRecord
   *          the representation in Json of the new Object of MedicalRecord to create.
   *
   * @return  a ResponseEntity with in Body the new MedicalRecord and its location URI.
   *
   * @throws    a {@link MedicalRecordAlreadyExistedException} if MedicalRecord already exists
   *            for a Person.
   *
   */
  @PostMapping(value = "/medicalRecord", produces = "application/json")
  @ApiOperation(value = "Create a MedicalRecord",
                notes = "create a MedicalRecord for a Person",
                response = MedicalRecord.class)
  public ResponseEntity<MedicalRecord>
      postMedicalRecord(@Valid @RequestBody MedicalRecord medicalRecord) {

    boolean idPresent = false;

    for (Medication medication : medicalRecord.getMedications()) {

      if (medication.getIdMedication() != null) {

        idPresent = true;
      }
    }

    for (Allergy allergy : medicalRecord.getAllergies()) {

      if (allergy.getIdAllergy() != null) {

        idPresent = true;
      }
    }

    if ((medicalRecord.getIdMedicalRecord() == null)
        && (medicalRecord.getPerson().getIdPerson() == null)
        && (idPresent == false)) {

      //check if MedicalRecord already exist by find
      // if person is already existed cause of relation one to one.
      // We first find person because it can be not mapped with a medicalRecord
      // and in this case we have to create it.
      //If we search only by medicalRecord we can't find this person (not mapped with medicalRecord)

      Optional<Person> existedPerson =
          personService.getPersonByNames(medicalRecord.getPerson().getFirstName(),
              medicalRecord.getPerson().getLastName());

      MedicalRecord savedMedicalRecord = new MedicalRecord();

      if (existedPerson.isPresent()) {

        // case of person is already mapped with MedicalRecord.
        if (existedPerson.get().getMedicalRecord() != null) {

          throw new MedicalRecordAlreadyExistedException("MedicalRecord for this person "
              + "already exist! Please chose another Person to map with");

        } else {

          //case of Person is not mapped to medicalRecord

          // map new medicalRecord to existed Person and save it in database

          medicalRecord.setPerson(existedPerson.get());
          savedMedicalRecord = medicalRecordService.saveMedicalRecord(medicalRecord);

          //map existed Person to the new MedicalRecord and update it

          existedPerson.get().setMedicalRecord(savedMedicalRecord);
          personService.savePerson(existedPerson.get());
        }

      } else {

        // save the new medicalRecord and new Person(Hibernate do it by one to one relationship)
        savedMedicalRecord = medicalRecordService.saveMedicalRecord(medicalRecord);
      }

      // check if given address in person is mapped with fireStation(s).
      List<FireStation> fireStationsMappedToAddress =
          fireStationService.getFireStationsFetchPersonMappedToAddress(
              savedMedicalRecord.getPerson().getAddress());

      if (!fireStationsMappedToAddress.isEmpty()) {

        for (FireStation fireStation : fireStationsMappedToAddress) {

          fireStation.addPerson(savedMedicalRecord.getPerson());
          fireStationService.saveFireStation(fireStation);
        }
      }

      // creation of URI
      URI locationUri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
          .buildAndExpand(savedMedicalRecord.getIdMedicalRecord()).toUri();

      log.info(
          "POST /medicalRecord: Creation of MedicalRecord :{} with Id :{} "
              + "mapped with Person : {}, Medications: {} and Allergies:{}",
          savedMedicalRecord,
          savedMedicalRecord.getIdMedicalRecord(),
          savedMedicalRecord.getPerson(),
          savedMedicalRecord.getMedications(),
          savedMedicalRecord.getAllergies());

      return ResponseEntity.created(locationUri).body(savedMedicalRecord);
    } else {

      throw new MedicalRecordWithIdException("Don't use a Id in body request !");
    }

  }

  /**
   * Update a MedicalRecord with the Id given in parameter.
   * All mapping and creation of other entities with a relationship are automatically realized.
   *
   * @param id
   *          the identification of the MedicalRecord to update in database.
   *
   * @param medicalRecord
   *          the representation of MedicalRecord to update with its new fields in the body in Json.
   *
   * @return  a ResponseEntity with in body the updated MedicalRecord.
   *
   * @throws  a {@link MedicalRecordChangedNamesException}
   *          if in MedicalRecord given in parameter , the couple LastName/FirstName of
   *          Person mapped with MedicalRecord doesn't match.
   *
   * @throws  a {@link MedicalRecordNotFoundException}
   *          if there isn't a existed MedicalRecord with Id given in parameter.
   */
  @PutMapping(value = "/medicalRecord/{id}", produces = "application/json")
  @ApiOperation(value = "Update MedicalRecord",
                notes = "Update a MedicalRecord by it's given ID",
                response = MedicalRecord.class)
  public ResponseEntity<MedicalRecord> putMedicalRecord(@Valid @PathVariable Long id,
      @Valid @RequestBody MedicalRecord medicalRecord) {

    boolean idPresent = false;

    for (Medication medication : medicalRecord.getMedications()) {

      if (medication.getIdMedication() != null) {

        idPresent = true;
      }
    }

    for (Allergy allergy : medicalRecord.getAllergies()) {

      if (allergy.getIdAllergy() != null) {

        idPresent = true;
      }
    }

    if ((medicalRecord.getIdMedicalRecord() == null)
        && (medicalRecord.getPerson().getIdPerson() == null)
        && (idPresent == false)) {

      Optional<MedicalRecord> existedMedicalRecord =
          medicalRecordService.getMedicalRecordJoinAllById(id);

      //check if medicalRecord is present
      if (existedMedicalRecord.isPresent()) {

        MedicalRecord currentMedicalRecord = existedMedicalRecord.get();

        // check if lastName and firstName are the same that in requestBody
        if ((currentMedicalRecord.getPerson().getLastName()
            .equals(medicalRecord.getPerson().getLastName()))
            && (currentMedicalRecord.getPerson().getFirstName()
                .equals(medicalRecord.getPerson().getFirstName()))) {

          // ******************** update Person *************************

          Person currentPerson = currentMedicalRecord.getPerson();

          // if modification of address of Person -> update relationship
          //between Person and FireStation to be sure to respect map fireStation/address
          if (!currentPerson.getAddress().equals(medicalRecord.getPerson().getAddress())) {

            currentPerson.setAddress(medicalRecord.getPerson().getAddress());

            List<FireStation> fireStationMappedToAddress =
                fireStationService
                    .getFireStationsMappedToAddress(medicalRecord.getPerson().getAddress());

            currentPerson.setFireStations(new HashSet<>());

            if (!fireStationMappedToAddress.isEmpty()) {

              currentPerson.addFireStations(fireStationMappedToAddress);
            }
          }
          currentPerson.setBirthDate(medicalRecord.getPerson().getBirthDate());
          currentPerson.setCity(medicalRecord.getPerson().getCity());
          currentPerson.setEmail(medicalRecord.getPerson().getEmail());
          currentPerson.setPhone(medicalRecord.getPerson().getPhone());
          currentPerson.setZip(medicalRecord.getPerson().getZip());

          // ****************** update Medications *****************************

          Set<Medication> currentMedications = currentMedicalRecord.getMedications();

          Set<Medication> medicationsToUpdate =
              medicationsToUpdateBetween(currentMedications, medicalRecord.getMedications());

          log.info("\n Medications to Update = {} \n", medicationsToUpdate);

          currentMedications.clear();
          currentMedications = medicationsToUpdate;
          currentMedicalRecord.setMedications(currentMedications);

          // ****************** update Allergies ****************************
          // same logic business that for medications

          Set<Allergy> currentAllergies = currentMedicalRecord.getAllergies();

          Set<Allergy> allergiesToUpdate =
              allergiesToUpdateBetween(currentAllergies,
                  medicalRecord.getAllergies(),
                  currentMedicalRecord);
          log.info("\n allergies to Update = {} \n", allergiesToUpdate);
          currentAllergies.clear();
          currentAllergies = allergiesToUpdate;
          currentMedicalRecord.setAllergies(currentAllergies);
          log.info("\n Curentallergies ={}\n", currentMedicalRecord.getAllergies());
          //**************** save of MedicalRecord **********************

          MedicalRecord savedMedicalRecord =
              medicalRecordService.saveMedicalRecord(currentMedicalRecord);


          log.info(
              " Update of MedicalRecord with Id {} was successed :"
                  + " all fields was updated ! :{} , {}",
              savedMedicalRecord.getIdMedicalRecord(),
              savedMedicalRecord.getMedications(),
              savedMedicalRecord.getAllergies());

          return new ResponseEntity<MedicalRecord>(savedMedicalRecord, HttpStatus.OK);

        } else {

          throw new MedicalRecordChangedNamesException("Can't change names of person "
              + "in a MedicalRecord! Please don't modify fistName and LastName of the Person");

        }

      } else {

        throw new MedicalRecordNotFoundException("MedicalRecord with id: " + id
            + " was not found ! please chose a existed medicalRecord.");

      }

    } else {

      throw new MedicalRecordWithIdException("Don't use a Id in body request !");
    }


  }

  /**
   * Delete the MedicalRecord with as identification unique couple
   *  FirstName/LastName of Person mapped with this medicalRecord.
   *
   * @param lastName
   *            the lastname of person mapped with the MedicalRecord.
   *
   * @param firstName
   *            the Firstname of Person mapped with the MedicalRecord.
   *
   * @return    a ResponseEntity with the status OK if MedicalRecord was deleted.
   *
   * @Throws    a {@link MedicalRecordNotFoundException}
   *            if couple firstname/lastName doesn't match with any Person.
   */
  @DeleteMapping(value = "/medicalRecord/{lastName}/{firstName}",
                 produces = "application/json")
  @ApiOperation(value = "Delete MedicalRecord",
                notes = "Delete a MedicalRecord by given owner's LastName and FirstName",
                response = MedicalRecord.class)
  public ResponseEntity<?> deleteMedicalRecord(
      @Valid @PathVariable String lastName,
      @Valid @PathVariable String firstName) {

    Optional<MedicalRecord> medicalRecordByNames =
        medicalRecordService.getMedicalRecordByNames(lastName, firstName);

    // check if MedicalRecord is present
    if (medicalRecordByNames.isPresent()) {

      MedicalRecord currentMedicalRecord = medicalRecordByNames.get();

      // update person, medications, allergies linked with MedicalRecord
      currentMedicalRecord.getPerson().setMedicalRecord(null);
      currentMedicalRecord.clearSet(currentMedicalRecord.getMedications());
      currentMedicalRecord.clearSet(currentMedicalRecord.getAllergies());

      medicalRecordService.deleteMedicalRecord(medicalRecordByNames.get());

      log.info("MedicalRecord of Person {} {} was deleted", lastName, firstName);
      return new ResponseEntity<>("MedicalRecord of Person " + lastName + " " + firstName
          + " was deleted", HttpStatus.OK);

    } else {

      throw new MedicalRecordNotFoundException("MedicalRecord was not found "
          + "because lastname and firstname didn't match with anybody: "
          + "Please chose valid couple firstName/LastName");
    }


  }

  /**
   * Method allows,from two Set of Medications of two MedicalRecord, to compare them (like a diff)
   *  and create a new Set with common and new Medications between two sets without duplicates.
   *  And in same time , when found a new non existed Medication( so without a Id),
   *  before put it in the new Set,it saves it in database to retrieve its Id.
   *
   * @param currentMedications
   *              the existed Set of Medication of one MedicalRecord.
   *
   * @param medicationsToUpdate
   *                the new Set of Medication of the other MedicalRecord.
   *
   * @return    a new Set of Medications with common and new Medications
   *             from two Set given in parameter(all Medications have a Id).
   */
  public Set<Medication> medicationsToUpdateBetween(Set<Medication> currentMedications,
      Set<Medication> medicationsToUpdate) {

    Set<Medication> result = new HashSet<Medication>();
    boolean findCommon = false;

    for (Medication medicationToUpdate : medicationsToUpdate) {

      for (Medication currentMedication : currentMedications) {

        if ((medicationToUpdate.getDesignation().equals(currentMedication.getDesignation()))
            && (medicationToUpdate.getPosology().equals(currentMedication.getPosology()))) {

          result.add(currentMedication);
          findCommon = true;
        }
      }

      if (!findCommon) {

        Optional<Medication> existedMedication =
            medicationService.getMedicationByDesignationAndPosology(
                medicationToUpdate.getDesignation(),
                medicationToUpdate.getPosology());

        if (!existedMedication.isPresent()) {

          medicationService.saveMedication(medicationToUpdate);
          result.add(medicationToUpdate);
        } else {

          result.add(existedMedication.get());
        }
      } else {

        findCommon = false;
      }
    }

    return result;

  }

  /**
   * Method allows,from two Set of Allergies of two MedicalRecord, to compare them (like a diff)
   *  and create a new Set with common and new Allergies between two sets without duplicates.
   *  And in same time , when found a new non existed Allergy( so without a Id),
   *  before put it in the new Set,it saves it in database to retrieve its Id.
   *
   * @param currentAllergies
   *              the existed Set of Allergy of one MedicalRecord.
   *
   * @param allergiesToUpdate
   *                the new Set of Allergy of the other MedicalRecord.
   *
   * @return    a new Set of Allergy with common and new allergies
   *             from two Set given in parameter(all allergies have now a Id).
   */
  public Set<Allergy> allergiesToUpdateBetween(Set<Allergy> currentAllergies,
      Set<Allergy> allergiesToUpdate,
      MedicalRecord currentMedicalRecord) {

    Set<Allergy> result = new HashSet<Allergy>();
    boolean findCommon = false;

    for (Allergy allergyToUpdate : allergiesToUpdate) {

      for (Allergy currentAllergy : currentAllergies) {

        if (allergyToUpdate.getDesignation().equals(currentAllergy.getDesignation())) {

          result.add(currentAllergy);
          findCommon = true;
        }
      }

      if (!findCommon) {

        Optional<Allergy> existedAllergy = allergyService.getAllergyByDesignation(
            allergyToUpdate.getDesignation());

        if (!existedAllergy.isPresent()) {

          allergyService.saveAllergy(allergyToUpdate);
          result.add(allergyToUpdate);
        } else {

          result.add(existedAllergy.get());
        }
      } else {

        findCommon = false;
      }
    }

    return result;

  }


}


