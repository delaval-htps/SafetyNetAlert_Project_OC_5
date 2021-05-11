package com.safetynet.alert.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LoadDatabaseFromJsonImpl implements LoadDatabaseService {

  private final PersonService personService;
  private final FireStationService fireStationService;
  private final MedicalRecordService medicalRecordService;
  private final MedicationService medicationService;
  private final AllergyService allergyService;

  private final ObjectMapper objectMapper;
  private final Resource resource;

  private static Logger logger =
      LoggerFactory.getLogger(LoadDatabaseFromJsonImpl.class);

  @Autowired
  public LoadDatabaseFromJsonImpl(ObjectMapper mapper,
                                  @Value(
                                    "classpath:json/data.json"
                                  ) Resource resource,
                                  PersonService ps,
                                  FireStationService fs,
                                  MedicalRecordService mrs,
                                  MedicationService ms,
                                  AllergyService as) {
    this.objectMapper = mapper;
    this.resource = resource;
    this.allergyService = as;
    this.fireStationService = fs;
    this.medicalRecordService = mrs;
    this.medicationService = ms;
    this.personService = ps;
  }

  @Override
  @Transactional
  public boolean loadDatabaseFromSource() {

    // objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // definie dans application.properties for test

    File fileJson = null;
    try {
      fileJson = resource.getFile();
    } catch (IOException e1) {
      if (e1 instanceof FileNotFoundException) {
        logger.error("File Data.json is not Found in resources");
      } else {
        logger.error("Reading Failure for File Data.json");
      }
      e1.printStackTrace();
      return false;
    }

    JsonNode root = null;
    try {
      root = objectMapper.readTree(fileJson);
    } catch (JsonProcessingException e) {
      logger.error("Json's datas are not valid");
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      logger.error("File Data.json is missing to be parsed");
      e.printStackTrace();
      return false;
    }
    // initilaisation des jsonNode pour chaque Ogject Person,FireStation,MedicalRecord
    if (root != null) {

      JsonNode personArray = root.get("persons");
      JsonNode fireStationArray = root.get("firestations");
      JsonNode medicalRecordArray = root.get("medicalrecords");

      // initialisation des itérators respectifs
      Iterator<JsonNode> personNode = personArray.elements();
      Iterator<JsonNode> fireStationNode = fireStationArray.elements();
      Iterator<JsonNode> medicalRecordNode = medicalRecordArray.elements();

      // save persons

      logger.info("********** sauvegarde des Persons ***********");

      while (personNode.hasNext()) {

        String element = personNode.next().toString();

        try {
          Person person = objectMapper.readValue(element, Person.class);
          personService.savePerson(person);
        } catch (JsonProcessingException e) {
          e.printStackTrace();
          logger.error("problem to parse persons with objectMapper");
          return false;
        }
      }

      // save firestations

      // to avoid duplicate FireStations
      List<Integer> numberStations = new ArrayList<Integer>();

      logger.info("********** save FireStation **********");

      while (fireStationNode.hasNext()) {

        FireStation fireStation = null;
        JsonNode elementFireStation = fireStationNode.next();

        int numberStation = elementFireStation.get("station").asInt();

        if (!numberStations.contains(numberStation)) {
          fireStation = new FireStation();
          fireStation.setNumberStation(numberStation);
          fireStationService.saveFireStation(fireStation);
          numberStations.add(numberStation);

        } else {
          fireStation =
              fireStationService.getFireStationByNumberStation(numberStation);
        }
        String addressFireStation = elementFireStation.get("address").asText();
        Iterable<Person> persons =
            personService.getPersonByAddress(addressFireStation);

        for (Person person : persons) {
          if (person.getAddress().equals(addressFireStation)) {
            fireStation.add(person);
          }
        }
      }

      // to avoid duplicate allergy
      List<String> designationAllergy = new ArrayList<>();

      // to avoid duplicate medication
      List<String> designationPosologies = new ArrayList<>();

      // save medications

      logger.info("********** save MedicalRecord **********");

      while (medicalRecordNode.hasNext()) {

        JsonNode elementMedicalRecord = medicalRecordNode.next();

        MedicalRecord medicalRecord = new MedicalRecord();

        // get person with this medicalrecord

        Person currentPerson =
            personService.getPersonByNames(
                                           elementMedicalRecord.get("firstName")
                                               .asText().toString(),
                                           elementMedicalRecord.get("lastName")
                                               .asText().toString());

        // update birthdate and medicalRecord for person

        String birthDate;
        birthDate = elementMedicalRecord.get("birthdate").asText();

        currentPerson.setBirthDate(birthDate);
        currentPerson.setMedicalRecord(medicalRecord);


        // save medication instance

        logger.info("********** save Medication **********");

        JsonNode medicationArray = elementMedicalRecord.get("medications");
        JsonNode allergyArray = elementMedicalRecord.get("allergies");

        Iterator<JsonNode> medicationElement = medicationArray.elements();
        Iterator<JsonNode> allergyElement = allergyArray.elements();

        while (medicationElement.hasNext()) {

          String designationPosology = medicationElement.next().asText();
          String[] composition = designationPosology.split(":");

          Medication medication = null;

          if (!designationPosologies.contains(designationPosology)) {

            medication = new Medication();
            medication.setDesignation(composition[0]);
            medication.setPosology(composition[1]);

            designationPosologies.add(designationPosology);

            medicationService.saveMedication(medication);

          } else {
            medication = medicationService
                .getMedicationByDesignationAndPosology(composition[0],
                                                       composition[1]);
          }

          medication.add(medicalRecord);
        }

        // save allergies

        logger.info("********** save Allergy **********");

        while (allergyElement.hasNext()) {

          String designation = allergyElement.next().asText();

          Allergy allergy = null;

          if (!designationAllergy.contains(designation)) {

            allergy = new Allergy();
            allergy.setDesignation(designation);

            designationAllergy.add(designation);

            allergyService.saveAllergy(allergy);

          } else {
            allergy = allergyService.getAllergyByDesignation(designation);
          }
          allergy.add(medicalRecord);
        }

        // save medicalRecord
        logger.info("********** save MedicalRecord **********");
        medicalRecordService.saveMedicalRecord(medicalRecord);

      }
      return true;
    }
    return false;
  }
}
