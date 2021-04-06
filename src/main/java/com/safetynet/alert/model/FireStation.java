package com.safetynet.alert.model;

import com.fasterxml.jackson.annotation.JsonAlias;
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
public class FireStation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long Id_FireStation;

  @Column(name = "station")
  @JsonAlias("station") // for deserialize with another name that numberStation using
                        // jacksonAnnotation
  private int numberStation;


  @ManyToMany(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.REFRESH, CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST})
  @JoinTable(
      name = "FireStation_Person",
      joinColumns = @JoinColumn(name = "id_FireStation"),
      inverseJoinColumns = @JoinColumn(name = "id_Person"))
  private Set<Person> persons = new HashSet<>();

  public void add(Person person) {

    persons.add(person);
  }
}
