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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class implements LoadDataStrategy that is used to load data from another file.json for testing.
 *
 * @author delaval
 *
 */
@Service
@Log4j2
public class LoadDatabaseTestFromJson implements LoadDataStrategy {

  @Autowired
  private PersonService personService;
  @Autowired
  private FireStationService fireStationService;
  @Autowired
  private MedicalRecordService medicalRecordService;
  @Autowired
  private MedicationService medicationService;
  @Autowired
  private AllergyService allergyService;


  private final ObjectMapper objectMapper;
  private ResourceLoader resourceLoader;
  private String filePath;

  /**
   * Constructor with fields.
   *
   * @param mapper
   *        a {@link ObjectMapper} to parse json Object.
   *
   * @param resourceLoader
   *        a {@link ResourceLoader} to know the path of resources to used.
   *
   * @param filePath
   *            the path of file.json to load with default value initialize for testing.
   */
  @Autowired
  public LoadDatabaseTestFromJson(ObjectMapper mapper,
                                  ResourceLoader resourceLoader,
                                  @Value("classpath:${filejson.test}") String filePath) {

    this.objectMapper = mapper;
    this.resourceLoader = resourceLoader;
    this.filePath = filePath;

  }

  @Override
  public StrategyName getStrategyName() {

    return StrategyName.StrategyTest;

  }

  @Override
  @Transactional
  public boolean loadDatabaseFromSource() {

    File fileJson = null;

    try {

      fileJson = resourceLoader.getResource(filePath)
          .getFile();

      // ClassPathResource(filePath).getFile();
    } catch (IOException e1) {

      if (e1 instanceof FileNotFoundException) {

        log.error("File Data.json is not Found in resources");

      } else {

        log.error("Reading Failure for File Data.json");

      }

      e1.printStackTrace();
      return false;

    }

    JsonNode root = null;

    try {

      root = objectMapper.readTree(fileJson);

    } catch (JsonProcessingException e) {

      log.error("Json's datas are not valid");
      e.printStackTrace();
      return false;

    } catch (IOException e) {

      log.error("File Data.json is missing to be parsed");
      e.printStackTrace();
      return false;

    }

    // initilaisation des jsonNode pour chaque Ogject
    // Person,FireStation,MedicalRecord
    if (root != null) {

      JsonNode personArray = root.get("persons");
      JsonNode fireStationArray = root.get("firestations");
      JsonNode medicalRecordArray = root.get("medicalrecords");

      // initialisation des it??rators respectifs
      Iterator<JsonNode> personNode = personArray.elements();
      Iterator<JsonNode> fireStationNode = fireStationArray.elements();
      Iterator<JsonNode> medicalRecordNode = medicalRecordArray.elements();

      // save persons

      log.info("********** sauvegarde des Persons ***********");

      while (personNode.hasNext()) {

        String element = personNode.next().toString();

        try {

          Person person = objectMapper.readValue(element, Person.class);
          personService.savePerson(person);

        } catch (JsonProcessingException e) {

          e.printStackTrace();
          log.error("problem to parse persons with objectMapper");
          return false;

        }

      }

      // save firestations

      // to avoid duplicate FireStations
      List<Integer> numberStations = new ArrayList<Integer>();

      log.info("********** save FireStation **********");

      while (fireStationNode.hasNext()) {

        FireStation fireStation = null;
        JsonNode elementFireStation = fireStationNode.next();

        int numberStation = elementFireStation.get("station").asInt();
        String addressFireStation = elementFireStation.get("address").asText();

        if (!numberStations.contains(numberStation)) {

          fireStation = new FireStation();
          fireStation.setNumberStation(numberStation);
          fireStationService.saveFireStation(fireStation);
          numberStations.add(numberStation);

        } else {

          fireStation =
              fireStationService.getFireStationByNumberStation(numberStation).get();

        }

        fireStation.addAddress(addressFireStation);

        Iterable<Person> persons = personService.getPersonsByAddress(addressFireStation);

        for (Person person : persons) {

          if (person.getAddress().equals(addressFireStation)) {

            fireStation.addPerson(person);

          }

        }

      }

      // to avoid duplicate allergy
      List<String> designationAllergy = new ArrayList<>();

      // to avoid duplicate medication
      List<String> designationPosologies = new ArrayList<>();

      // save medications

      log.info("********** save MedicalRecord **********");

      while (medicalRecordNode.hasNext()) {

        JsonNode elementMedicalRecord = medicalRecordNode.next();

        MedicalRecord medicalRecord = new MedicalRecord();

        // get person with this medicalrecord

        Optional<Person> currentPerson =
            personService.getPersonByNames(
                elementMedicalRecord.get("firstName").asText().toString(),
                elementMedicalRecord.get("lastName").asText().toString());

        // update birthdate and medicalRecord for person

        String birthDateAsString;
        birthDateAsString = elementMedicalRecord.get("birthdate").asText();

        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date birthDate = null;

        try {

          birthDate = df.parse(birthDateAsString);
        } catch (ParseException e) {

          e.printStackTrace();
        }

        currentPerson.get().setBirthDate(birthDate);
        currentPerson.get().setMedicalRecord(medicalRecord);


        // save medication instance

        log.info("********** save Medication **********");

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
                .getMedicationByDesignationAndPosology(composition[0], composition[1]).get();

          }

          medication.add(medicalRecord);

        }

        // save allergies

        log.info("********** save Allergy **********");

        while (allergyElement.hasNext()) {

          String designation = allergyElement.next().asText();

          Allergy allergy = null;

          if (!designationAllergy.contains(designation)) {

            allergy = new Allergy();
            allergy.setDesignation(designation);

            designationAllergy.add(designation);

            allergyService.saveAllergy(allergy);

          } else {

            allergy = allergyService.getAllergyByDesignation(designation).get();

          }

          allergy.add(medicalRecord);

        }

        // save medicalRecord
        log.info("********** save MedicalRecord **********");
        medicalRecordService.saveMedicalRecord(medicalRecord);

      }

      return true;

    }

    return false;

  }

}

