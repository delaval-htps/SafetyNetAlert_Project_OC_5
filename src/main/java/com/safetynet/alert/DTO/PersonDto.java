package com.safetynet.alert.DTO;

import com.safetynet.alert.controller.emergency.EmergencyRestController;
import com.safetynet.alert.model.Allergy;
import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.model.MedicalRecord;
import com.safetynet.alert.model.Medication;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * Custom Global Model response to give custom informations for a person
 * in the ResponseEntity of {@link EmergencyRestController}.
 *
 *
 * @author delaval
 */

@Getter
@Setter
@ToString
public class PersonDto {

  private String lastName;
  private String firstName;
  private String age;
  private String address;
  private String phone;
  private String email;

  private Set<Integer> numberStation;
  private Set<Medication> medications;
  private Set<Allergy> allergies;

  /**
   * constructor for endpoint /childAlert.
   *
   * @param firstName firstName of person
   * @param lastName  lastName of Person
   * @param birthDate birthdate of Person
   */
  public PersonDto(String firstName, String lastName, Date birthDate) {

    this(lastName, birthDate, null, null);
    this.firstName = firstName;

  }

  /**
   * Constructor for endpoint /flood/stations.
   *
   * @param lastName        lastName of Person
   * @param birthDate       birthDate of Person
   * @param phone           phone of person
   * @param medicalRecord   medicalRecord of Person
   */
  public PersonDto(String lastName, Date birthDate, String phone,
                   MedicalRecord medicalRecord) {

    this(null, lastName, birthDate, phone, medicalRecord);

  }

  /**
   * Constructor for endpoint /fire.
   *
   * @param fireStations    list of FireStations mapped with person
   * @param lastName        lastName of Person
   * @param birthDate       birthDate of Person
   * @param phone           phone of Person
   * @param medicalRecord   medicalRecord of Person
   */
  public PersonDto(Set<FireStation> fireStations, String lastName, Date birthDate,
                   String phone, MedicalRecord medicalRecord) {

    this(lastName, birthDate, null, null, medicalRecord);

    if (fireStations != null) {

      this.numberStation = new HashSet<>();

      fireStations.forEach(fireStation -> {

        this.numberStation.add(fireStation.getNumberStation());
      });
    }
    this.phone = phone;

  }

  /**
   * Constructor for endpoints /personInfo.
   *
   * @param lastName      lastname for person
   * @param birthDate     birhtDate for person
   * @param address       address for person
   * @param email         email for Person
   * @param medicalRecord medicalRecord for Person
   */

  public PersonDto(String lastName, Date birthDate, String address, String email,
                   MedicalRecord medicalRecord) {

    this.lastName = lastName;
    this.firstName = null;

    int calculatedAge = this.calculateAge(birthDate);

    if (calculatedAge > 0) {

      this.age = String.valueOf(calculatedAge);
    } else {

      this.age = "not specified";
    }
    this.address = address;
    this.email = email;

    if (medicalRecord != null) {

      this.medications = medicalRecord.getMedications();
      this.allergies = medicalRecord.getAllergies();
    }

    this.phone = null;
    this.numberStation = null;

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

    if (birthDate != null) {

      LocalDate birthDateLocalDate = new java.sql.Date(birthDate.getTime()).toLocalDate();
      return Period.between(birthDateLocalDate, LocalDate.now()).getYears();

    } else {

      return -1;
    }

  }

}
