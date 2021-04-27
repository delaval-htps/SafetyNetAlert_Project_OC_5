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
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

@Getter
@Setter
@Entity
public class MedicalRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id_MedicalRecord;

  // declaration of relationship 1:1 to have bidirectionnal relation
  // ( we can with a MedicalRecord add a Person)
  @OneToOne(mappedBy = "medicalRecord", cascade = CascadeType.ALL)

  private Person person;


  @ManyToMany(
              fetch = FetchType.LAZY,
              cascade = {CascadeType.DETACH,
                         CascadeType.MERGE,
                         CascadeType.PERSIST,
                         CascadeType.REFRESH}
  )
  @JoinTable(
             name = "attribution_medication",
             joinColumns = @JoinColumn(name = "id_MedicalRecord"),
             inverseJoinColumns = @JoinColumn(name = "id_Medication")
  )
  @OrderBy("id_Medication") // to impose jsonPath to be ordered by id when response
  private Set<Medication> medications = new HashSet<>();


  @ManyToMany(
              fetch = FetchType.LAZY,
              cascade = {CascadeType.DETACH,
                         CascadeType.MERGE,
                         CascadeType.PERSIST,
                         CascadeType.REFRESH}
  )
  @JoinTable(
             name = "attribution_allergy",
             joinColumns = @JoinColumn(name = "id_MedicalRecord"),
             inverseJoinColumns = @JoinColumn(name = "id_Allergy")
  )
  @OrderBy("id_Allergy") // to impose jsonPath to be ordered by id when response
  private Set<Allergy> allergies = new HashSet<>();

}
