package com.safetynet.alert.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;
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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Person {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long idPerson;

  @Column
  @NotNull(message = "this firstName must not be null")
  @Pattern(
           regexp = "^[A-Z]+[a-z]*",
           message = "this firstName must contains first capital letter "
               + "and for the other letter lowercase  "
  )
  private String firstName;

  @Column
  @NotNull(message = "this lastName must not be null")
  @Pattern(
           regexp = "^[A-Z]+[a-z]*",
           message = "this firstName must contains first capital letter"
               + " and for the other letter lowercase  "
  )
  private String lastName;

  @Column
  // @NotNull(message = "this birthDate must not be null")
  private String birthDate;

  @Column
  @NotNull(message = "this address must not be null")
  private String address;

  @Column
  @NotNull(message = "this city must not be null")
  @Pattern(
           regexp = "^[A-Z]+[a-zA-Z ]*",
           message = "this City must contains first capital letter "
               + "and for the other letter lowercase  "
  )
  private String city;

  @Column
  @Range(
         min = 0,
         max = 99999,
         message = "this zip must not be beetween 0 and 99999"
  )
  private int zip;

  @Column
  @NotNull(message = "this number of phone must not be null")
  @Pattern(
           regexp = "[0-9]{3}-[0-9]{3}-[0-9]{4}",
           message = "this phone number must match "
               + "with this pattern xxx-xxx-xxxx with x for a integer"
  )
  private String phone;

  @Column
  @NotNull(message = "this email must be not null")
  @Email(
         message = "this field need to be a correct Email, example: john.boyd@email.com"
  )
  private String email;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "id_MedicalRecord")
  @JsonIgnore
  private MedicalRecord medicalRecord;

  @ManyToMany(
              fetch = FetchType.LAZY,
              cascade = {CascadeType.DETACH,
                         CascadeType.MERGE,
                         CascadeType.REFRESH,
                         CascadeType.PERSIST}
  )
  @JoinTable(
             name = "FireStation_Person",
             joinColumns = @JoinColumn(name = "id_Person"),
             inverseJoinColumns = @JoinColumn(name = "id_FireStation")
  )
  @JsonIgnore
  private Set<FireStation> fireStations = new HashSet<>();


}
