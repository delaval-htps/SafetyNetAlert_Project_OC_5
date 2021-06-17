package com.safetynet.alert.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
                     "fireStation"})

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Person",
       uniqueConstraints = @UniqueConstraint(columnNames = {"idPerson",
                                                            "lastName",
                                                            "firstName"}))
@Entity
public class Person {

  /**
   * Constructor with some fields used in hql query: "/fireStation?stationNumber= int".
   *
   * @param firstName the firstname of person.
   * @param lastName  the lastname of person.
   * @param address   the address of person.
   * @param phone     the phone of person.
   * @param birthDate
   *
   */
  public Person(String firstName, String lastName,
                String address,
                String phone,
                Date birthDate) {

    this.firstName = firstName;
    this.lastName = lastName;
    this.birthDate = birthDate;
    this.address = address;
    this.phone = phone;

  }

  /**
   * Constructor with some fields used in hql query: "/childAlert?address=String".
   *
   * @param firstName   the firstName of Person
   * @param lastName    the lastName of Person
   * @param birthDate   the bithDate of Person
   */
  public Person(String firstName, String lastName, Date birthDate) {

    this.firstName = firstName;
    this.lastName = lastName;
    this.birthDate = birthDate;

  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long idPerson;

  @Column
  @NotNull(message = "this firstName must not be null")
  @Pattern(
           regexp = "^[A-Z]+[a-z]*",
           message = "this firstName must contains first capital letter "
               + "and for the other letter lowercase  ")
  private String firstName;

  @Column
  @NotNull(message = "this lastName must not be null")
  @Pattern(
           regexp = "^[A-Z]+[a-z]*",
           message = "this firstName must contains first capital letter"
               + " and for the other letter lowercase  ")
  private String lastName;

  @Column
  @Past(message = "this birthdate must be past today")
  @JsonFormat(shape = JsonFormat.Shape.STRING,
              pattern = "MM/dd/yyyy")
  private Date birthDate;

  @Column
  @NotNull(message = "this address must not be null")
  @NotBlank
  private String address;

  @Column
  @NotNull(message = "this city must not be null")
  @Pattern(
           regexp = "^[A-Z]+[a-zA-Z ]*",
           message = "this City must contains first capital letter "
               + "and for the other letter lowercase  ")
  private String city;

  @Column
  @Range(
         min = 0,
         max = 99999,
         message = "this zip must not be beetween 0 and 99999")
  @NotNull
  private Integer zip;

  @Column
  @NotNull(message = "this number of phone must not be null")
  @Pattern(
           regexp = "[0-9]{3}-[0-9]{3}-[0-9]{4}",
           message = "this phone number must match "
               + "with this pattern xxx-xxx-xxxx with x for a integer")
  private String phone;

  @Column
  @NotNull(message = "this email must be not null")
  @Email(
         message = "this field need to be a correct Email, example: john.boyd@email.com")
  private String email;

  //Cascade ALL to delete automatically all relationships
  //with medicalRecord,medications and allergies when deleting person

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "idMedicalRecord",
              referencedColumnName = "idMedicalRecord")
  @JsonBackReference
  private MedicalRecord medicalRecord;

  @ManyToOne(
             fetch = FetchType.LAZY,
             cascade = {CascadeType.DETACH,
                        CascadeType.MERGE,
                        CascadeType.REFRESH,
                        CascadeType.PERSIST})
  @JoinColumn(name = "idFireStation")
  @JsonIgnore
  private FireStation fireStation;



}


