package com.safetynet.alert.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModelProperty;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity MedicalRecord.
 *
 * @author delaval
 *
 */
@Getter
@Setter
@ToString(exclude = "person")
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "MedicalRecord")
@Entity
@JsonPropertyOrder({"idMedicalRecord",
                    "person",
                    "medications",
                    "allergies"})
public class MedicalRecord {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @ApiModelProperty(notes = "Id of MedicalRecord", readOnly = true)
  private Long idMedicalRecord;

  // declaration of relationship 1:1 to have bidirectionnal relation
  // ( we can with a MedicalRecord add a Person)

  @OneToOne(mappedBy = "medicalRecord",
            cascade = {CascadeType.DETACH,
                       CascadeType.MERGE,
                       CascadeType.REFRESH,
                       CascadeType.PERSIST})
  @JsonManagedReference(value = "person_medicalRecord")
  @ApiModelProperty(notes = "Person owner of Medicalrecord")

  private Person person;

  @ManyToMany(
              fetch = FetchType.LAZY,
              cascade = {CascadeType.DETACH,
                         CascadeType.MERGE,
                         CascadeType.PERSIST,
                         CascadeType.REFRESH})
  @JoinTable(
             name = "attribution_medication",
             joinColumns = {@JoinColumn(name = "idMedicalRecord")},
             inverseJoinColumns = {@JoinColumn(name = "idMedication")})
  //@OrderBy("idMedication") // to impose jsonPath to be ordered by id when response
  @ApiModelProperty(notes = "list of medications in MedicalRecord")

  private Set<Medication> medications = new HashSet<>();


  @ManyToMany(
              fetch = FetchType.LAZY,
              cascade = {CascadeType.DETACH,
                         CascadeType.MERGE,
                         CascadeType.PERSIST,
                         CascadeType.REFRESH})
  @JoinTable(
             name = "attribution_allergy",
             joinColumns = {@JoinColumn(name = "idMedicalRecord")},
             inverseJoinColumns = {@JoinColumn(name = "idAllergy")})
  //@OrderBy("idAllergy") // to impose jsonPath to be ordered by id when response
  @ApiModelProperty(notes = "list of allergies in Medicalrecord")

  private Set<Allergy> allergies = new HashSet<>();

  /**
   * Method to clear any Set of MedicalRecord.
   *
   * @param hashSet
   *            the Set to clear for MedicalRecord. Can be a Set of Allergy or Medication.
   */
  public void clearSet(Set<?> hashSet) {

    hashSet.clear();

  }

  /**
   * method to add a medication in the list medications of this MedicalRecord.
   *
   * @param medication    the medication to add
   */
  public void add(Medication medication) {

    this.medications.add(medication);

  }

  /**
   * method to add an allergy in the list allergies of this MedicalRecord.
   *
   * @param allergy the allergy to add
   */
  public void add(Allergy allergy) {

    this.allergies.add(allergy);

  }

  /**
   * Method to add a Set of medications and of allergy to a MedicalRecord.
   *
   * @param medications Set of medications to add.
   * @param allergies   Set of allergies to add.
   */
  public MedicalRecord(Medication medications, Allergy allergies) {

    this.allergies.add(allergies);
    this.medications.add(medications);

  }
}
