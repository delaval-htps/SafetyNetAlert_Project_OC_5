package com.safetynet.alert.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity Allergy.
 *
 * @author delaval
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "medicalRecords")
@Table(name = "Allergy",
       uniqueConstraints = @UniqueConstraint(columnNames = {"idAllergy",
                                                            "designation"}))
@Entity
public class Allergy {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long idAllergy;

  @Column
  private String designation;

  @ManyToMany(
              fetch = FetchType.LAZY,
              cascade = {CascadeType.DETACH,
                         CascadeType.MERGE,
                         CascadeType.PERSIST,
                         CascadeType.REFRESH})
  @JoinTable(
             name = "attribution_allergy",
             joinColumns = @JoinColumn(name = "idAllergy"),
             inverseJoinColumns = @JoinColumn(name = "idMedicalRecord"))
  @JsonIgnore
  private Set<MedicalRecord> medicalRecords = new HashSet<>();

  /**
   * Method to map this allergy with a MedicalRecord.
   *
   * @param medicalRecord
   *            the MedicalRecord to add to set of MedicalRecord of Allergy.
   */
  public void add(MedicalRecord medicalRecord) {

    medicalRecords.add(medicalRecord);

  }

  /**
   * Method to delete mapping of this allergy with a MedicalRecord.
   *
   * @param currentMedicalRecord
   *            the MedicalRecord to remove from set of MedicalRecord of Allergy.
   */
  public void remove(MedicalRecord currentMedicalRecord) {

    medicalRecords.remove(currentMedicalRecord);

  }
}
