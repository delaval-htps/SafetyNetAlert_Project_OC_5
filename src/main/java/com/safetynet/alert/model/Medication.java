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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity of Medication.
 *
 * @author delaval
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "medicalRecords")
@Table(name = "Medication")
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

  /**
   * Method to add a medicalRecord to the Set of a instance of Medication.
   *
   * @param medicalRecord
   *            the MedicalRecord to add to Set medicalRecords.
   */
  public void add(MedicalRecord medicalRecord) {

    if ((medicalRecord != null) && (!this.medicalRecords.contains(medicalRecord))) {

      medicalRecords.add(medicalRecord);

    }

  }

  /**
   * Method to remove a medicalRecord from the Set of a instance of Medication.
   *
   * @param medicalRecord
   *            the MedicalRecord to remove from Set medicalRecords.
   */
  public void remove(MedicalRecord medicalRecord) {

    if ((medicalRecord != null) && (this.medicalRecords.contains(medicalRecord))) {

      medicalRecords.remove(medicalRecord);

    }

  }
}
