package com.safetynet.alert.controller.emergency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.safetynet.alert.CommandLineRunnerTaskExcecutor;
import com.safetynet.alert.database.LoadDataStrategyFactory;
import com.safetynet.alert.database.StrategyName;
import com.safetynet.alert.exceptions.address.AddressNotFoundException;
import com.safetynet.alert.exceptions.firestation.FireStationNotFoundException;
import com.safetynet.alert.exceptions.person.PersonNotFoundException;
import com.safetynet.alert.service.PersonService;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Integration Test for class {@link EmergencyRestController}.
 *
 * @author delaval
 *
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestMethodOrder(OrderAnnotation.class)
class EmergencyRestControllerIT {

  @Autowired
  private PersonService personService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private LoadDataStrategyFactory ldsf;

  @MockBean
  private CommandLineRunnerTaskExcecutor clrte;

  @BeforeEach
  void setUp() throws Exception {

    ldsf.findStrategy(StrategyName.StrategyTest).loadDatabaseFromSource();

  }

  @Test
  @Order(1)
  void getPersonsMappedWithFireStation_whenNumberStationExists_thenReturn200()
      throws Exception {

    //Given

    //When and Then
    mockMvc.perform(get("/firestation/getpersons").param("stationNumber", "3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.AdultCount", is(3)))
        .andExpect(jsonPath("$.ChildrenCount", is(2)))
        .andExpect(jsonPath("$.BirthDateNotSpecified", is(1)))
        .andExpect(jsonPath("$.persons.length()", is(6)))
        .andExpect(jsonPath("$.persons[0].firstName", is("John")))
        .andExpect(jsonPath("$.persons[0].lastName", is("Boyd")))
        .andExpect(jsonPath("$.persons[0].phone", is("841-874-6512")))
        .andExpect(jsonPath("$.persons[1].firstName", is("Jacob")))
        .andExpect(jsonPath("$.persons[1].lastName", is("Boyd")))
        .andExpect(jsonPath("$.persons[1].phone", is("841-874-6513")))
        .andExpect(jsonPath("$.persons[2].firstName", is("Tenley")))
        .andExpect(jsonPath("$.persons[2].lastName", is("Boyd")))
        .andExpect(jsonPath("$.persons[2].phone", is("841-874-6512")))
        .andExpect(jsonPath("$.persons[3].firstName", is("Roger")))
        .andExpect(jsonPath("$.persons[3].lastName", is("Boyd")))
        .andExpect(jsonPath("$.persons[3].phone", is("841-874-6512")))
        .andExpect(jsonPath("$.persons[4].firstName", is("Felicia")))
        .andExpect(jsonPath("$.persons[4].lastName", is("Boyd")))
        .andExpect(jsonPath("$.persons[4].phone", is("841-874-6544")))
        .andExpect(jsonPath("$.persons[5].firstName", is("Dorian")))
        .andExpect(jsonPath("$.persons[5].lastName", is("Delaval")))
        .andExpect(jsonPath("$.persons[5].phone", is("061-846-0160")))
        .andDo(print());

  }

  @Test
  @Order(2)
  void getPersonsMappedWithFireStation_whenFireStationNotMappedWithAddrress_thenReturn200()
      throws Exception {

    //Given

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

    //when and then
    mockMvc.perform(get("/childAlert").param("address", "1509 Culver St"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(2)))
        .andExpect(jsonPath("$.children.length()", is(2)))
        .andExpect(jsonPath("$.children[0].lastName", is("Boyd")))
        .andExpect(jsonPath("$.children[0].firstName", is("Roger")))
        .andExpect(jsonPath("$.children[0].age", notNullValue()))
        .andExpect(jsonPath("$.children[1].lastName", is("Boyd")))
        .andExpect(jsonPath("$.children[1].firstName", is("Tenley")))
        .andExpect(jsonPath("$.children[1].age", notNullValue()))
        .andExpect(jsonPath("$.otherMembers.length()", is(3)))
        .andExpect(jsonPath("$.otherMembers[0].lastName", is("Boyd")))
        .andExpect(jsonPath("$.otherMembers[0].firstName", is("Jacob")))
        .andExpect(jsonPath("$.otherMembers[0].age", notNullValue()))
        .andExpect(jsonPath("$.otherMembers[1].lastName", is("Boyd")))
        .andExpect(jsonPath("$.otherMembers[1].firstName", is("Felicia")))
        .andExpect(jsonPath("$.otherMembers[1].age", notNullValue()))
        .andExpect(jsonPath("$.otherMembers[2].lastName", is("Boyd")))
        .andExpect(jsonPath("$.otherMembers[2].firstName", is("John")))
        .andExpect(jsonPath("$.otherMembers[2].age", notNullValue()))
        .andDo(print());


  }

  @Test
  @Order(5)
  void getChildAlert_whenNotExistedAddress_thenReturn404() throws Exception {
    //Given

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

    //When and Then
    mockMvc.perform(get("/phoneAlert").param("firestation", "3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.phones.length()", is(4)))
        .andExpect(jsonPath("$.phones[0]", is("061-846-0160")))
        .andExpect(jsonPath("$.phones[1]", is("841-874-6512")))
        .andExpect(jsonPath("$.phones[2]", is("841-874-6513")))
        .andExpect(jsonPath("$.phones[3]", is("841-874-6544")))

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
    //when &then verifier l'ordre de la liste desordonnée
    mockMvc.perform(get("/fire").param("address", "1509 Culver St"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(5)))
        .andExpect(jsonPath("$[0].lastName", is("Boyd")))
        .andExpect(jsonPath("$[0].phone", is("841-874-6512")))
        .andExpect(jsonPath("$[0].age", notNullValue()))
        .andExpect(jsonPath("$[0].medications.length()", is(2)))
        .andExpect(jsonPath("$[0].medications[*].designation",
            Matchers.containsInAnyOrder("aznol", "hydrapermazol")))
        .andExpect(jsonPath("$[0].medications[*].posology",
            Matchers.containsInAnyOrder("350mg", "100mg")))
        .andExpect(jsonPath("$[0].allergies.length()", is(1)))
        .andExpect(jsonPath("$[0].allergies[0].designation", is("nillacilan")))
        .andExpect(jsonPath("$[1].lastName", is("Boyd")))
        .andExpect(jsonPath("$[1].phone", is("841-874-6512")))
        .andExpect(jsonPath("$[1].age", notNullValue()))
        .andExpect(jsonPath("$[1].medications.length()", is(0)))
        .andExpect(jsonPath("$[1].allergies.length()", is(1)))
        .andExpect(jsonPath("$[1].allergies[0].designation", is("peanut")))
        .andExpect(jsonPath("$[2].lastName", is("Boyd")))
        .andExpect(jsonPath("$[2].phone", is("841-874-6512")))
        .andExpect(jsonPath("$[2].age", notNullValue()))
        .andExpect(jsonPath("$[2].medications.length()", is(0)))
        .andExpect(jsonPath("$[2].allergies.length()", is(0)))
        .andExpect(jsonPath("$[3].lastName", is("Boyd")))
        .andExpect(jsonPath("$[3].phone", is("841-874-6513")))
        .andExpect(jsonPath("$[3].age", notNullValue()))
        .andExpect(jsonPath("$[3].medications.length()", is(3)))
        .andExpect(jsonPath("$[3].medications[*].designation",
            Matchers.containsInAnyOrder("pharmacol", "terazine", "noznazol")))
        .andExpect(jsonPath("$[3].medications[*].posology",
            Matchers.containsInAnyOrder("5000mg", "10mg", "250mg")))
        .andExpect(jsonPath("$[3].allergies.length()", is(0)))
        .andExpect(jsonPath("$[4].lastName", is("Boyd")))
        .andExpect(jsonPath("$[4].phone", is("841-874-6544")))
        .andExpect(jsonPath("$[4].age", notNullValue()))
        .andExpect(jsonPath("$[4].medications.length()", is(1)))
        .andExpect(jsonPath("$[4].medications[0].designation", is("tetracyclaz")))
        .andExpect(jsonPath("$[4].medications[0].posology", is("650mg")))
        .andExpect(jsonPath("$[4].allergies.length()", is(1)))
        .andExpect(jsonPath("$[4].allergies[0].designation", is("xilliathal")))
        .andDo(print());

  }

  @Test
  @Order(9)
  void getPersonsWhenFire_whenAddressDoesntExist_thenReturn404()
      throws Exception {

    //Given

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
    List<String> values = Arrays.asList("3");

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.addAll("stations", values);

    //When& then
    mockMvc.perform(get("/flood/stations").params(params))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(2)))
        .andExpect(jsonPath("$.['1509 Av marechal foch'][0].lastName", is("Delaval")))
        .andExpect(jsonPath("$.['1509 Av marechal foch'][0].phone", is("061-846-0160")))
        .andExpect(jsonPath("$.['1509 Av marechal foch'][0].age", is("not specified")))
        .andExpect(
            jsonPath("$.['1509 Av marechal foch'][0].status_MedicalRecord",
                is("not yet created")))

        .andExpect(jsonPath("$.['1509 Culver St'][0].lastName", is("Boyd")))
        .andExpect(jsonPath("$.['1509 Culver St'][0].phone", is("841-874-6512")))
        .andExpect(jsonPath("$.['1509 Culver St'][0].age", notNullValue()))
        .andExpect(jsonPath("$.['1509 Culver St'][0].medications.length()", is(2)))
        .andExpect(jsonPath("$.['1509 Culver St'][0].medications[*].designation",
            Matchers.containsInAnyOrder("aznol", "hydrapermazol")))
        .andExpect(jsonPath("$.['1509 Culver St'][0].medications[*].posology",
            Matchers.containsInAnyOrder("350mg", "100mg")))
        .andExpect(jsonPath("$.['1509 Culver St'][0].allergies.length()", is(1)))
        .andExpect(
            jsonPath("$.['1509 Culver St'][0].allergies[0].designation", is("nillacilan")))
        .andExpect(jsonPath("$.['1509 Culver St'][3].lastName", is("Boyd")))
        .andExpect(jsonPath("$.['1509 Culver St'][3].phone", is("841-874-6512")))
        .andExpect(jsonPath("$.['1509 Culver St'][3].age", notNullValue()))
        .andExpect(jsonPath("$.['1509 Culver St'][3].medications.length()", is(0)))
        .andExpect(jsonPath("$.['1509 Culver St'][3].allergies.length()", is(1)))
        .andExpect(jsonPath("$.['1509 Culver St'][3].allergies[0].designation", is("peanut")))
        .andExpect(jsonPath("$.['1509 Culver St'][4].lastName", is("Boyd")))
        .andExpect(jsonPath("$.['1509 Culver St'][4].phone", is("841-874-6512")))
        .andExpect(jsonPath("$.['1509 Culver St'][4].age", notNullValue()))
        .andExpect(jsonPath("$.['1509 Culver St'][4].medications.length()", is(0)))
        .andExpect(jsonPath("$.['1509 Culver St'][4].allergies.length()", is(0)))
        .andExpect(jsonPath("$.['1509 Culver St'][2].lastName", is("Boyd")))
        .andExpect(jsonPath("$.['1509 Culver St'][2].phone", is("841-874-6513")))
        .andExpect(jsonPath("$.['1509 Culver St'][2].age", notNullValue()))
        .andExpect(jsonPath("$.['1509 Culver St'][2].medications.length()", is(3)))
        .andExpect(jsonPath("$.['1509 Culver St'][2].medications[*].designation",
            Matchers.containsInAnyOrder("pharmacol", "terazine", "noznazol")))
        .andExpect(jsonPath("$.['1509 Culver St'][2].medications[*].posology",
            Matchers.containsInAnyOrder("5000mg", "10mg", "250mg")))
        .andExpect(jsonPath("$.['1509 Culver St'][2].allergies.length()", is(0)))
        .andExpect(jsonPath("$.['1509 Culver St'][1].lastName", is("Boyd")))
        .andExpect(jsonPath("$.['1509 Culver St'][1].phone", is("841-874-6544")))
        .andExpect(jsonPath("$.['1509 Culver St'][1].age", notNullValue()))
        .andExpect(jsonPath("$.['1509 Culver St'][1].medications.length()", is(1)))
        .andExpect(
            jsonPath("$.['1509 Culver St'][1].medications[0].designation", is("tetracyclaz")))
        .andExpect(jsonPath("$.['1509 Culver St'][1].medications[0].posology", is("650mg")))
        .andExpect(jsonPath("$.['1509 Culver St'][1].allergies.length()", is(1)))
        .andExpect(
            jsonPath("$.['1509 Culver St'][1].allergies[0].designation", is("xilliathal")))
        .andDo(print());

  }


  @Test
  @Order(11)
  void getPersonsWhenFlood_whenNoExistedStationInList_thenReturn404() throws Exception {

    //Given
    List<String> values = Arrays.asList("3", "5");

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.addAll("stations", values);

    //When & then

    MvcResult mvcResult = mockMvc.perform(get("/flood/stations").params(params))
        .andExpect(status().isNotFound()).andReturn();

    assertThat(mvcResult.getResolvedException())
        .isInstanceOf(FireStationNotFoundException.class);
    assertThat(mvcResult.getResolvedException().getMessage()).isEqualTo(
        "the Firestation with numberStation: 5 was not found."
            + "Please replace it by existed FireStation");

  }

  @Test
  @Order(12)
  void getPersonsWhenFlood_whenOnePersonHasNoMedicalrecord_thenReturn200() throws Exception {

    //Given
    List<String> values = Arrays.asList("2");

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.addAll("stations", values);

    //When& then
    mockMvc.perform(get("/flood/stations").params(params))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(1)))
        .andExpect(jsonPath("$.['29 15th St'][0].lastName", is("Marrack")))
        .andExpect(jsonPath("$.['29 15th St'][0].phone", is("841-874-6513")))
        .andExpect(jsonPath("$.['29 15th St'][0].age", is("not specified")))
        .andExpect(jsonPath("$.['29 15th St'][0].status_MedicalRecord", is("not yet created")))
        .andDo(print());

  }

  @Test
  @Order(13)
  void getPersonInfo_whenExistedPerson_thenReturn200() throws Exception {

    //given
    //    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    //    map.add("firstName", "John");
    //    map.add("lastName", "Boyd");

    String firstName = "John";
    String lastName = "Boyd";
    //when & Then
    mockMvc
        .perform(get("/personInfo").param("lastName", lastName).param("firstName", firstName))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(5)))
        .andExpect(jsonPath("$[0].lastName", is("Boyd")))
        .andExpect(jsonPath("$[0].address", is("1509 Culver St")))
        .andExpect(jsonPath("$[0].age", notNullValue()))
        .andExpect(jsonPath("$[0].email", is("jaboyd@email.com")))
        .andExpect(jsonPath("$[0].medications.length()", is(2)))
        .andExpect(jsonPath("$[0].medications[*].designation",
            Matchers.containsInAnyOrder("aznol", "hydrapermazol")))
        .andExpect(jsonPath("$[0].medications[*].posology",
            Matchers.containsInAnyOrder("350mg", "100mg")))
        .andExpect(jsonPath("$[0].allergies.length()", is(1)))
        .andExpect(
            jsonPath("$[0].allergies[0].designation", is("nillacilan")))
        .andDo(print());

  }

  @Test
  @Order(14)
  void getPersonsInfo_whenNoExistedPerson_thenReturn404() throws Exception {

    //Given

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

    map.add("lastName", "Baudouin");
    map.add("firstName", "Emilie");

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
    //when&then
    mockMvc.perform(get("/communityEmail").param("city", "Culver"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$[0]", is("dd@email.com")))
        .andExpect(jsonPath("$[1]", is("drk@email.com")))
        .andExpect(jsonPath("$[2]", is("jaboyd@email.com")))
        .andExpect(jsonPath("$[3]", is("tenz@email.com"))).andDo(print());

  }

  @Test
  @Order(16)
  void getEmailsByCity_whenNoExistedCity_thenReturn200() throws Exception {

    //given
    //when&then
    mockMvc.perform(get("/communityEmail").param("city", "Cassis"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$",
            is("there is nobody in this city or the city is not indexed in database")))
        .andDo(print());

  }
}
