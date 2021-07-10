package com.safetynet.alert.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity FireStation.
 *
 * @author delaval
 */
@Getter
@Setter
@ToString(exclude = {"persons",
                     "addresses"})
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FireStation",
       uniqueConstraints = @UniqueConstraint(columnNames = {"idFireStation",
                                                            "station"}))
public class FireStation implements Serializable {

  private static final long serialVersionUID = 5090513180865338976L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long idFireStation;

  @Column(name = "station")
  @Min(value = 1)
  @JsonAlias("station")
  private Integer numberStation;

  @ElementCollection
  @CollectionTable(joinColumns = @JoinColumn(name = "idFireStation"))
  @Column(name = "adresses")
  private Set<@NotBlank String> addresses = new HashSet<String>();


  @ManyToMany(fetch = FetchType.LAZY,
              cascade = {CascadeType.REFRESH,
                         CascadeType.DETACH,
                         CascadeType.MERGE,
                         CascadeType.PERSIST})
  @JoinTable(
             name = "person_firestation",
             joinColumns = @JoinColumn(name = "idFireStation"),
             inverseJoinColumns = @JoinColumn(name = "idPerson"))
  @JsonIgnore
  private Set<Person> persons = new HashSet<>();

  /**
   * method to add a Person in Set persons of this FireStation.
   *
   * @param person
   *             a Person to map with FireStation
   */
  public void addPerson(Person person) {

    persons.add(person);

  }

  /**
   * method to remove a Person in Set Persons of this FireStation.
   *
   * @param person
   *             a Person to remove from Set of Persons.
   */
  public void removePerson(Person person) {

    persons.remove(person);

  }

  /**
   * method to add a address in Set addresses of this FireStation.
   *
   * @param address
   *             a address to map with FireStation
   */
  public void addAddress(String address) {

    addresses.add(address);

  }


}
