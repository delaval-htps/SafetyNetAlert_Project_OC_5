package com.safetynet.alert.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "MedicalRecord")
@Entity
public class MedicalRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long idMedicalRecord;

  // declaration of relationship 1:1 to have bidirectionnal relation
  // ( we can with a MedicalRecord add a Person)

  @OneToOne(mappedBy = "medicalRecord",
            cascade = {CascadeType.DETACH,
                       CascadeType.MERGE,
                       CascadeType.REFRESH,
                       CascadeType.PERSIST})
  @JsonManagedReference
  private Person person;


  @ManyToMany(
              fetch = FetchType.LAZY,
              cascade = {CascadeType.DETACH,
                         CascadeType.MERGE,
                         CascadeType.PERSIST,
                         CascadeType.REFRESH})
  @JoinTable(
             name = "attribution_medication",
             joinColumns = @JoinColumn(name = "idMedicalRecord"),
             inverseJoinColumns = @JoinColumn(name = "idMedication"))
  @OrderBy("idMedication") // to impose jsonPath to be ordered by id when response
  private Set<Medication> medications = new HashSet<>();


  @ManyToMany(
              fetch = FetchType.LAZY,
              cascade = {CascadeType.DETACH,
                         CascadeType.MERGE,
                         CascadeType.PERSIST,
                         CascadeType.REFRESH})
  @JoinTable(
             name = "attribution_allergy",
             joinColumns = @JoinColumn(name = "idMedicalRecord"),
             inverseJoinColumns = @JoinColumn(name = "idAllergy"))
  @OrderBy("idAllergy") // to impose jsonPath to be ordered by id when response
  private Set<Allergy> allergies = new HashSet<>();

  public void clearSet(Set<?> hashSet) {

    hashSet.clear();

  }
}
