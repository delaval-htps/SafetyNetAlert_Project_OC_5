package com.safetynet.alert.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
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

@Getter
@Setter
@Entity
@JsonIdentityInfo(
                  generator = ObjectIdGenerators.PropertyGenerator.class,
                  property = "id_Person"
)
public class Person {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id_Person;

  @Column
  private String firstName;

  @Column
  private String lastName;

  @Column
  private String birthDate;

  @Column
  private String address;

  @Column
  private String city;

  @Column
  private int zip;

  @Column
  private String phone;

  @Column
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

  @Override
  public String toString() {
    return "Person [id_Person=" + id_Person + ", firstName=" + firstName
        + ", lastName=" + lastName + ", birthDate=" + birthDate + ", address="
        + address + ", city=" + city + ", zip=" + zip + ", phone=" + phone
        + ", email=" + email + ", medicalRecord=" + medicalRecord
        + ", fireStations=" + fireStations + "]";
  }
}
