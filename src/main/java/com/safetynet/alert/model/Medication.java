package com.safetynet.alert.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "medicalRecords")
@Table(name = "Medication",
       uniqueConstraints = @UniqueConstraint(columnNames = {"designation",
                                                            "posology"}))
@Entity
public class Medication {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long idMedication;

  @Column
  private String designation;

  @Column
  private String posology;

  @ManyToMany(
              fetch = FetchType.LAZY,
              cascade = {CascadeType.DETACH,
                         CascadeType.MERGE,
                         CascadeType.PERSIST,
                         CascadeType.REFRESH})
  @JoinTable(
             name = "attribution_medication",
             joinColumns = @JoinColumn(name = "idMedication"),
             inverseJoinColumns = @JoinColumn(name = "idMedicalRecord"))
  @JsonIgnore
  private Set<MedicalRecord> medicalRecords = new HashSet<>();

  public void add(MedicalRecord medicalRecord) {

    if ((medicalRecord != null) && (!this.medicalRecords.contains(medicalRecord))) {

      medicalRecords.add(medicalRecord);

    }

  }

  public void remove(MedicalRecord medicalRecord) {

    if ((medicalRecord != null) && (this.medicalRecords.contains(medicalRecord))) {

      medicalRecords.remove(medicalRecord);

    }

  }
}
