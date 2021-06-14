package com.safetynet.alert.controller.admin;

import com.safetynet.alert.exceptions.medicalrecord.MedicalRecordAlreadyExistedException;
import com.safetynet.alert.exceptions.medicalrecord.MedicalRecordChangedNamesException;
import com.safetynet.alert.exceptions.medicalrecord.MedicalRecordNotFoundException;
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
import java.net.URI;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
import javax.validation.Valid;

@RestController
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

  @GetMapping("/medicalRecord")
  public Iterable<MedicalRecord> getMedicalRecords() {

    return medicalRecordService.getMedicalRecords();

  }

  @GetMapping("/medicalRecord/{id}")
  public ResponseEntity<MedicalRecord> getMedicalRecords(@Valid @PathVariable Long id) {

    Optional<MedicalRecord> currentMedicalRecord =
        medicalRecordService.getMedicalRecordById(id);

    if (currentMedicalRecord.isPresent()) {

      return new ResponseEntity<MedicalRecord>(currentMedicalRecord.get(), HttpStatus.OK);
    } else {

      throw new MedicalRecordNotFoundException("MedicalRecord with id:" + id
          + " was not found!");
    }

  }

  @PostMapping("/medicalRecord")
  public ResponseEntity<MedicalRecord>
      postMedicalRecord(@Valid @RequestBody MedicalRecord medicalRecord) {

    //check if MedicalRecord already exist
    //by find if person is already existed cause of relation one to one
    Optional<Person> existedPerson =
        personService.getPersonByNames(medicalRecord.getPerson().getFirstName(),
            medicalRecord.getPerson().getLastName());

    MedicalRecord savedMedicalRecord = new MedicalRecord();

    if (existedPerson.isPresent()) {

      if (existedPerson.get().getMedicalRecord() != null) {

        throw new MedicalRecordAlreadyExistedException("MedicalRecord for this person "
            + "already exist! Please chose another Person to map with");

      } else {

        //map new medicalRecord to existed Person and save it in database
        //  if we don't setPerson to medicalRecord:
        //  hibernate create a new person (so duplicate!) and map this new to medicalRecord
        medicalRecord.setPerson(existedPerson.get());
        savedMedicalRecord = medicalRecordService.saveMedicalRecord(medicalRecord);

        //map existed Person to the new MedicalRecord and update it
        existedPerson.get().setMedicalRecord(savedMedicalRecord);
        personService.savePerson(existedPerson.get());
      }

    } else {

      // save the new medicalRecord and the new Person ( Hibernate do it by one to one relationship)
      savedMedicalRecord = medicalRecordService.saveMedicalRecord(medicalRecord);
    }

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

  }

  @PutMapping("/medicalRecord/{id}")
  public ResponseEntity<MedicalRecord> putMedicalRecord(@Valid @PathVariable Long id,
      @Valid @RequestBody MedicalRecord medicalRecord) {

    Optional<MedicalRecord> existedMedicalRecord =
        medicalRecordService.getMedicalRecordById(id);

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

          Optional<FireStation> fireStationMappedToAddress =
              fireStationService.getFireStationMappedToAddress(currentPerson.getAddress());

          if (fireStationMappedToAddress.isPresent()) {

            currentPerson.setFireStation(fireStationMappedToAddress.get());
          } else {

            currentPerson.setFireStation(null);
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
            AllergiesToUpdateBetween(currentAllergies,
                medicalRecord.getAllergies(),
                currentMedicalRecord);
        log.info("\n allergies to Update = {} \n", allergiesToUpdate);
        currentAllergies.clear();
        currentAllergies = allergiesToUpdate;
        currentMedicalRecord.setAllergies(currentAllergies);
        log.info("\n Cuurentallergies ={}\n", currentMedicalRecord.getAllergies());
        //**************** save of MedicalRecord **********************

        MedicalRecord savedMedicalRecord =
            medicalRecordService.saveMedicalRecord(currentMedicalRecord);


        log.info(
            " Update of MedicalRecord with Id {} was successed : all fields was updated ! :{} , {}",
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

  }

  @DeleteMapping("/medicalRecord/{lastName}/{firstName}")
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
      return new ResponseEntity<MedicalRecord>(HttpStatus.OK);

    } else {

      throw new MedicalRecordNotFoundException("MedicalRecord was not found "
          + "because lastname and firstname didn't match with anybody: "
          + "Please chose valid couple firstName/LastName");
    }


  }

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

  public Set<Allergy> AllergiesToUpdateBetween(Set<Allergy> currentAllergies,
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


