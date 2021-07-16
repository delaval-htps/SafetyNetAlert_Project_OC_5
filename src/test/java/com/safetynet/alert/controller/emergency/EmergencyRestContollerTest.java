package com.safetynet.alert.controller.emergency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.hamcrest.Matchers;
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

  private Person mockAdult1;
  private Person mockAdult2;
  private Person mockChild1;
  private Person mockAdult3;

  private Set<String> addresses1;
  private Set<String> addresses2;

  private Set<Person> persons1;
  private Set<Person> persons2;

  private FireStation mockFireStation1;
  private FireStation mockFireStation2;

  private Set<FireStation> mockSetFireStation1;
  private Set<FireStation> mockSetFireStation2;

  private Medication mockMedication1;
  private Medication mockMedication2;
  private Allergy mockAllergy1;

  private MedicalRecord mockMedicalRecord;

  private Map<String, List<Person>> mapBodyResponse;
  private Map<String, Object> mapBodyResponseWithCount;


  private LinkedHashSet<Medication> medications;
  private Set<Allergy> allergies;

  private Date birthDatePerson3;



  @BeforeEach
  void setUp() throws Exception {

    mockAdult1 =
        new Person(null, "Person1", "Adult1",
                   new SimpleDateFormat("MM/dd/yyyy").parse("12/27/1976"), "address1",
                   null, null, "061-846-0160", null, null, mockSetFireStation1);
    mockAdult2 =
        new Person(null, "Person2", "Adult2",
                   new SimpleDateFormat("MM/dd/yyyy").parse("02/22/1984"), "address1",
                   null, null, "061-846-0161", null, null, mockSetFireStation1);

    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -12);
    birthDatePerson3 = cal.getTime();

    mockChild1 =
        new Person(null, "Person3", "Child1", birthDatePerson3, "address1",
                   null, null, "061-846-0162", null, null, mockSetFireStation1);
    mockAdult3 =
        new Person(null, "Person4", "Adult3",
                   new SimpleDateFormat("MM/dd/yyyy").parse("12/27/1965"),
                   "address2", null, null, "061-846-0163", null, null, mockSetFireStation2);


    mockMedication1 = new Medication(1L, "medication1", "100mg", null);
    mockMedication2 = new Medication(2L, "medication2", "200mg", null);

    mockAllergy1 = new Allergy(1L, "allergy1", null);

    medications = new LinkedHashSet<>();
    medications.add(mockMedication1);
    medications.add(mockMedication2);

    allergies = new HashSet<>();
    allergies.add(mockAllergy1);

    addresses1 = new HashSet<String>();
    addresses1.add("address1");
    addresses1.add("address2");

    addresses2 = new HashSet<String>();
    addresses2.add("address3");

    persons1 = new LinkedHashSet<Person>();
    persons1.add(mockAdult1);
    persons1.add(mockAdult2);
    persons1.add(mockChild1);

    persons2 = new LinkedHashSet<Person>();
    persons2.add(mockAdult3);

    mockFireStation1 = new FireStation(1L, 1, addresses1, persons1);
    mockFireStation2 = new FireStation(2L, 2, addresses2, persons2);

    mockSetFireStation1 = new HashSet<>();
    mockSetFireStation1.add(mockFireStation1);

    mockSetFireStation2 = new HashSet<>();
    mockSetFireStation2.add(mockFireStation2);

    mockMedicalRecord = new MedicalRecord(1L, mockChild1, medications, allergies);

    mapBodyResponse = new LinkedHashMap<>();
    mapBodyResponseWithCount = new LinkedHashMap<>();

  }

  @Test
  @Order(1)
  void getPersonsMappedWithFireStation_whenNumberStationExists_thenReturn200()
      throws Exception {

    //Given
    mapBodyResponseWithCount.put("AdultCount", 2);
    mapBodyResponseWithCount.put("ChildrenCount", 1);

    //persons1 contains mockAdult1,2 and mockChild1 mapped with address1
    mapBodyResponseWithCount.put("persons", new ArrayList<>(persons1));

    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.of(mockFireStation1));

    when(personService.getPersonsMappedWithFireStation(Mockito.anyInt()))
        .thenReturn(mapBodyResponseWithCount);

    //When and Then
    mockMvc.perform(get("/firestation/getpersons").param("stationNumber", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(3)))
        .andExpect(jsonPath("$.persons[0].firstName", is("Person1")))
        .andExpect(jsonPath("$.persons[0].lastName", is("Adult1")))
        .andExpect(jsonPath("$.persons[0].phone", is("061-846-0160")))
        .andExpect(jsonPath("$.persons[1].firstName", is("Person2")))
        .andExpect(jsonPath("$.persons[1].lastName", is("Adult2")))
        .andExpect(jsonPath("$.persons[1].phone", is("061-846-0161")))
        .andExpect(jsonPath("$.persons[2].firstName", is("Person3")))
        .andExpect(jsonPath("$.persons[2].lastName", is("Child1")))
        .andExpect(jsonPath("$.persons[2].phone", is("061-846-0162")))
        .andExpect(jsonPath("$.AdultCount", is(2)))
        .andExpect(jsonPath("$.ChildrenCount", is(1))).andDo(print());

  }

  @Test
  @Order(2)
  void getPersonsMappedWithFireStation_whenFireStationNotMappedWithAddrress_thenReturn200()
      throws Exception {

    //Given
    mapBodyResponseWithCount.put("AdultCount", 0);
    mapBodyResponseWithCount.put("ChildrenCount", 0);
    mapBodyResponseWithCount.put("persons", new ArrayList<>());

    mockFireStation1.setAddresses(new HashSet<String>());
    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.of(mockFireStation1));

    when(personService.getPersonsMappedWithFireStation(Mockito.anyInt()))
        .thenReturn(mapBodyResponseWithCount);
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
    children.add(mockChild1);

    List<Person> otherMembers = new ArrayList();
    otherMembers.add(mockAdult1);
    otherMembers.add(mockAdult2);


    mapBodyResponse.put("children", children);
    mapBodyResponse.put("otherMembers", otherMembers);

    when(personService.getChildrenByAddress(Mockito.anyString())).thenReturn(mapBodyResponse);

    //when and then
    mockMvc.perform(get("/childAlert").param("address", "address1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(2)))
        .andExpect(jsonPath("$.children.length()", is(1)))
        .andExpect(jsonPath("$.children[0].lastName", is("Child1")))
        .andExpect(jsonPath("$.children[0].age", notNullValue()))
        .andExpect(jsonPath("$.otherMembers.length()", is(2)))
        .andExpect(jsonPath("$.otherMembers[0].lastName", is("Adult1")))
        .andExpect(jsonPath("$.otherMembers[0].age", notNullValue()))
        .andExpect(jsonPath("$.otherMembers[1].lastName", is("Adult2")))
        .andExpect(jsonPath("$.otherMembers[1].age", notNullValue()))
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
  void getChildAlert_whenNoChildren_thenReturnEmptyList200() throws Exception {

    //Given
    List<Person> children = new ArrayList();
    List<Person> otherMembers = new ArrayList();
    otherMembers.add(mockAdult1);
    otherMembers.add(mockAdult2);

    mapBodyResponse.put("children", children);
    mapBodyResponse.put("otherMembers", otherMembers);
    when(personService.getChildrenByAddress(Mockito.anyString())).thenReturn(mapBodyResponse);

    //when and then
    MvcResult result =
        mockMvc.perform(get("/childAlert").param("address", "AddressWithNoChildren"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", is(0)))
            .andReturn();

  }

  @Test
  @Order(6)
  void getPhoneAlert_whenExistedFireStation_thenReturn200() throws Exception {

    //Given
    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.of(mockFireStation1));

    // as mockFireStation has mockPerson1,2,3
    List<String> phones =
        Arrays.asList(mockAdult1.getPhone(), mockAdult2.getPhone(), mockChild1.getPhone());

    when(personService.getPhonesByNumberStation(Mockito.anyInt())).thenReturn(phones);

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

    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.empty());

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
    // as set persons1 contains persons with the same address address1
    // set mockMedicalRecord for all just to check if fetching medications and allergies
    for (Person person : persons1) {

      person.setMedicalRecord(mockMedicalRecord);

    }
    List<Person> personsMappedToAddress = new ArrayList(persons1);

    when(personService.getPersonsWhenFireMappedByAddress(Mockito.anyString()))
        .thenReturn(personsMappedToAddress);

    //when & then
    mockMvc.perform(get("/fire").param("address", "address1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(3)))
        .andExpect(jsonPath("$[0].lastName", is("Adult1")))
        .andExpect(jsonPath("$[0].phone", is("061-846-0160")))
        .andExpect(jsonPath("$[0].age", notNullValue()))
        .andExpect(jsonPath("$[0].medications.length()", is(2)))
        .andExpect(jsonPath("$[0].medications[*].designation",
            Matchers.containsInAnyOrder("medication1", "medication2")))
        .andExpect(jsonPath("$[0].medications[*].posology",
            Matchers.containsInAnyOrder("100mg", "200mg")))
        .andExpect(jsonPath("$[0].allergies.length()", is(1)))
        .andExpect(jsonPath("$[0].allergies[0].designation", is("allergy1")))
        .andExpect(jsonPath("$[1].lastName", is("Adult2")))
        .andExpect(jsonPath("$[1].phone", is("061-846-0161")))
        .andExpect(jsonPath("$[1].age", notNullValue()))
        .andExpect(jsonPath("$[1].medications[*].designation",
            Matchers.containsInAnyOrder("medication1", "medication2")))
        .andExpect(jsonPath("$[1].medications[*].posology",
            Matchers.containsInAnyOrder("100mg", "200mg")))
        .andExpect(jsonPath("$[1].allergies.length()", is(1)))
        .andExpect(jsonPath("$[1].allergies[0].designation", is("allergy1")))
        .andExpect(jsonPath("$[2].lastName", is("Child1")))
        .andExpect(jsonPath("$[2].phone", is("061-846-0162")))
        .andExpect(jsonPath("$[2].age", notNullValue()))
        .andExpect(jsonPath("$[2].medications[*].designation",
            Matchers.containsInAnyOrder("medication1", "medication2")))
        .andExpect(jsonPath("$[2].medications[*].posology",
            Matchers.containsInAnyOrder("100mg", "200mg")))
        .andExpect(jsonPath("$[2].allergies.length()", is(1)))
        .andExpect(jsonPath("$[2].allergies[0].designation", is("allergy1")))
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

    // mapping a medicalRecord and firestation to persons
    mockAdult1.setMedicalRecord(mockMedicalRecord);
    mockAdult2.setMedicalRecord(mockMedicalRecord);
    mockChild1.setMedicalRecord(mockMedicalRecord);
    mockAdult1.setFireStations(mockSetFireStation1);
    mockAdult2.setFireStations(mockSetFireStation1);
    mockChild1.setFireStations(mockSetFireStation1);

    mockAdult3.setMedicalRecord(null);
    mockAdult3.setFireStations(mockSetFireStation2);

    // persons1 contains persons with address1
    // persons2 contains persons with address2
    mapBodyResponse.put("address1", new ArrayList<>(persons1));
    mapBodyResponse.put("address2", new ArrayList<>(persons2));

    //mockFirestation has address1 and mockPerson1,2,3
    //mockFireStation2 has address2 and mockPerson5
    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.of(mockFireStation1), Optional.of(mockFireStation2));

    when(personService.getPersonsWhenFloodByStations(Mockito.anyList()))
        .thenReturn(mapBodyResponse);

    //When& then
    mockMvc
        .perform(get("/flood/stations").params(params))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(2)))
        .andExpect(jsonPath("$.address1[0].lastName", is("Adult1")))
        .andExpect(jsonPath("$.address1[0].phone", is("061-846-0160")))
        .andExpect(jsonPath("$.address1[0].age", notNullValue()))
        .andExpect(jsonPath("$.address1[0].medications.length()", is(2)))
        .andExpect(jsonPath("$.address1[*].medications[*].designation",
            Matchers.hasItems("medication1", "medication2")))
        .andExpect(jsonPath("$.address1[*].medications[*].posology",
            Matchers.hasItems("100mg", "200mg")))
        .andExpect(jsonPath("$.address1[0].allergies.length()", is(1)))
        .andExpect(
            jsonPath("$.address1[*].allergies[*].designation", Matchers.hasItems("allergy1")))
        .andExpect(jsonPath("$.address1[1].lastName", is("Adult2")))
        .andExpect(jsonPath("$.address1[1].phone", is("061-846-0161")))
        .andExpect(jsonPath("$.address1[1].age", notNullValue()))
        .andExpect(jsonPath("$.address1[1].medications.length()", is(2)))
        .andExpect(jsonPath("$.address1[1].allergies.length()", is(1)))
        .andExpect(jsonPath("$.address1[2].lastName", is("Child1")))
        .andExpect(jsonPath("$.address1[2].phone", is("061-846-0162")))
        .andExpect(jsonPath("$.address1[2].age", notNullValue()))
        .andExpect(jsonPath("$.address1[2].medications.length()", is(2)))
        .andExpect(jsonPath("$.address1[2].allergies.length()", is(1)))
        .andExpect(jsonPath("$.address2[0].lastName", is("Adult3")))
        .andExpect(jsonPath("$.address2[0].phone", is("061-846-0163")))
        .andExpect(jsonPath("$.address2[0].age", notNullValue()))
        .andExpect(jsonPath("$.address2[0].status_MedicalRecord", is("not yet created")))
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

  //  @Test
  //  @Order(12)
  //  void getPersonsWhenFlood_whenOnePersonHasNoMedicalrecord_thenReturn200() throws Exception {
  //
  //    //Given
  //    List<String> values = Arrays.asList("1");
  //    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
  //    params.addAll("stations", values);
  //
  //    mapBodyResponse.put("address3", new ArrayList<>(persons2));
  //
  //    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
  //        .thenReturn(Optional.of(mockFireStation2));
  //
  //    when(personService.getPersonsWhenFloodByStations(Mockito.anyList()))
  //        .thenReturn(mapresult);
  //    //When& then
  //    mockMvc.perform(get("/flood/stations").params(params))
  //        .andExpect(status().isOk())
  //        .andExpect(jsonPath("$.length()", is(1)))
  //        .andExpect(jsonPath("$.address3[0].lastName", is("Test2")))
  //        .andExpect(jsonPath("$.address3[0].phone", is("061-846-0164")))
  //        .andExpect(jsonPath("$.address3[0].age", notNullValue()))
  //        .andExpect(jsonPath("$.address3[0].status_MedicalRecord", is("not yet created")))
  //        .andDo(print());
  //
  //  }

  @Test
  @Order(13)
  void getPersonInfo_whenExistedPerson_thenReturn200() throws Exception {

    //given
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("firstName", "Person1");
    map.add("lastName", "Adult1");

    mockAdult1.setEmail("person1@email.com");
    // change name of Adult2 to "Adult1" to check if he's in the result
    // and Adult1 is the first Row of result
    mockAdult2.setLastName("Adult1");
    mockAdult2.setEmail("adult2@email.com");

    Set<Person> mockInformations = new LinkedHashSet<Person>();
    mockInformations.add(mockAdult1);
    mockInformations.add(mockAdult2);

    when(personService.getPersonInfoByNames(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(mockInformations);

    //when & Then
    mockMvc.perform(get("/personInfo").params(map)).andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(2)))
        .andExpect(jsonPath("$[0].lastName", is("Adult1")))
        .andExpect(jsonPath("$[0].address", is("address1")))
        .andExpect(jsonPath("$[0].age", notNullValue()))
        .andExpect(jsonPath("$[0].email", is("person1@email.com")))
        .andExpect(jsonPath("$[0].status_MedicalRecord", is("not yet created")))
        .andExpect(jsonPath("$[1].lastName", is("Adult1")))
        .andExpect(jsonPath("$[1].address", is("address1")))
        .andExpect(jsonPath("$[1].age", notNullValue()))
        .andExpect(jsonPath("$[1].email", is("adult2@email.com")))
        .andExpect(jsonPath("$[1].status_MedicalRecord", is("not yet created")))
        .andDo(print());

  }

  @Test
  @Order(14)
  void getPersonsInfo_whenNoExistedPerson_thenReturn404() throws Exception {

    //Given

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("firstName", "Emilie");
    map.add("lastName", "Baudouin");

    Set<Person> mockInformations = new LinkedHashSet<Person>();
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

    for (Person person : persons1) {

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
