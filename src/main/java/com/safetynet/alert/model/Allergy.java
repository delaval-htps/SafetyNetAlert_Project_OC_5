package com.safetynet.alert.model;

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
public class Allergy {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id_Allergy;

  @Column
  private String designation;

  @ManyToMany(fetch = FetchType.EAGER,
      cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinTable(name = "attribution_allergy", joinColumns = @JoinColumn(name = "id_Allergy"),
      inverseJoinColumns = @JoinColumn(name = "id_MedicalRecord"))
  private Set<MedicalRecord> medicalRecords = new HashSet<>();

  public void add(MedicalRecord medicalRecord) {
    medicalRecords.add(medicalRecord);
  }
}
