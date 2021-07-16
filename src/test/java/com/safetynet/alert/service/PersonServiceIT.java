package com.safetynet.alert.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import com.safetynet.alert.CommandLineRunnerTaskExcecutor;
import com.safetynet.alert.database.LoadDataStrategyFactory;
import com.safetynet.alert.database.StrategyName;
import com.safetynet.alert.model.Person;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestMethodOrder(OrderAnnotation.class)
class PersonServiceIT {


  @MockBean
  private CommandLineRunnerTaskExcecutor clrte;

  @Autowired
  private LoadDataStrategyFactory ldsf;

  @Autowired
  PersonService classUnderTest;

  @BeforeEach
  void setUp() throws Exception {

    ldsf.findStrategy(StrategyName.StrategyTest).loadDatabaseFromSource();


  }

  @Test
  @Order(1)
  void getPersonById_whenValidId() {

    //Given
    Long id = 1L;
    //When
    Optional<Person> personWithId = classUnderTest.getPersonById(id);

    //then
    assertThat(personWithId.get().getLastName()).isEqualTo("Boyd");
    assertThat(personWithId.get().getFirstName()).isEqualTo("John");
    assertThat(personWithId.get().getBirthDate()).isEqualToIgnoringHours("1984-03-06");
    assertThat(personWithId.get().getAddress()).isEqualTo("1509 Culver St");
    assertThat(personWithId.get().getCity()).isEqualTo("Culver");
    assertThat(personWithId.get().getZip()).isEqualTo(97451);
    assertThat(personWithId.get().getPhone()).isEqualTo("841-874-6512");
    assertThat(personWithId.get().getEmail()).isEqualTo("jaboyd@email.com");
    assertThat(personWithId.get().getMedicalRecord()).isNotNull();
    //    assertThat(personWithId.get().getMedicalRecord().getMedications()).hasSize(2)
    //        .extracting(Medication::getDesignation)
    //        .containsExactlyInAnyOrder("aznol", "hydrapermazol");
    assertThat(personWithId.get().getFireStations()).isNotNull();

  }

  @Test
  @Order(2)
  void getPersonById_whenNotFoundId() {

    //given
    Long id = 9L;

    //when
    Optional<Person> personWithInvalidId = classUnderTest.getPersonById(id);

    //then
    assertThat(personWithInvalidId).isEmpty();

  }

  @Test
  @Order(3)
  void getPersons_whenExistedPersons() {

    //Given
    //When
    Iterable<Person> persons = classUnderTest.getPersons();
    Person person = persons.iterator().next();
    //then
    assertThat(person.getLastName()).isEqualTo("Boyd");
    assertThat(person.getFirstName()).isEqualTo("John");
    assertThat(person.getBirthDate()).isEqualToIgnoringHours("1984-03-06");
    assertThat(person.getAddress()).isEqualTo("1509 Culver St");
    assertThat(person.getCity()).isEqualTo("Culver");
    assertThat(person.getZip()).isEqualTo(97451);
    assertThat(person.getPhone()).isEqualTo("841-874-6512");
    assertThat(person.getEmail()).isEqualTo("jaboyd@email.com");
    assertThat(person.getMedicalRecord()).isNotNull();
    assertThat(person.getFireStations()).isNotNull();

  }

  @Test
  @Order(4)
  void getPersonsByAddress_whenExistedPersonsAndAddress() {

    //Given
    String address = "1509 Culver St";
    //When
    Iterable<Person> persons = classUnderTest.getPersonsByAddress(address);

    //then
    Person person1 = persons.iterator().next();
    assertThat(person1.getLastName()).isEqualTo("Boyd");
    assertThat(person1.getFirstName()).isEqualTo("John");
    assertThat(person1.getBirthDate()).isEqualToIgnoringHours("1984-03-06");
    assertThat(person1.getAddress()).isEqualTo("1509 Culver St");
    assertThat(person1.getCity()).isEqualTo("Culver");
    assertThat(person1.getZip()).isEqualTo(97451);
    assertThat(person1.getPhone()).isEqualTo("841-874-6512");
    assertThat(person1.getEmail()).isEqualTo("jaboyd@email.com");
    assertThat(person1.getMedicalRecord()).isNotNull();
    assertThat(person1.getFireStations()).isNotNull();

  }

  @Test
  @Order(5)
  void getPersonsByAddress_whenNoPersonsAtAddress() {

    //Given
    String address = "addressNotMappedWithPerson";

    //When
    Iterable<Person> persons = classUnderTest.getPersonsByAddress(address);

    //then
    assertThat(persons).isEmpty();

  }

  @Test
  @Order(6)
  void getPersonByNames_whenPersonExisted() {

    //given
    String lastName = "Boyd";
    String firstName = "John";

    //when
    Optional<Person> person = classUnderTest.getPersonByNames(firstName, lastName);

    //then
    Person firstPerson = person.get();
    assertThat(firstPerson.getLastName()).isEqualTo("Boyd");
    assertThat(firstPerson.getFirstName()).isEqualTo("John");
    assertThat(firstPerson.getBirthDate()).isEqualToIgnoringHours("1984-03-06");
    assertThat(firstPerson.getAddress()).isEqualTo("1509 Culver St");
    assertThat(firstPerson.getCity()).isEqualTo("Culver");
    assertThat(firstPerson.getZip()).isEqualTo(97451);
    assertThat(firstPerson.getPhone()).isEqualTo("841-874-6512");
    assertThat(firstPerson.getEmail()).isEqualTo("jaboyd@email.com");
    assertThat(firstPerson.getMedicalRecord()).isNotNull();
    assertThat(firstPerson.getFireStations()).isNotNull();

  }

  @ParameterizedTest
  @CsvSource(value = {"noexisted,John",
                      "Boyd,noexisted",
                      "noexisted,noexisted"})
  @Order(7)
  void getPersonByNames_whenNoExistedPerson(ArgumentsAccessor args) {

    //given
    String lastName = args.getString(0);
    String firstName = args.getString(1);

    //when
    Optional<Person> person = classUnderTest.getPersonByNames(firstName, lastName);

    //then
    assertThat(person).isEmpty();

  }

  @Test
  @Order(8)
  void getPersonJoinFireStation_whenPersonExisted() {

    //given
    Long idPerson = 1L;
    List<String> addresses = new ArrayList<>();
    addresses.add("1509 Culver St");
    addresses.add("1509 Av marechal foch");

    //when
    Optional<Person> person = classUnderTest.getPersonJoinFireStationById(idPerson);

    //then
    Person firstPerson = person.get();
    assertThat(firstPerson.getLastName()).isEqualTo("Boyd");
    assertThat(firstPerson.getFirstName()).isEqualTo("John");
    assertThat(firstPerson.getBirthDate()).isEqualToIgnoringHours("1984-03-06");
    assertThat(firstPerson.getAddress()).isEqualTo("1509 Culver St");
    assertThat(firstPerson.getCity()).isEqualTo("Culver");
    assertThat(firstPerson.getZip()).isEqualTo(97451);
    assertThat(firstPerson.getPhone()).isEqualTo("841-874-6512");
    assertThat(firstPerson.getEmail()).isEqualTo("jaboyd@email.com");
    assertThat(firstPerson.getMedicalRecord()).isNotNull();
    assertThat(firstPerson.getFireStations()).isNotNull();
    firstPerson.getFireStations().forEach(fireStation -> {

      assertThat(fireStation.getAddresses()).hasSize(2).containsAnyElementsOf(addresses);
      assertThat(fireStation.getIdFireStation()).isNotNull();
    });

  }

  @Test
  @Order(9)
  void getPersonJoinFireStation_whenNoExistedPerson() {

    //given
    Long idPerson = 10L;

    //when
    Optional<Person> person = classUnderTest.getPersonJoinFireStationById(idPerson);

    //then
    assertThat(person).isEmpty();

  }


  @Test
  @Order(9)
  void savePerson_whenExistedPerson() {

    //given
    Date birthDate = new Date(System.currentTimeMillis() - 3600);
    Person personToSave =
        new Person(null, "Test", "Tosave", birthDate, "addressTest", "CityTest", 13000,
                   "061-846-0160", "test@email.com", null, null);

    //when
    Person personSaved = classUnderTest.savePerson(personToSave);

    //then
    assertThat(personSaved.getIdPerson()).isNotNull();
    assertThat(personSaved.getLastName()).isEqualTo("Tosave");
    assertThat(personSaved.getFirstName()).isEqualTo("Test");
    assertThat(personSaved.getBirthDate()).isAfterOrEqualTo(birthDate);
    assertThat(personSaved.getAddress()).isEqualTo("addressTest");
    assertThat(personSaved.getCity()).isEqualTo("CityTest");
    assertThat(personSaved.getZip()).isEqualTo(13000);
    assertThat(personSaved.getPhone()).isEqualTo("061-846-0160");
    assertThat(personSaved.getEmail()).isEqualTo("test@email.com");
    assertThat(personSaved.getMedicalRecord()).isNull();
    assertThat(personSaved.getFireStations()).isNull();

  }

  @Test
  @Order(10)
  void savePerson_whenNullPerson() {

    //given
    Person personToSave = null;

    //when
    //then
    assertThrows("In PersonService.Save(person): Person is null!",
        NullPointerException.class,
        (
        ) -> {

          classUnderTest.savePerson(personToSave);
        });

  }

  @Test
  @Order(11)
  void deletePerson_whenExistedPerson() {

    //given
    Optional<Person> personToDelete = classUnderTest.getPersonById(1L);

    //When
    classUnderTest.deletePerson(personToDelete.get());

    //then
    assertThat(classUnderTest.getPersonByNames("John", "Boyd")).isEmpty();

  }

  @Test
  @Order(12)
  void deletePerson_whenNullPerson() {

    //given
    classUnderTest = new PersonService();

    Person personToDelete = null;

    //when
    //then
    assertThrows("In PersonService.deletePerson(person): Person is null!",
        NullPointerException.class,
        (
        ) -> {

          classUnderTest.deletePerson(personToDelete);
        });

  }

  @Test
  @Order(13)
  void getPersonsMappedWithFireStation_whenExistedFireStation() {

    //Given
    Map<String, Object> map = new LinkedHashMap<>();
    int existedNumberStation = 3;

    //When
    map = classUnderTest.getPersonsMappedWithFireStation(existedNumberStation);

    //Then

    assertThat(map.get("AdultCount")).isEqualTo(3);
    assertThat(map.get("ChildrenCount")).isEqualTo(2);
    assertThat(map.get("BirthDateNotSpecified")).isEqualTo(1);
    assertThat(map.get("persons")).asList().size().isEqualTo(6);
    List<Person> persons = (List<Person>) map.get("persons");
    assertThat(persons).element(0).hasFieldOrPropertyWithValue("lastName", "Boyd");
    assertThat(persons).element(0).hasFieldOrPropertyWithValue("firstName", "John");
    assertThat(persons).element(0).hasFieldOrPropertyWithValue("address", "1509 Culver St");
    assertThat(persons).element(0).hasFieldOrPropertyWithValue("phone", "841-874-6512");
    assertThat(persons).element(1).hasFieldOrPropertyWithValue("lastName", "Boyd");
    assertThat(persons).element(1).hasFieldOrPropertyWithValue("firstName", "Jacob");
    assertThat(persons).element(1).hasFieldOrPropertyWithValue("address", "1509 Culver St");
    assertThat(persons).element(1).hasFieldOrPropertyWithValue("phone", "841-874-6513");
    assertThat(persons).element(2).hasFieldOrPropertyWithValue("lastName", "Boyd");
    assertThat(persons).element(2).hasFieldOrPropertyWithValue("firstName", "Tenley");
    assertThat(persons).element(2).hasFieldOrPropertyWithValue("address", "1509 Culver St");
    assertThat(persons).element(2).hasFieldOrPropertyWithValue("phone", "841-874-6512");
    assertThat(persons).element(3).hasFieldOrPropertyWithValue("lastName", "Boyd");
    assertThat(persons).element(3).hasFieldOrPropertyWithValue("firstName", "Roger");
    assertThat(persons).element(3).hasFieldOrPropertyWithValue("address", "1509 Culver St");
    assertThat(persons).element(3).hasFieldOrPropertyWithValue("phone", "841-874-6512");
    assertThat(persons).element(4).hasFieldOrPropertyWithValue("lastName", "Boyd");
    assertThat(persons).element(4).hasFieldOrPropertyWithValue("firstName", "Felicia");
    assertThat(persons).element(4).hasFieldOrPropertyWithValue("address", "1509 Culver St");
    assertThat(persons).element(4).hasFieldOrPropertyWithValue("phone", "841-874-6544");
    assertThat(persons).element(5).hasFieldOrPropertyWithValue("lastName", "Delaval");
    assertThat(persons).element(5).hasFieldOrPropertyWithValue("firstName", "Dorian");
    assertThat(persons).element(5).hasFieldOrPropertyWithValue("address",
        "1509 Av marechal foch");
    assertThat(persons).element(5).hasFieldOrPropertyWithValue("phone", "061-846-0160");


  }


  @Test
  @Order(15)
  void getPersonsMappedWithFireStation_whenNonExistedFireStation() {

    //Given
    Map<String, Object> map = new LinkedHashMap<>();
    int noExistedNumberStation = 5;

    //When
    map = classUnderTest.getPersonsMappedWithFireStation(noExistedNumberStation);

    //then
    assertThat(map.get("AdultCount")).isEqualTo(0);
    assertThat(map.get("ChildrenCount")).isEqualTo(0);
    assertThat(map.get("persons")).asList().size().isEqualTo(0);

  }

  @Test
  @Order(16)
  void getPersonsMappedWithFireStation_whenFireStationWithNoPersonMapped() {

    //Given
    Map<String, Object> map = new LinkedHashMap<>();
    int existedStationWithoutPersons = 4;

    //When
    map = classUnderTest.getPersonsMappedWithFireStation(existedStationWithoutPersons);

    //then
    assertThat(map.get("AdultCount")).isEqualTo(0);
    assertThat(map.get("ChildrenCount")).isEqualTo(0);
    assertThat(map.get("persons")).asList().size().isEqualTo(0);

  }

  @Test
  @Order(17)
  void getChildrenByAddress_whenExistedAddressAndChildren() {

    //Given
    Map<String, List<Person>> map = new LinkedHashMap<>();
    String existedAddress = "1509 Culver St";

    //When
    map = classUnderTest.getChildrenByAddress(existedAddress);

    //Then
    List<Person> children = map.get("children");
    List<Person> others = map.get("otherMembers");
    assertThat(children).size().isEqualTo(2);
    assertThat(children.get(0).getLastName()).isEqualTo("Boyd");
    assertThat(children.get(0).getBirthDate()).isNotNull();
    assertThat(children.get(1).getLastName()).isEqualTo("Boyd");
    assertThat(children.get(1).getBirthDate()).isNotNull();
    assertThat(others).size().isEqualTo(3);
    assertThat(others.get(0).getLastName()).isEqualTo("Boyd");
    assertThat(others.get(0).getBirthDate()).isNotNull();
    assertThat(others.get(1).getLastName()).isEqualTo("Boyd");
    assertThat(others.get(1).getBirthDate()).isNotNull();
    assertThat(others.get(2).getLastName()).isEqualTo("Boyd");
    assertThat(others.get(2).getBirthDate()).isNotNull();

  }

  @Test
  @Order(18)
  void getChildrenByAddress_whenExistedAddressAndNoChildren() {

    //Given
    Map<String, List<Person>> map = new LinkedHashMap<>();
    String existedAddress = "29 15th St";

    //When
    map = classUnderTest.getChildrenByAddress(existedAddress);

    //Then
    List<Person> children = map.get("children");
    List<Person> others = map.get("otherMembers");
    assertThat(children).size().isEqualTo(0);
    assertThat(others).size().isEqualTo(2);
    assertThat(others.get(0).getLastName()).isEqualTo("Marrack");
    assertThat(others.get(0).getFirstName()).isEqualTo("Jonanathan");
    assertThat(others.get(0).getBirthDate()).isNotNull();
    assertThat(others.get(1).getLastName()).isEqualTo("Marrack");
    assertThat(others.get(1).getFirstName()).isEqualTo("Jonathan");
    assertThat(others.get(1).getBirthDate()).isNull();

  }

  @Test
  @Order(19)
  void getChildrenByAddress_whenNoExistedAddress() {

    //Given
    Map<String, List<Person>> map;
    String noExistedAddress = "not existed address";

    //When
    map = classUnderTest.getChildrenByAddress(noExistedAddress);

    //Then
    assertThat(map).isNull();

  }

  @Test
  @Order(20)
  void getPersonsWhenFireMappedByAddress_whenExistedAddressWithoutMedicalRecord() {

    //Given
    List<Person> result;
    String existedAddress = "1509 Av marechal foch";

    //When
    result = classUnderTest.getPersonsWhenFireMappedByAddress(existedAddress);

    //Then
    // Age and MedicalRecord are not specified and not yet created
    //  for this person because he has no medicalRecord
    assertThat(result).size().isEqualTo(1);
    assertThat(result.get(0).getLastName()).isEqualTo("Delaval");
    assertThat(result.get(0).getFirstName()).isEqualTo("Dorian");
    assertThat(result.get(0).getBirthDate()).isNull();
    assertThat(result.get(0).getPhone()).isEqualTo("061-846-0160");
    assertThat(result.get(0).getFireStations().size()).isEqualTo(1);
    assertThat(result.get(0).getMedicalRecord()).isNull();

  }

  @Test
  @Order(20)
  void getPersonsWhenFireMappedByAddress_whenExistedAddressAndMedicalRecord() {

    //Given
    List<Person> result;
    String existedAddress = "29 15th St";

    //When
    result = classUnderTest.getPersonsWhenFireMappedByAddress(existedAddress);

    //Then
    // Age and MedicalRecord are not specified and not yet created
    //  for this person because he has no medicalRecord
    assertThat(result).size().isEqualTo(2);
    assertThat(result.get(0).getLastName()).isEqualTo("Marrack");
    assertThat(result.get(0).getFirstName()).isEqualTo("Jonathan");
    assertThat(result.get(0).getBirthDate()).isNull();
    assertThat(result.get(0).getPhone()).isEqualTo("841-874-6513");
    assertThat(result.get(0).getFireStations().size()).isEqualTo(1);
    assertThat(result.get(0).getMedicalRecord()).isNull();
    assertThat(result.get(1).getLastName()).isEqualTo("Marrack");
    assertThat(result.get(1).getFirstName()).isEqualTo("Jonanathan");
    assertThat(result.get(1).getBirthDate()).isNotNull();
    assertThat(result.get(1).getPhone()).isEqualTo("841-874-6513");
    assertThat(result.get(1).getFireStations().size()).isEqualTo(1);
    assertThat(result.get(1).getMedicalRecord()).isNotNull();
    assertThat(result.get(1).getMedicalRecord().getMedications()).hasSize(0);
    assertThat(result.get(1).getMedicalRecord().getAllergies()).hasSize(0);

  }

  @Test
  @Order(21)
  void getPersonsWhenFireMappedByAddress_whenNoExistedAddress() {

    //Given
    List<Person> result = new ArrayList<>();

    String noExistedAddress = "not existed address";

    //When
    result = classUnderTest.getPersonsWhenFireMappedByAddress(noExistedAddress);

    //Then
    assertThat(result).size().isEqualTo(0);

  }

  @Test
  @Order(22)
  void getPersonsWhenFloodByStation() {

    //Given
    List<Integer> numberStations = Arrays.asList(3, 2);
    Map<String, List<Person>> result;

    //When
    result = classUnderTest.getPersonsWhenFloodByStations(numberStations);
    //Then

    assertThat(result).size().isEqualTo(3);
    assertThat(result.keySet())
        .containsExactlyInAnyOrder("1509 Culver St", "1509 Av marechal foch", "29 15th St");
    assertThat(result.get("1509 Culver St")).hasSize(5);
    assertThat(result.get("1509 Av marechal foch")).hasSize(1);
    assertThat(result.get("29 15th St")).hasSize(2);

    List<Person> persons = result.get("1509 Culver St");

    assertThat(persons.get(0).getLastName()).isEqualTo("Boyd");
    assertThat(persons.get(0).getFirstName()).isEqualTo("John");
    assertThat(persons.get(0).getBirthDate()).isNotNull();
    assertThat(persons.get(0).getPhone()).isEqualTo("841-874-6512");
    assertThat(persons.get(0).getMedicalRecord().getMedications()).isNotEmpty();
    assertThat(persons.get(0).getMedicalRecord().getMedications()).hasSize(2)
        .extracting("IdMedication").isNotNull();
    assertThat(persons.get(0).getMedicalRecord().getMedications()).hasSize(2)
        .extracting("designation").containsExactlyInAnyOrder("aznol", "hydrapermazol");
    assertThat(persons.get(0).getMedicalRecord().getMedications()).hasSize(2)
        .extracting("posology").containsExactlyInAnyOrder("350mg", "100mg");
    assertThat(persons.get(0).getMedicalRecord().getAllergies()).hasSize(1)
        .extracting("IdAllergy").isNotNull();
    assertThat(persons.get(0).getMedicalRecord().getAllergies()).hasSize(1)
        .extracting("designation").containsExactlyInAnyOrder("nillacilan");
    assertThat(persons.get(1).getFirstName()).isEqualTo("Felicia");
    assertThat(persons.get(1).getBirthDate()).isNotNull();
    assertThat(persons.get(1).getPhone()).isEqualTo("841-874-6544");
    assertThat(persons.get(1).getMedicalRecord().getMedications()).isNotEmpty();
    assertThat(persons.get(1).getMedicalRecord().getMedications()).hasSize(1)
        .extracting("IdMedication").isNotNull();
    assertThat(persons.get(1).getMedicalRecord().getMedications()).hasSize(1)
        .extracting("designation").containsExactlyInAnyOrder("tetracyclaz");
    assertThat(persons.get(1).getMedicalRecord().getMedications()).hasSize(1)
        .extracting("posology").containsExactlyInAnyOrder("650mg");
    assertThat(persons.get(1).getMedicalRecord().getAllergies()).hasSize(1)
        .extracting("IdAllergy").isNotNull();
    assertThat(persons.get(1).getMedicalRecord().getAllergies()).hasSize(1)
        .extracting("designation").containsExactlyInAnyOrder("xilliathal");
    assertThat(persons.get(2).getLastName()).isEqualTo("Boyd");
    assertThat(persons.get(2).getFirstName()).isEqualTo("Jacob");
    assertThat(persons.get(2).getBirthDate()).isNotNull();
    assertThat(persons.get(2).getPhone()).isEqualTo("841-874-6513");
    assertThat(persons.get(2).getMedicalRecord().getMedications()).isNotEmpty();
    assertThat(persons.get(2).getMedicalRecord().getMedications()).hasSize(3)
        .extracting("IdMedication").isNotNull();
    assertThat(persons.get(2).getMedicalRecord().getMedications()).hasSize(3)
        .extracting("designation")
        .containsExactlyInAnyOrder("pharmacol", "terazine", "noznazol");
    assertThat(persons.get(2).getMedicalRecord().getMedications()).hasSize(3)
        .extracting("posology").containsExactlyInAnyOrder("250mg", "10mg", "5000mg");
    assertThat(persons.get(2).getMedicalRecord().getAllergies()).hasSize(0);
    assertThat(persons.get(3).getLastName()).isEqualTo("Boyd");
    assertThat(persons.get(3).getFirstName()).isEqualTo("Tenley");
    assertThat(persons.get(3).getBirthDate()).isNotNull();
    assertThat(persons.get(3).getMedicalRecord().getMedications()).isEmpty();
    assertThat(persons.get(3).getMedicalRecord().getAllergies()).hasSize(1)
        .extracting("IdAllergy").isNotNull();
    assertThat(persons.get(3).getMedicalRecord().getAllergies()).hasSize(1)
        .extracting("designation").containsExactlyInAnyOrder("peanut");
    assertThat(persons.get(3).getPhone()).isEqualTo("841-874-6512");
    assertThat(persons.get(4).getLastName()).isEqualTo("Boyd");
    assertThat(persons.get(4).getFirstName()).isEqualTo("Roger");
    assertThat(persons.get(4).getBirthDate()).isNotNull();
    assertThat(persons.get(4).getPhone()).isEqualTo("841-874-6512");
    assertThat(persons.get(4).getMedicalRecord().getMedications()).isEmpty();
    assertThat(persons.get(4).getMedicalRecord().getAllergies()).isEmpty();
    assertThat(persons.get(1).getLastName()).isEqualTo("Boyd");


    persons = result.get("1509 Av marechal foch");
    assertThat(persons.get(0).getLastName()).isEqualTo("Delaval");
    assertThat(persons.get(0).getFirstName()).isEqualTo("Dorian");
    assertThat(persons.get(0).getBirthDate()).isNull();
    assertThat(persons.get(0).getPhone()).isEqualTo("061-846-0160");
    assertThat(persons.get(0).getMedicalRecord()).isNull();

    persons = result.get("29 15th St");
    assertThat(persons.get(0).getLastName()).isEqualTo("Marrack");
    assertThat(persons.get(0).getFirstName()).isEqualTo("Jonathan");
    assertThat(persons.get(0).getBirthDate()).isNull();
    assertThat(persons.get(0).getPhone()).isEqualTo("841-874-6513");
    assertThat(persons.get(0).getMedicalRecord()).isNull();
    assertThat(persons.get(1).getLastName()).isEqualTo("Marrack");
    assertThat(persons.get(1).getFirstName()).isEqualTo("Jonanathan");
    assertThat(persons.get(1).getBirthDate()).isNotNull();
    assertThat(persons.get(1).getPhone()).isEqualTo("841-874-6513");
    assertThat(persons.get(1).getMedicalRecord()).isNotNull();
    assertThat(persons.get(1).getMedicalRecord().getMedications()).isEmpty();
    assertThat(persons.get(1).getMedicalRecord().getAllergies()).isEmpty();

  }

  @Test
  @Order(23)
  void getPersonsWhenFloodByStation_whenNobodyMappedWithAddressesStation() {

    //Given
    List<Integer> numberStations = Arrays.asList(4);
    Map<String, List<Person>> result = new HashMap<>();

    //When
    result = classUnderTest.getPersonsWhenFloodByStations(numberStations);

    //Then
    assertThat(result).isEmpty();

  }

  @Test
  @Order(24)
  void getPersonInfoByName_whenExistedFirstNameAndLastName() {

    //given
    String lastName = "Boyd";
    String firstName = "John";
    Set<Person> result;

    //When
    result = classUnderTest.getPersonInfoByNames(firstName, lastName);

    //then
    Person firstPerson = result.iterator().next();
    assertThat(firstPerson.getLastName()).isEqualTo("Boyd");
    assertThat(firstPerson.getAddress()).isEqualTo("1509 Culver St");
    assertThat(firstPerson.getBirthDate()).isNotNull();
    assertThat(firstPerson.getEmail()).isEqualTo("jaboyd@email.com");
    assertThat(firstPerson.getMedicalRecord().getMedications()).isNotEmpty();

  }

}
