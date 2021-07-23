package com.safetynet.alert.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

/**
 * Entity Person.
 *
 * @author delaval
 *
 */
@Getter
@Setter
@ToString(exclude = {"medicalRecord",
                     "fireStations"})

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Person",
       uniqueConstraints = @UniqueConstraint(columnNames = {"idPerson",
                                                            "lastName",
                                                            "firstName"}))
@Entity
@ApiModel
public class Person {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  @ApiModelProperty(readOnly = true)

  private Long idPerson;

  @Column
  @NotNull(message = "this firstName must not be null")
  @Pattern(
           regexp = "^[A-Z]+[a-z]*",
           message = "this firstName must contains first capital letter "
               + "and for the other letter lowercase  ")
  @ApiModelProperty(notes = "firstName of Person")

  private String firstName;

  @Column
  @NotNull(message = "this lastName must not be null")
  @Pattern(
           regexp = "^[A-Z]+[a-z]*",
           message = "this firstName must contains first capital letter"
               + " and for the other letter lowercase  ")
  @ApiModelProperty(notes = "lastName of Person")

  private String lastName;

  @Column
  @Past(message = "this birthdate must be past today")
  @JsonFormat(shape = JsonFormat.Shape.STRING,
              pattern = "MM/dd/yyyy")
  @ApiModelProperty(notes = "birthDate of Person")

  private Date birthDate;

  @Column
  @NotNull(message = "this address must not be null")
  @NotBlank(message = "this address must not be null")
  @ApiModelProperty(notes = "address of Person")

  private String address;

  @Column
  @NotNull(message = "this city must not be null")
  @Pattern(
           regexp = "^[A-Z]+[a-zA-Z ]*",
           message = "this City must contains first capital letter "
               + "and for the other letter lowercase  ")
  @ApiModelProperty(notes = "city of Person")

  private String city;

  @Column
  @Range(
         min = 0,
         max = 99999,
         message = "this zip must not be beetween 0 and 99999")
  @NotNull
  @ApiModelProperty(notes = "zip of person's city")

  private Integer zip;

  @Column
  @NotNull(message = "this number of phone must not be null")
  @Pattern(
           regexp = "[0-9]{3}-[0-9]{3}-[0-9]{4}",
           message = "this phone number must match "
               + "with this pattern xxx-xxx-xxxx with x for a integer")
  @ApiModelProperty(notes = "phone's number of Person")

  private String phone;

  @Column
  @NotNull(message = "this email must be not null")
  @Email(
         message = "this field need to be a correct Email, example: john.boyd@email.com")
  @ApiModelProperty(notes = "email of Person")

  private String email;

  //Cascade ALL to delete automatically all relationships
  //with medicalRecord,medications and allergies when deleting person

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "idMedicalRecord",
              referencedColumnName = "idMedicalRecord")
  @JsonBackReference(value = "person_medicalRecord")
  @ApiModelProperty(notes = "MedicalRecord of Person")

  private MedicalRecord medicalRecord;


  @ManyToMany(fetch = FetchType.LAZY,
              cascade = {CascadeType.DETACH,
                         CascadeType.MERGE,
                         CascadeType.PERSIST,
                         CascadeType.REFRESH})
  @JoinTable(
             name = "person_firestation",
             joinColumns = {@JoinColumn(name = "idPerson")},
             inverseJoinColumns = {@JoinColumn(name = "idFireStation")})
  @JsonIgnore
  @ApiModelProperty(notes = "List of FireStations mapped with address of Person")

  private Set<FireStation> fireStations = new HashSet<>();

  /**
   * Method to add a fireStation into Set of FireStations of Person.
   *
   * @param fireStation the fireStation to add
   *
   */
  public void addFireStation(FireStation fireStation) {

    if (fireStation != null) {

      this.fireStations.add(fireStation);
    }

  }

  /**
   * Method to add all FireStations into Set of FireStations of Person.
   *
   * @param fireStations    List of FireStations to add
   *
   */
  public void addFireStations(List<FireStation> fireStations) {

    if (!fireStations.isEmpty()) {

      this.fireStations.addAll(fireStations);
    }

  }

  /**
   * Method to remove a firestation from Set Firestations of Person.
   *
   * @param fireStation the fireStation to remove.
   *
   */
  public void removeFireStation(FireStation fireStation) {

    if (fireStation != null) {

      this.fireStations.remove(fireStation);
    }

  }

  /**
   * Method to clear  Set FireStations of Person.
   */
  public void clearFireStations() {

    this.fireStations.clear();

  }

  /**
   * Constructor with some fields used in jpql query: "/childAlert?address=String".
   *
   * @param firstName   the firstName of Person
   * @param lastName    the lastName of Person
   * @param birthDate   the bithDate of Person
   */
  public Person(String firstName, String lastName, Date birthDate) {

    this(firstName, lastName, birthDate, null, null);

  }

  /**
   * Constructor with some fields used in jpql query: "/fireStation?stationNumber= int".
   *
   * @param firstName the firstName of person.
   * @param lastName  the lastName of person.
   * @param address   the address of person.
   * @param phone     the phone of person.
   * @param birthDate the birthDate of person.
   *
   */
  public Person(String firstName, String lastName,
                Date birthDate, String address, String phone) {

    this.firstName = firstName;
    this.lastName = lastName;
    this.birthDate = birthDate;
    this.address = address;
    this.phone = phone;

  }


}


