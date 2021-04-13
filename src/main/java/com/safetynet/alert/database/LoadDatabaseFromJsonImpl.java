package com.safetynet.alert.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class LoadDatabaseFromJsonImpl implements LoadDatabaseService {

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

  private File fileJson;
  private ObjectMapper objectMapper;

  public LoadDatabaseFromJsonImpl(File file) {
    this.fileJson = file;
    this.objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public LoadDatabaseFromJsonImpl() {}

  @Override
  public boolean loadDatabaseFromSource() {

    JsonNode root = null;
    try {
      root = objectMapper.readTree(fileJson);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    // initilaisation des jsonNode pour chaque Ogject Person,FireStation,MedicalRecord
    JsonNode personArray = root.get("persons");
    JsonNode fireStationArray = root.get("firestations");
    JsonNode medicalRecordArray = root.get("medicalrecords");

    // initialisation des itérators respectifs
    Iterator<JsonNode> personNode = personArray.elements();
    Iterator<JsonNode> fireStationNode = fireStationArray.elements();
    Iterator<JsonNode> medicalRecordNode = medicalRecordArray.elements();

    // enregistrement des persons}
    System.out.println("******** sauvegarde des Persons ... ");
    while (personNode.hasNext()) {
      String element = personNode.next().toString(); // attention au to string pour utiliser
                                                     // objectMapper sinon on y arrive pas
      try {
        Person person = objectMapper.readValue(element, Person.class);
        personService.savePerson(person);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
        return false;
      }
    }

    // save firestations

    List<Integer> numberStations = new ArrayList<Integer>();

    // pour ne pas enregistrer de doublon on verifie avec cette liste que la station n'exite pas
    // dejà

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
        fireStation = fireStationService.getFireStationByNumberStation(numberStation);
      }

      String addressFireStation = elementFireStation.get("address").asText();
      Iterable<Person> persons = personService.getPersonByAddress(addressFireStation);
      for (Person person : persons) {
        if (person.getAddress().equals(addressFireStation)) {
          fireStation.add(person);
        }
      }
    }

    // save medicalRecords

    List<String> designationAllergy = new ArrayList<>();// permet d'eviter les doublons dans
                                                        // allergy
    List<String> designationPosologies = new ArrayList<>();// permet d'eviter les doublons dans

    // enregristrement des medications

    while (medicalRecordNode.hasNext()) {

      JsonNode elementMedicalRecord = medicalRecordNode.next();

      MedicalRecord medicalRecord = new MedicalRecord();

      // get person with this medicalrecord

      System.out.println(elementMedicalRecord.get("firstName").asText()
          + elementMedicalRecord.get("lastName").asText());
      Person currentPerson =
          personService.getPersonByNames(elementMedicalRecord.get("firstName").asText().toString(),
              elementMedicalRecord.get("lastName").asText().toString());

      // update ForeignKey for medicalRecord and Person
      System.out.println(currentPerson);

      System.out.println("currentPerson: " + currentPerson.getId_Person() + " "
          + currentPerson.getLastName() + " " + currentPerson.getFirstName() + "setMedicalRecord :"
          + medicalRecord.getId_MedicalRecord());

      // update birthdate for person
      String birthDate;
      birthDate = elementMedicalRecord.get("birthdate").asText();

      currentPerson.setBirthDate(birthDate);
      currentPerson.setMedicalRecord(medicalRecord);


      // save medication instance

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
          medication = medicationService.getMedicationByDesignationAndPosology(composition[0],
              composition[1]);
        }

        medication.add(medicalRecord);
      }


      // save allergies
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
      medicalRecordService.saveMedicalRecord(medicalRecord);

    }
    return true;
  }
}
