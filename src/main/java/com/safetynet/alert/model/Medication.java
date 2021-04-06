package com.safetynet.alert.model;

import java.util.HashSet;
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
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
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
      fetch = FetchType.LAZY,
      cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinTable(
      name = "attribution_medication",
      joinColumns = @JoinColumn(name = "id_Medication"),
      inverseJoinColumns = @JoinColumn(name = "id_MedicalRecord"))
  private Set<MedicalRecord> medicalRecords = new HashSet<>();

  public void add(MedicalRecord medicalRecord) {
    medicalRecords.add(medicalRecord);
  }
}