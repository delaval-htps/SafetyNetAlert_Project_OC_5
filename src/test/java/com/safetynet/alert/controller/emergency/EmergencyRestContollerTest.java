package com.safetynet.alert.controller.emergency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.safetynet.alert.DTO.PersonDto;
import com.safetynet.alert.exceptions.address.AddressNotFoundException;
import com.safetynet.alert.exceptions.firestation.FireStationNotFoundException;
import com.safetynet.alert.exceptions.person.PersonNotFoundException;
import com.safetynet.alert.model.Allergy;
import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.model.MedicalRecord;
import com.safetynet.alert.model.Medication;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.AllergyService;
import com.safetynet.alert.service.FireStationService;
import com.safetynet.alert.service.MedicationService;
import com.safetynet.alert.service.PersonService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@WebMvcTest(controllers = EmergencyRestController.class)
@TestMethodOrder(OrderAnnotation.class)
class EmergencyRestContollerTest {

  @MockBean
  private PersonService personService;

  @MockBean
  private FireStationService fireStationService;

  @MockBean
  private MedicationService medicationService;

  @MockBean
  private AllergyService allergyService;

  @Autowired
  private MockMvc mockMvc;

  private Person mockPerson1;
  private Person mockPerson2;
  private Person mockPerson3;
  private Person mockPerson4;
  private Person mockPerson5;

  private FireStation mockFireStation;
  private FireStation mockFireStation2;
  private Set<FireStation> mockSetFireStation;
  private Set<FireStation> mockSetFireStation2;

  private Medication mockMedication1;
  private Medication mockMedication2;
  private Allergy mockAllergy1;

  private MedicalRecord mockMedicalRecord;

  private Map<String, Object> mapReponsebody;
  private Map<String, List<Person>> mapresult;
  private Map<String, List<PersonDto>> mapResultWithMedicalRecord;

  private Set<String> addresses;
  private Set<String> addresses2;

  private Set<Person> persons;
  private Set<Person> persons2;

  private LinkedHashSet<Medication> medications;
  private Set<Allergy> allergies;

  private Date birthDatePerson3;



  @BeforeEach
  void setUp() throws Exception {

    mockPerson1 =
        new Person(null, "Person1", "Test",
                   new SimpleDateFormat("MM/dd/yyyy").parse("12/27/1976"), "address1",
                   null, null, "061-846-0160", null, null, mockSetFireStation);
    mockPerson2 =
        new Person(null, "Person2", "Test",
                   new SimpleDateFormat("MM/dd/yyyy").parse("02/22/1984"), "address1",
                   null, null, "061-846-0161", null, null, mockSetFireStation);

    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -18);
    birthDatePerson3 = cal.getTime();

    mockPerson3 =
        new Person(null, "Person3", "Test",
                   birthDatePerson3, "address1",
                   null, null, "061-846-0162", null, null, mockSetFireStation);
    mockPerson4 =
        new Person(null, "Person4", "Test",
                   birthDatePerson3, "address2",
                   null, null, "061-846-0163", null, null, mockSetFireStation);

    mockPerson5 =
        new Person(null, "Person5", "Test",
                   birthDatePerson3, "address3",
                   null, null, "061-846-0164", null, null, mockSetFireStation2);

    medications = new LinkedHashSet<>();
    allergies = new HashSet<>();

    mockMedication1 = new Medication(1L, "medication1", "100mg", null);
    mockMedication2 = new Medication(2L, "medication2", "100mg", null);

    mockAllergy1 = new Allergy(1L, "allergy1", null);

    medications.add(mockMedication1);
    medications.add(mockMedication2);

    allergies.add(mockAllergy1);

    addresses = new HashSet<String>();
    addresses2 = new HashSet<String>();
    addresses.add("address1");
    addresses.add("address2");
    addresses2.add("address3");

    persons = new LinkedHashSet<Person>();
    persons2 = new LinkedHashSet<Person>();
    persons.add(mockPerson1);
    persons.add(mockPerson2);
    persons.add(mockPerson3);
    persons2.add(mockPerson5);

    mockFireStation = new FireStation(1L, 1, addresses, persons);
    mockFireStation2 = new FireStation(2L, 2, addresses2, persons2);
    mockSetFireStation = new HashSet<>();
    mockSetFireStation.add(mockFireStation);
    mockSetFireStation2 = new HashSet<>();
    mockSetFireStation2.add(mockFireStation2);
    mockMedicalRecord = new MedicalRecord(1L, mockPerson3, medications, allergies);
    mapReponsebody = new LinkedHashMap<>();
    mapresult = new LinkedHashMap<>();
    mapResultWithMedicalRecord = new LinkedHashMap<>();

  }

  @Test
  @Order(1)
  void getPersonsMappedWithFireStation_whenNumberStationExists_thenReturn200()
      throws Exception {

    //Given
    mapReponsebody.put("AdultCount", 2);
    mapReponsebody.put("ChildrenCount", 1);
    mapReponsebody.put("persons", new ArrayList<>(persons));

    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.of(mockFireStation));

    when(personService.getPersonsMappedWithFireStation(Mockito.anyInt()))
        .thenReturn(mapReponsebody);

    //When and Then
    mockMvc.perform(get("/firestation/getpersons").param("stationNumber", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(3)))
        .andExpect(jsonPath("$.persons[0].firstName", is("Person1")))
        .andExpect(jsonPath("$.persons[0].lastName", is("Test")))
        .andExpect(jsonPath("$.persons[0].phone", is("061-846-0160")))
        .andExpect(jsonPath("$.persons[1].firstName", is("Person2")))
        .andExpect(jsonPath("$.persons[1].lastName", is("Test")))
        .andExpect(jsonPath("$.persons[1].phone", is("061-846-0161")))
        .andExpect(jsonPath("$.persons[2].firstName", is("Person3")))
        .andExpect(jsonPath("$.persons[2].lastName", is("Test")))
        .andExpect(jsonPath("$.persons[2].phone", is("061-846-0162")))
        .andExpect(jsonPath("$.AdultCount", is(2)))
        .andExpect(jsonPath("$.ChildrenCount", is(1))).andDo(print());

  }

  @Test
  @Order(2)
  void getPersonsMappedWithFireStation_whenFireStationNotMappedWithAddrress_thenReturn200()
      throws Exception {

    //Given
    mapReponsebody.put("AdultCount", 0);
    mapReponsebody.put("ChildrenCount", 0);
    mapReponsebody.put("persons", new ArrayList<>());

    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.of(mockFireStation));

    when(personService.getPersonsMappedWithFireStation(Mockito.anyInt()))
        .thenReturn(mapReponsebody);
    //When and Then
    mockMvc.perform(get("/firestation/getpersons").param("stationNumber", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.AdultCount", is(0)))
        .andExpect(jsonPath("$.ChildrenCount", is(0))).andDo(print());

  }

  @Test
  @Order(3)
  void getPersonsMappedWithFireStation_whenNumberStationDoesntExist_thenReturn404()
      throws Exception {

    //Given
    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.empty());

    //When and Then
    MvcResult result =
        mockMvc.perform(get("/firestation/getpersons").param("stationNumber", "5"))
            .andExpect(status().isNotFound()).andReturn();

    assertThat(result.getResolvedException()).isInstanceOf(FireStationNotFoundException.class);
    assertThat(result.getResolvedException().getMessage()).isEqualTo(
        "FireStation with number_station: 5 was not found! please choose another existed one!");

  }

  @Test
  @Order(4)
  void getChildAlert_whenValidAddressAndExistedChildren_thenReturn200() throws Exception {

    //Given
    List<Person> children = new ArrayList();
    children.add(mockPerson3);

    List<Person> otherMembers = new ArrayList();
    otherMembers.add(mockPerson1);
    otherMembers.add(mockPerson2);


    mapresult.put("Children", children);
    mapresult.put("OtherMembers", otherMembers);

    when(personService.getChildrenByAddress(Mockito.anyString())).thenReturn(mapresult);

    //when and then
    mockMvc.perform(get("/childAlert").param("address", "1509 Culver St"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(2)))
        .andExpect(jsonPath("$.Children.length()", is(1)))
        .andExpect(jsonPath("$.Children[0].name", is("Test Person3")))
        .andExpect(jsonPath("$.Children[0].age", notNullValue()))
        .andExpect(jsonPath("$.OtherMembers.length()", is(2)))
        .andExpect(jsonPath("$.OtherMembers[0].name", is("Test Person1")))
        .andExpect(jsonPath("$.OtherMembers[0].age", notNullValue()))
        .andExpect(jsonPath("$.OtherMembers[1].name", is("Test Person2")))
        .andExpect(jsonPath("$.OtherMembers[1].age", notNullValue()))
        .andDo(print());


  }

  @Test
  @Order(5)
  void getChildAlert_whenNotExistedAddress_thenReturn404() throws Exception {

    //Given
    when(personService.getChildrenByAddress(Mockito.anyString())).thenReturn(null);

    //when and then
    MvcResult result = mockMvc.perform(get("/childAlert").param("address", "AddressNotFound"))
        .andExpect(status().isNotFound()).andReturn();

    assertThat(result.getResolvedException()).isInstanceOf(AddressNotFoundException.class);
    assertThat(result.getResolvedException().getMessage()).isEqualTo(
        "this address : AddressNotFound was not found. Please choose a existed address");

  }

  @Test
  @Order(6)
  void getPhoneAlert_whenExistedFireStation_thenReturn200() throws Exception {

    //Given

    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.of(mockFireStation));

    //When and Then
    mockMvc.perform(get("/phoneAlert").param("firestation", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.phones.length()", is(3)))
        .andExpect(jsonPath("$.phones[0]", is("061-846-0160")))
        .andExpect(jsonPath("$.phones[1]", is("061-846-0161")))
        .andExpect(jsonPath("$.phones[2]", is("061-846-0162")))
        .andDo(print());

  }

  @Test
  @Order(7)
  void getphoneAlert_whenNotExistedFireStation_thenReturn404() throws Exception {
    //Given

    //when and then
    MvcResult result = mockMvc.perform(get("/phoneAlert").param("firestation", "6"))
        .andExpect(status().isNotFound()).andReturn();

    assertThat(result.getResolvedException()).isInstanceOf(FireStationNotFoundException.class);
    assertThat(result.getResolvedException().getMessage()).isEqualTo(
        "FireStation with numberStation: 6 was not found. Please choose a existed fireStation!");

  }

  @Test
  @Order(8)
  void getPersonsWhenFire_whenValidAddress_thenReturn200() throws Exception {

    //Given
    mockPerson3.setMedicalRecord(mockMedicalRecord);

    List<Person> personsList = new ArrayList<>();
    personsList.add(mockPerson3);

    when(personService.getPersonsWhenFireMappedByAddress(Mockito.anyString()))
        .thenReturn(personsList);

    //when & then
    mockMvc.perform(get("/fire").param("address", "address2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(1)))
        .andExpect(jsonPath("$[0].lastName", is("Test")))
        .andExpect(jsonPath("$[0].phone", is("061-846-0162")))
        .andExpect(jsonPath("$[0].age", notNullValue()))
        .andExpect(jsonPath("$[0].medications.length()", is(2)))
        .andExpect(jsonPath("$[0].medications[0].designation", is("medication1")))
        .andExpect(jsonPath("$[0].medications[0].posology", is("100mg")))
        .andExpect(jsonPath("$[0].medications[1].designation", is("medication2")))
        .andExpect(jsonPath("$[0].medications[1].posology", is("100mg")))
        .andExpect(jsonPath("$[0].allergies.length()", is(1)))
        .andExpect(jsonPath("$[0].allergies[0].designation", is("allergy1")))
        .andDo(print());

  }

  @Test
  @Order(9)
  void getPersonsWhenFire_whenAddressDoesntExist_thenReturn404()
      throws Exception {

    //Given
    List<Person> personsList = new ArrayList<>();

    when(personService.getPersonsWhenFireMappedByAddress(Mockito.anyString()))
        .thenReturn(personsList);

    //When and Then
    MvcResult result =
        mockMvc.perform(get("/fire").param("address", "addressNotFound"))
            .andExpect(status().isNotFound()).andReturn();

    assertThat(result.getResolvedException()).isInstanceOf(AddressNotFoundException.class);
    assertThat(result.getResolvedException().getMessage()).isEqualTo(
        "this address :addressNotFound, was not found "
            + "or nobody lives at this address. Please choose a existed address!");

  }

  @Test
  @Order(10)
  void getPersonsWhenFlood_whenAllExistedStations_thenReturn200() throws Exception {

    //Given

    //the list of numberStations
    List<String> valuesStation = Arrays.asList("1", "2");
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.addAll("stations", valuesStation);

    mockPerson1.setMedicalRecord(mockMedicalRecord);
    mockPerson2.setMedicalRecord(mockMedicalRecord);
    mockPerson3.setMedicalRecord(mockMedicalRecord);


    mapresult.put("address1", new ArrayList<>(persons));


    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.of(mockFireStation), Optional.of(mockFireStation2));

    when(personService.getPersonsWhenFloodByStations(Mockito.anyList()))
        .thenReturn(mapresult);

    //When& then
    mockMvc
        .perform(get("/flood/stations").params(params))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(1)))
        .andExpect(jsonPath("$.address1[0].name", is("Test")))
        .andExpect(jsonPath("$.address1[0].phone", is("061-846-0160")))
        .andExpect(jsonPath("$.address1[0].age", notNullValue()))
        .andExpect(jsonPath("$.address1[0].medications.length()", is(2)))
        .andExpect(jsonPath("$.address1[0].medications[0].designation", is("medication1")))
        .andExpect(jsonPath("$.address1[0].medications[0].posology", is("100mg")))
        .andExpect(jsonPath("$.address1[0].medications[1].designation", is("medication2")))
        .andExpect(jsonPath("$.address1[0].medications[1].posology", is("100mg")))
        .andExpect(jsonPath("$.address1[0].allergies.length()", is(1)))
        .andExpect(jsonPath("$.address1[0].allergies[0].designation", is("allergy1")))
        .andExpect(jsonPath("$.address1[1].name", is("Test")))
        .andExpect(jsonPath("$.address1[1].phone", is("061-846-0161")))
        .andExpect(jsonPath("$.address1[1].age", notNullValue()))
        .andExpect(jsonPath("$.address1[1].medications.length()", is(2)))
        .andExpect(jsonPath("$.address1[1].medications[0].designation", is("medication1")))
        .andExpect(jsonPath("$.address1[1].medications[0].posology", is("100mg")))
        .andExpect(jsonPath("$.address1[1].medications[1].designation", is("medication2")))
        .andExpect(jsonPath("$.address1[1].medications[1].posology", is("100mg")))
        .andExpect(jsonPath("$.address1[1].allergies.length()", is(1)))
        .andExpect(jsonPath("$.address1[1].allergies[0].designation", is("allergy1")))
        .andExpect(jsonPath("$.address1[2].name", is("Test")))
        .andExpect(jsonPath("$.address1[2].phone", is("061-846-0162")))
        .andExpect(jsonPath("$.address1[2].age", notNullValue()))
        .andExpect(jsonPath("$.address1[2].medications.length()", is(2)))
        .andExpect(jsonPath("$.address1[2].medications[0].designation", is("medication1")))
        .andExpect(jsonPath("$.address1[2].medications[0].posology", is("100mg")))
        .andExpect(jsonPath("$.address1[2].medications[1].designation", is("medication2")))
        .andExpect(jsonPath("$.address1[2].medications[1].posology", is("100mg")))
        .andExpect(jsonPath("$.address1[2].allergies.length()", is(1)))
        .andExpect(jsonPath("$.address1[2].allergies[0].designation", is("allergy1")))
        .andDo(print());

  }

  @Test
  @Order(11)
  void getPersonsWhenFlood_whenNoExistedStationInList_thenReturn404() throws Exception {

    //Given
    List<String> valuesStation = Arrays.asList("3");

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.addAll("stations", valuesStation);

    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.empty());

    //When & then

    MvcResult mvcResult = mockMvc.perform(get("/flood/stations").params(params))
        .andExpect(status().isNotFound()).andReturn();

    assertThat(mvcResult.getResolvedException())
        .isInstanceOf(FireStationNotFoundException.class);
    assertThat(mvcResult.getResolvedException().getMessage()).isEqualTo(
        "the Firestation with numberStation: 3 was not found."
            + "Please replace it by existed FireStation");

  }

  @Test
  @Order(12)
  void getPersonsWhenFlood_whenOnePersonHasNoMedicalrecord_thenReturn200() throws Exception {

    //Given
    List<String> values = Arrays.asList("2");

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.addAll("stations", values);


    mapresult.put("address3", new ArrayList<>(persons2));

    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.of(mockFireStation2));

    when(personService.getPersonsWhenFloodByStations(Mockito.anyList()))
        .thenReturn(mapresult);
    //When& then
    mockMvc.perform(get("/flood/stations").params(params))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(1)))
        .andExpect(jsonPath("$.address3[0].lastName", is("Test")))
        .andExpect(jsonPath("$.address3[0].phone", is("061-846-0164")))
        .andExpect(jsonPath("$.address3[0].age", notNullValue()))
        .andExpect(jsonPath("$.address3[0].medicalRecord", is("not yet created")))
        .andDo(print());

  }

  @Test
  @Order(13)
  void getPersonInfo_whenExistedPerson_thenReturn200() throws Exception {

    //given
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("firstName", "Person3");
    map.add("lastName", "Test");

    mockPerson3.setMedicalRecord(mockMedicalRecord);
    mockPerson3.setEmail("person3@email.com");

    List<Person> mockInformations = new ArrayList<>();
    mockInformations.add(mockPerson3);

    when(personService.getPersonInfoByNames(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(mockInformations);

    //when & Then
    mockMvc.perform(get("/personInfo").params(map)).andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(1)))
        .andExpect(jsonPath("$[0].lastName", is("Test")))
        .andExpect(jsonPath("$[0].address", is("address1")))
        .andExpect(jsonPath("$[0].age", notNullValue()))
        .andExpect(jsonPath("$[0].email", is("person3@email.com")))
        .andExpect(jsonPath("$[0].medications.length()", is(2)))
        .andExpect(jsonPath("$[0].medications[0].designation", is("medication1")))
        .andExpect(jsonPath("$[0].medications[0].posology", is("100mg")))
        .andExpect(jsonPath("$[0].medications[1].designation",
            is("medication2")))
        .andExpect(jsonPath("$[0].medications[1].posology", is("100mg")))
        .andExpect(jsonPath("$[0].allergies.length()", is(1)))
        .andExpect(
            jsonPath("$[0].allergies[0].designation", is("allergy1")))
        .andDo(print());

  }

  @Test
  @Order(14)
  void getPersonsInfo_whenNoExistedPerson_thenReturn404() throws Exception {

    //Given

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("firstName", "Emilie");
    map.add("lastName", "Baudouin");

    List<Person> mockInformations = new ArrayList<>();
    when(personService.getPersonInfoByNames(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(mockInformations);

    //When & then

    MvcResult mvcResult = mockMvc.perform(get("/personInfo").params(map))
        .andExpect(status().isNotFound()).andReturn();

    assertThat(mvcResult.getResolvedException())
        .isInstanceOf(PersonNotFoundException.class);
    assertThat(mvcResult.getResolvedException().getMessage()).isEqualTo(
        "Person with firstName: Emilie and lastName: Baudouin was not found."
            + "Please choose another names!");

  }

  @Test
  @Order(15)
  void getEmailsByCity_whenExistedCity_thenReturn200() throws Exception {

    //given
    int i = 0;
    List<String> mockEmails = new ArrayList<>();

    for (Person person : persons) {

      person.setCity("CityTest");
      person.setEmail("test" + i + "@email.com");
      i++;
      mockEmails.add(person.getEmail());
    }

    when(personService.getEmailsByCity(Mockito.anyString())).thenReturn(mockEmails);
    //when&then
    mockMvc.perform(get("/communityEmail").param("city", "Culver"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(3)))
        .andExpect(jsonPath("$[0]", is("test0@email.com")))
        .andExpect(jsonPath("$[1]", is("test1@email.com")))
        .andExpect(jsonPath("$[2]", is("test2@email.com")))
        .andDo(print());

  }

  @Test
  @Order(16)
  void getEmailsByCity_whenNoExistedCity_thenReturn200() throws Exception {

    //given
    List<String> mockEmails = new ArrayList<>();
    when(personService.getEmailsByCity(Mockito.anyString())).thenReturn(mockEmails);

    //when&then
    mockMvc.perform(get("/communityEmail").param("city", "Cassis"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$",
            is("there is nobody in this city or the city is not indexed in database")))
        .andDo(print());

  }
}
