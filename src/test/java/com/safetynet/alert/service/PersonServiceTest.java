package com.safetynet.alert.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.safetynet.alert.model.Allergy;
import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.model.MedicalRecord;
import com.safetynet.alert.model.Medication;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.repository.PersonRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
class PersonServiceTest {

  @Mock
  private PersonRepository personRepository;

  @InjectMocks
  private PersonService classUnderTest;

  private Person mockPerson;
  private FireStation mockFireStation;
  private Set<String> mockAddresses;
  private Set<Person> mockPersons;
  private Date birthDate;

  @BeforeEach
  void setUp() throws Exception {

    birthDate = new Date(System.currentTimeMillis() - 3600);

    mockPerson = new Person(1L, "Dorian", "Delaval",
                            birthDate, "26 av Marechal Foch",
                            "Culver", 97451, "061-846-0160", "dd@email.com", null,
                            new HashSet());
    mockAddresses = new LinkedHashSet<>();
    mockAddresses.add("26 av Marechal Foch");

    mockPersons = new LinkedHashSet<>();
    mockPersons.add(mockPerson);

    mockFireStation = new FireStation(1L, 1, mockAddresses, mockPersons);
    mockPerson.addFireStation(mockFireStation);

  }

  @Test
  @Order(1)
  void getPersonById() {

    //Given
    when(personRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockPerson));

    //when
    Optional<Person> personById = classUnderTest.getPersonById(1L);

    //Then
    assertThat(personById.get().getLastName()).isEqualTo("Delaval");
    assertThat(personById.get().getFirstName()).isEqualTo("Dorian");
    assertThat(personById.get().getBirthDate()).isEqualTo(birthDate);
    assertThat(personById.get().getAddress()).isEqualTo("26 av Marechal Foch");
    assertThat(personById.get().getCity()).isEqualTo("Culver");
    assertThat(personById.get().getZip()).isEqualTo(97451);
    assertThat(personById.get().getPhone()).isEqualTo("061-846-0160");
    assertThat(personById.get().getEmail()).isEqualTo("dd@email.com");
    assertThat(personById.get().getMedicalRecord()).isNull();
    assertThat(personById.get().getFireStations()).isNotNull();

    verify(personRepository, times(1)).findById(Mockito.anyLong());

  }

  @Test
  @Order(2)
  void getPersonById_whenNotFoundId() {

    //given
    when(personRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
    //when
    Optional<Person> personWithInvalidId = classUnderTest.getPersonById(1L);

    //then
    assertThat(personWithInvalidId).isEmpty();

  }

  @Test
  @Order(3)
  void getPersons_whenExistedPersons() {

    //Given
    List<Person> mockPersons = new ArrayList<Person>();
    mockPersons.add(mockPerson);

    when(personRepository.findAll()).thenReturn(mockPersons);

    //When
    Iterable<Person> persons = classUnderTest.getPersons();
    Person person = persons.iterator().next();

    //then
    Person firstPerson = persons.iterator().next();
    assertThat(firstPerson.getLastName()).isEqualTo("Delaval");
    assertThat(firstPerson.getFirstName()).isEqualTo("Dorian");
    assertThat(firstPerson.getBirthDate()).isEqualTo(birthDate);
    assertThat(firstPerson.getAddress()).isEqualTo("26 av Marechal Foch");
    assertThat(firstPerson.getCity()).isEqualTo("Culver");
    assertThat(firstPerson.getZip()).isEqualTo(97451);
    assertThat(firstPerson.getPhone()).isEqualTo("061-846-0160");
    assertThat(firstPerson.getEmail()).isEqualTo("dd@email.com");
    assertThat(firstPerson.getMedicalRecord()).isNull();
    assertThat(firstPerson.getFireStations()).isNotNull();

    verify(personRepository, times(1)).findAll();

  }

  @Test
  @Order(4)
  void getPersonsByAddress_whenExistedPersonsAndAddress() {

    //Given
    String address = "26 av Marechal Foch";

    List<Person> mockPersons = new ArrayList<Person>();
    mockPersons.add(mockPerson);

    when(personRepository.getPersonsByAddress(Mockito.anyString())).thenReturn(mockPersons);
    //When
    Iterable<Person> persons = classUnderTest.getPersonsByAddress(address);

    //then
    Person firstPerson = persons.iterator().next();
    assertThat(firstPerson.getLastName()).isEqualTo("Delaval");
    assertThat(firstPerson.getFirstName()).isEqualTo("Dorian");
    assertThat(firstPerson.getBirthDate()).isEqualTo(birthDate);
    assertThat(firstPerson.getAddress()).isEqualTo("26 av Marechal Foch");
    assertThat(firstPerson.getCity()).isEqualTo("Culver");
    assertThat(firstPerson.getZip()).isEqualTo(97451);
    assertThat(firstPerson.getPhone()).isEqualTo("061-846-0160");
    assertThat(firstPerson.getEmail()).isEqualTo("dd@email.com");
    assertThat(firstPerson.getMedicalRecord()).isNull();
    assertThat(firstPerson.getFireStations()).isNotNull();

    verify(personRepository, times(1)).getPersonsByAddress(Mockito.anyString());

  }

  @Test
  @Order(5)
  void getPersonsByAddress_whenNoPersonsAtAddress() {

    //Given
    String address = "addressNotMappedWithPerson";

    when(personRepository.getPersonsByAddress(Mockito.anyString()))
        .thenReturn(new ArrayList());

    //When
    Iterable<Person> persons = classUnderTest.getPersonsByAddress(address);

    //then
    assertThat(persons).isEmpty();
    verify(personRepository, times(1)).getPersonsByAddress(Mockito.anyString());

  }

  @Test
  @Order(6)
  void getPersonByNames_whenPersonExisted() {

    //given
    String lastName = "Delaval";
    String firstName = "Dorian";

    when(personRepository.getOneByNames(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(Optional.of(mockPerson));
    //when
    Optional<Person> person = classUnderTest.getPersonByNames(firstName, lastName);

    //then
    Person firstPerson = person.get();
    assertThat(firstPerson.getLastName()).isEqualTo("Delaval");
    assertThat(firstPerson.getFirstName()).isEqualTo("Dorian");
    assertThat(firstPerson.getBirthDate()).isEqualTo(birthDate);
    assertThat(firstPerson.getAddress()).isEqualTo("26 av Marechal Foch");
    assertThat(firstPerson.getCity()).isEqualTo("Culver");
    assertThat(firstPerson.getZip()).isEqualTo(97451);
    assertThat(firstPerson.getPhone()).isEqualTo("061-846-0160");
    assertThat(firstPerson.getEmail()).isEqualTo("dd@email.com");
    assertThat(firstPerson.getMedicalRecord()).isNull();
    assertThat(firstPerson.getFireStations()).isNotNull();

    verify(personRepository, times(1)).getOneByNames(Mockito.anyString(), Mockito.anyString());

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

    when(personRepository.getOneByNames(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(Optional.empty());

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
    addresses.add("26 av Marechal Foch");

    when(personRepository.getOneJoinFireStationsById(Mockito.anyLong()))
        .thenReturn(Optional.of(mockPerson));

    //when
    Optional<Person> person = classUnderTest.getPersonJoinFireStationById(idPerson);

    //then
    Person firstPerson = person.get();
    assertThat(firstPerson.getLastName()).isEqualTo("Delaval");
    assertThat(firstPerson.getFirstName()).isEqualTo("Dorian");
    assertThat(firstPerson.getBirthDate()).isEqualTo(birthDate);
    assertThat(firstPerson.getAddress()).isEqualTo("26 av Marechal Foch");
    assertThat(firstPerson.getCity()).isEqualTo("Culver");
    assertThat(firstPerson.getZip()).isEqualTo(97451);
    assertThat(firstPerson.getPhone()).isEqualTo("061-846-0160");
    assertThat(firstPerson.getEmail()).isEqualTo("dd@email.com");
    assertThat(firstPerson.getMedicalRecord()).isNull();
    assertThat(firstPerson.getFireStations()).isNotNull();
    firstPerson.getFireStations().forEach(fireStation -> {

      assertThat(fireStation.getAddresses()).hasSize(1).containsAnyElementsOf(addresses);
      assertThat(fireStation.getIdFireStation()).isNotNull();
    });

  }

  @Test
  @Order(9)
  void getPersonJoinFireStation_whenNoExistedPerson() {

    //given
    Long idPerson = 10L;

    when(personRepository.getOneJoinFireStationsById(Mockito.anyLong()))
        .thenReturn(Optional.empty());
    //when
    Optional<Person> person = classUnderTest.getPersonJoinFireStationById(idPerson);

    //then
    assertThat(person).isEmpty();

  }

  @Test
  @Order(10)
  void savePerson_whenExistedPerson() {

    //given
    when(personRepository.save(Mockito.any(Person.class))).thenReturn(mockPerson);
    //when
    Person personSaved = classUnderTest.savePerson(mockPerson);

    //then
    assertThat(personSaved.getIdPerson()).isNotNull();
    assertThat(personSaved.getLastName()).isEqualTo("Delaval");
    assertThat(personSaved.getFirstName()).isEqualTo("Dorian");
    assertThat(personSaved.getBirthDate()).isAfterOrEqualTo(birthDate);
    assertThat(personSaved.getAddress()).isEqualTo("26 av Marechal Foch");
    assertThat(personSaved.getCity()).isEqualTo("Culver");
    assertThat(personSaved.getZip()).isEqualTo(97451);
    assertThat(personSaved.getPhone()).isEqualTo("061-846-0160");
    assertThat(personSaved.getEmail()).isEqualTo("dd@email.com");
    assertThat(personSaved.getMedicalRecord()).isNull();
    assertThat(personSaved.getFireStations()).isNotNull();

  }

  @Test
  @Order(11)
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
  @Order(12)
  void deletePerson_whenExistedPerson() {

    //given
    //When
    classUnderTest.deletePerson(mockPerson);

    //then
    ArgumentCaptor personCaptor = ArgumentCaptor.forClass(Person.class);
    verify(personRepository, times(1)).delete((Person) personCaptor.capture());

  }

  @Test
  @Order(13)
  void deletePerson_whenNullPerson() {

    //given
    Person personToDelete = null;

    //when
    //then
    assertThrows("In PersonService.deletePerson(person): Person is null!",
        NullPointerException.class,
        (
        ) -> {

          classUnderTest.deletePerson(personToDelete);
        });
    verify(personRepository, never()).delete(Mockito.any(Person.class));

  }

  @Test
  @Order(14)
  void getPersonsMappedWithFireStation_whenExistedFireStationAndOnlyChildren() {

    //Given
    Map<String, Object> map = new LinkedHashMap<>();
    int existedNumberStation = 3;

    when(personRepository.getPersonsMappedByNumberStation(Mockito.anyInt()))
        .thenReturn(new ArrayList(mockPersons));

    //When
    map = classUnderTest.getPersonsMappedWithFireStation(existedNumberStation);

    //Then
    assertThat(map.get("ChildrenCount")).isEqualTo(1);
    assertThat(map.get("AdultCount")).isEqualTo(0);
    assertThat(map.get("persons")).isEqualTo(new ArrayList(mockPersons));

  }

  @Test
  @Order(15)
  void getPersonsMappedWithFireStation_whenExistedFireStationAndOnlyAdult() {

    //Given

    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -20);
    Date birthDate = cal.getTime();
    mockPerson.setBirthDate(birthDate);

    when(personRepository.getPersonsMappedByNumberStation(Mockito.anyInt()))
        .thenReturn(new ArrayList(mockPersons));

    Map<String, Object> map = new LinkedHashMap<>();
    int existedNumberStation = 3;

    //When
    map = classUnderTest.getPersonsMappedWithFireStation(existedNumberStation);

    //Then
    assertThat(map.get("ChildrenCount")).isEqualTo(0);
    assertThat(map.get("AdultCount")).isEqualTo(1);
    assertThat(map.get("persons")).isEqualTo(new ArrayList(mockPersons));

  }

  @Test
  @Order(16)
  void getPersonsMappedWithFireStation_whenExistedFireStationAndPersonHasNoBirthDate() {

    //Given
    mockPerson.setBirthDate(null);

    when(personRepository.getPersonsMappedByNumberStation(Mockito.anyInt()))
        .thenReturn(new ArrayList(mockPersons));

    Map<String, Object> map = new LinkedHashMap<>();
    int existedNumberStation = 3;

    //When
    map = classUnderTest.getPersonsMappedWithFireStation(existedNumberStation);

    //Then
    assertThat(map.get("ChildrenCount")).isEqualTo(0);
    assertThat(map.get("AdultCount")).isEqualTo(0);
    assertThat(map.get("BirthDateNotSpecified")).isEqualTo(1);
    assertThat(map.get("persons")).isEqualTo(new ArrayList(mockPersons));

  }

  @Test
  @Order(17)
  void getPersonsMappedWithFireStation_whenNonExistedFireStationOrNoPersonMapped() {

    //Given
    Map<String, Object> map = new LinkedHashMap<>();
    int noExistedNumberStation = 5;

    when(personRepository.getPersonsMappedByNumberStation(Mockito.anyInt()))
        .thenReturn(new ArrayList<>());

    //When
    map = classUnderTest.getPersonsMappedWithFireStation(noExistedNumberStation);

    //then
    assertThat(map.get("AdultCount")).isEqualTo(0);
    assertThat(map.get("ChildrenCount")).isEqualTo(0);
    assertThat(map.get("persons")).asList().size().isEqualTo(0);

  }

  @Test
  @Order(18)
  void getChildrenByAddress_whenExistedAddressAndChildren() {

    //Given
    Map<String, List<Person>> map = new LinkedHashMap<>();
    String existedAddress = "26 av Marechal Foch";

    when(personRepository.getChildrenByAddress(Mockito.anyString())).thenReturn(mockPersons);

    //When
    map = classUnderTest.getChildrenByAddress(existedAddress);

    //Then
    List<Person> children = map.get("children");
    List<Person> others = map.get("otherMembers");
    assertThat(children).size().isEqualTo(1);
    assertThat(children.get(0).getLastName()).isEqualTo("Delaval");
    assertThat(children.get(0).getFirstName()).isEqualTo("Dorian");
    assertThat(children.get(0).getBirthDate()).isNotNull();
    assertThat(others).size().isEqualTo(0);

  }

  @Test
  @Order(19)
  void getChildrenByAddress_whenExistedAddressAndNoChildren() {

    //Given
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -20);
    Date birthDate = cal.getTime();
    mockPerson.setBirthDate(birthDate);

    when(personRepository.getChildrenByAddress(Mockito.anyString())).thenReturn(mockPersons);

    Map<String, List<Person>> map = new LinkedHashMap<>();
    String existedAddress = "26 av Marechal Foch";

    //When
    map = classUnderTest.getChildrenByAddress(existedAddress);

    //Then
    List<Person> children = map.get("children");
    List<Person> others = map.get("otherMembers");
    assertThat(children).size().isEqualTo(0);
    assertThat(others).size().isEqualTo(1);
    assertThat(others.get(0).getLastName()).isEqualTo("Delaval");
    assertThat(others.get(0).getFirstName()).isEqualTo("Dorian");
    assertThat(others.get(0).getBirthDate()).isNotNull();

  }

  @Test
  @Order(19)
  void getChildrenByAddress_whenExistedAddressAndPersonBirthDateNull() {

    //Given
    Map<String, List<Person>> map = new LinkedHashMap<>();
    String existedAddress = "26 av Marechal Foch";

    mockPerson.setBirthDate(null);

    when(personRepository.getChildrenByAddress(Mockito.anyString())).thenReturn(mockPersons);

    //When
    map = classUnderTest.getChildrenByAddress(existedAddress);

    //Then
    List<Person> children = map.get("children");
    List<Person> others = map.get("otherMembers");
    assertThat(children).size().isEqualTo(0);
    assertThat(others).size().isEqualTo(1);
    assertThat(others.get(0).getLastName()).isEqualTo("Delaval");
    assertThat(others.get(0).getFirstName()).isEqualTo("Dorian");
    assertThat(others.get(0).getBirthDate()).isNull();

  }

  @Test
  @Order(19)
  void getChildrenByAddress_whenNoExistedAddress() {

    //Given
    Map<String, List<Person>> map;
    String noExistedAddress = "not existed address";

    when(personRepository.getChildrenByAddress(Mockito.anyString()))
        .thenReturn(new ArrayList());

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
    String existedAddress = "26 Av Marechal Foch";

    when(personRepository.getPersonsWhenFire(Mockito.anyString()))
        .thenReturn(new ArrayList(mockPersons));

    //When
    result = classUnderTest.getPersonsWhenFireMappedByAddress(existedAddress);

    //Then
    // Age and MedicalRecord are not specified and not yet created
    //  for this person because he has no medicalRecord
    assertThat(result).size().isEqualTo(1);
    assertThat(result.get(0).getLastName()).isEqualTo("Delaval");
    assertThat(result.get(0).getFirstName()).isEqualTo("Dorian");
    assertThat(result.get(0).getBirthDate()).isNotNull();
    assertThat(result.get(0).getPhone()).isEqualTo("061-846-0160");
    assertThat(result.get(0).getFireStations().size()).isEqualTo(1);
    assertThat(result.get(0).getMedicalRecord()).isNull();

  }

  @Test
  @Order(21)
  void getPersonsWhenFireMappedByAddress_whenExistedAddressAndMedicalRecord() {

    //Given
    List<Person> result;
    String existedAddress = "26 Av Marechal Foch";

    MedicalRecord mockMedicalRecord =
        new MedicalRecord(1L, mockPerson, new HashSet<Medication>(), new HashSet<Allergy>());
    mockPerson.setMedicalRecord(mockMedicalRecord);

    when(personRepository.getPersonsWhenFire(Mockito.anyString()))
        .thenReturn(new ArrayList(mockPersons));

    //When
    result = classUnderTest.getPersonsWhenFireMappedByAddress(existedAddress);

    //Then
    // Age and MedicalRecord are not specified and not yet created
    //  for this person because he has no medicalRecord
    assertThat(result).size().isEqualTo(1);
    assertThat(result.get(0).getLastName()).isEqualTo("Delaval");
    assertThat(result.get(0).getFirstName()).isEqualTo("Dorian");
    assertThat(result.get(0).getBirthDate()).isNotNull();
    assertThat(result.get(0).getPhone()).isEqualTo("061-846-0160");
    assertThat(result.get(0).getFireStations().size()).isEqualTo(1);
    assertThat(result.get(0).getMedicalRecord()).isNotNull();

  }

  @Test
  @Order(22)
  void getPersonsWhenFireMappedByAddress_whenNoExistedAddress() {

    //Given
    List<Person> result = new ArrayList<>();
    String noExistedAddress = "not existed address";

    when(personRepository.getPersonsWhenFire(Mockito.anyString()))
        .thenReturn(new ArrayList());

    //When
    result = classUnderTest.getPersonsWhenFireMappedByAddress(noExistedAddress);

    //Then
    assertThat(result).size().isEqualTo(0);

  }

  @Test
  @Order(23)
  void getPersonsWhenFloodByStation() {

    //Given

    //add new mockPerson for fireStation n°2
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -20);
    Date birthDate = cal.getTime();
    Person mockPerson2 = new Person(2L, "Bernard", "Delaval", birthDate,
                                    "310 Rue Jean Jaures", "Culver", 97451, "061-846-0260",
                                    "db@email.com", null, null);
    mockPersons.add(mockPerson2);

    when(personRepository.getPersonsWhenFlood(Mockito.anyInt()))
        .thenReturn(new ArrayList(mockPersons));

    List<Integer> numberStations = Arrays.asList(1, 2);
    Map<String, List<Person>> result;

    //When
    result = classUnderTest.getPersonsWhenFloodByStations(numberStations);
    //Then

    assertThat(result).size().isEqualTo(2);
    assertThat(result.keySet())
        .containsExactlyInAnyOrder("26 av Marechal Foch", "310 Rue Jean Jaures");
    assertThat(result.get("26 av Marechal Foch")).hasSize(1);
    assertThat(result.get("310 Rue Jean Jaures")).hasSize(1);

    List<Person> persons = result.get("26 av Marechal Foch");

    assertThat(persons.get(0).getLastName()).isEqualTo("Delaval");
    assertThat(persons.get(0).getFirstName()).isEqualTo("Dorian");
    assertThat(persons.get(0).getBirthDate()).isNotNull();
    assertThat(persons.get(0).getPhone()).isEqualTo("061-846-0160");
    assertThat(persons.get(0).getMedicalRecord()).isNull();

    persons = result.get("310 Rue Jean Jaures");
    assertThat(persons.get(0).getLastName()).isEqualTo("Delaval");
    assertThat(persons.get(0).getFirstName()).isEqualTo("Bernard");
    assertThat(persons.get(0).getBirthDate()).isNotNull();
    assertThat(persons.get(0).getPhone()).isEqualTo("061-846-0260");
    assertThat(persons.get(0).getMedicalRecord()).isNull();

  }

  @Test
  @Order(24)
  void getPersonsWhenFloodByStation_whenNobodyMappedWithAddressesStation() {

    //Given
    List<Integer> numberStations = Arrays.asList(4);
    Map<String, List<Person>> result = new HashMap<>();

    when(personRepository.getPersonsWhenFlood(Mockito.anyInt()))
        .thenReturn(new ArrayList());

    //When
    result = classUnderTest.getPersonsWhenFloodByStations(numberStations);

    //Then
    assertThat(result).isEmpty();

  }

  @Test
  @Order(25)
  void getPersonInfoByName_whenExistedFirstNameAndLastName() {

    //given
    //add new mockPerson for fireStation n°2
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -20);
    Date birthDate = cal.getTime();
    Person mockPerson2 = new Person(2L, "Delaval", "Bernard", birthDate,
                                    "310 Rue Jean Jaures", "Culver", 97451, "061-846-0260",
                                    "db@email.com", null, null);
    mockPersons.add(mockPerson2);

    when(personRepository.getPersonInfoByNames(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(new ArrayList(mockPersons));

    String lastName = "Delaval";
    String firstName = "Dorian";
    Set<Person> result;

    //When
    result = classUnderTest.getPersonInfoByNames(firstName, lastName);

    //then
    Person firstPerson = result.iterator().next();
    assertThat(firstPerson.getLastName()).isEqualTo("Delaval");
    assertThat(firstPerson.getAddress()).isEqualTo("26 av Marechal Foch");
    assertThat(firstPerson.getBirthDate()).isNotNull();
    assertThat(firstPerson.getEmail()).isEqualTo("dd@email.com");
    assertThat(firstPerson.getMedicalRecord()).isNull();

  }
}
