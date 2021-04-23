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

@Getter
@Setter
@Entity
@JsonIdentityInfo(
                  generator = ObjectIdGenerators.PropertyGenerator.class,
                  property = "id_Medication"
)
public class Medication {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id_Medication;

  @Column
  private String designation;

  @Column
  private String posology;

  @ManyToMany(
              fetch = FetchType.EAGER,
              cascade = {CascadeType.DETACH,
                         CascadeType.MERGE,
                         CascadeType.PERSIST,
                         CascadeType.REFRESH}
  )
  @JoinTable(
             name = "attribution_medication",
             joinColumns = @JoinColumn(name = "id_Medication"),
             inverseJoinColumns = @JoinColumn(name = "id_MedicalRecord")
  )
  @JsonIgnore
  private Set<MedicalRecord> medicalRecords = new HashSet<>();

  public void add(MedicalRecord medicalRecord) {
    medicalRecords.add(medicalRecord);
  }
}
