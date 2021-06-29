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
        .andExpect(jsonPath("$.Children.length()", is(2)))
        .andExpect(jsonPath("$.Children[0].Name", is("Boyd Roger")))
        .andExpect(jsonPath("$.Children[0].Age", notNullValue()))
        .andExpect(jsonPath("$.Children[1].Name", is("Boyd Tenley")))
        .andExpect(jsonPath("$.Children[1].Age", notNullValue()))
        .andExpect(jsonPath("$.OtherMembers.length()", is(3)))
        .andExpect(jsonPath("$.OtherMembers[0].Name", is("Boyd Jacob")))
        .andExpect(jsonPath("$.OtherMembers[0].Age", notNullValue()))
        .andExpect(jsonPath("$.OtherMembers[1].Name", is("Boyd Felicia")))
        .andExpect(jsonPath("$.OtherMembers[1].Age", notNullValue()))
        .andExpect(jsonPath("$.OtherMembers[2].Name", is("Boyd John")))
        .andExpect(jsonPath("$.OtherMembers[2].Age", notNullValue()))
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
        .andExpect(jsonPath("$.Phones.length()", is(4)))
        .andExpect(jsonPath("$.Phones[0]", is("061-846-0160")))
        .andExpect(jsonPath("$.Phones[1]", is("841-874-6512")))
        .andExpect(jsonPath("$.Phones[2]", is("841-874-6513")))
        .andExpect(jsonPath("$.Phones[3]", is("841-874-6544")))

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
        .andExpect(jsonPath("$[0].Name", is("Boyd John")))
        .andExpect(jsonPath("$[0].Phone", is("841-874-6512")))
        .andExpect(jsonPath("$[0].Age", notNullValue()))
        .andExpect(jsonPath("$[0].Medications.length()", is(2)))
        .andExpect(jsonPath("$[0].Medications[0].designation", is("aznol")))
        .andExpect(jsonPath("$[0].Medications[0].posology", is("350mg")))
        .andExpect(jsonPath("$[0].Medications[1].designation", is("hydrapermazol")))
        .andExpect(jsonPath("$[0].Medications[1].posology", is("100mg")))
        .andExpect(jsonPath("$[0].Allergies.length()", is(1)))
        .andExpect(jsonPath("$[0].Allergies[0].designation", is("nillacilan")))
        .andExpect(jsonPath("$[1].Name", is("Boyd Tenley")))
        .andExpect(jsonPath("$[1].Phone", is("841-874-6512")))
        .andExpect(jsonPath("$[1].Age", notNullValue()))
        .andExpect(jsonPath("$[1].Medications.length()", is(0)))
        .andExpect(jsonPath("$[1].Allergies.length()", is(1)))
        .andExpect(jsonPath("$[1].Allergies[0].designation", is("peanut")))
        .andExpect(jsonPath("$[2].Name", is("Boyd Roger")))
        .andExpect(jsonPath("$[2].Phone", is("841-874-6512")))
        .andExpect(jsonPath("$[2].Age", notNullValue()))
        .andExpect(jsonPath("$[2].Medications.length()", is(0)))
        .andExpect(jsonPath("$[2].Allergies.length()", is(0)))
        .andExpect(jsonPath("$[3].Name", is("Boyd Jacob")))
        .andExpect(jsonPath("$[3].Phone", is("841-874-6513")))
        .andExpect(jsonPath("$[3].Age", notNullValue()))
        .andExpect(jsonPath("$[3].Medications.length()", is(3)))
        .andExpect(jsonPath("$[3].Medications[0].designation", is("pharmacol")))
        .andExpect(jsonPath("$[3].Medications[0].posology", is("5000mg")))
        .andExpect(jsonPath("$[3].Medications[1].designation", is("terazine")))
        .andExpect(jsonPath("$[3].Medications[1].posology", is("10mg")))
        .andExpect(jsonPath("$[3].Medications[2].designation", is("noznazol")))
        .andExpect(jsonPath("$[3].Medications[2].posology", is("250mg")))
        .andExpect(jsonPath("$[3].Allergies.length()", is(0)))
        .andExpect(jsonPath("$[4].Name", is("Boyd Felicia")))
        .andExpect(jsonPath("$[4].Phone", is("841-874-6544")))
        .andExpect(jsonPath("$[4].Age", notNullValue()))
        .andExpect(jsonPath("$[4].Medications.length()", is(1)))
        .andExpect(jsonPath("$[4].Medications[0].designation", is("tetracyclaz")))
        .andExpect(jsonPath("$[4].Medications[0].posology", is("650mg")))
        .andExpect(jsonPath("$[4].Allergies.length()", is(1)))
        .andExpect(jsonPath("$[4].Allergies[0].designation", is("xilliathal")))
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
    List<String> values = Arrays.asList("3", "2");

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.addAll("stations", values);

    //When& then
    mockMvc.perform(get("/flood/stations").params(params))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(3)))
        .andExpect(jsonPath("$.['1509 Av marechal foch'][0].Name", is("Delaval Dorian")))
        .andExpect(jsonPath("$.['1509 Av marechal foch'][0].Phone", is("061-846-0160")))
        .andExpect(jsonPath("$.['1509 Av marechal foch'][0].Age", is("not specified")))
        .andExpect(
            jsonPath("$.['1509 Av marechal foch'][0].MedicalRecord", is("not yet created")))

        .andExpect(jsonPath("$.['1509 Culver St'][0].Name", is("Boyd John")))
        .andExpect(jsonPath("$.['1509 Culver St'][0].Phone", is("841-874-6512")))
        .andExpect(jsonPath("$.['1509 Culver St'][0].Age", notNullValue()))
        .andExpect(jsonPath("$.['1509 Culver St'][0].Medications.length()", is(2)))
        .andExpect(jsonPath("$.['1509 Culver St'][0].Medications[0].designation", is("aznol")))
        .andExpect(jsonPath("$.['1509 Culver St'][0].Medications[0].posology", is("350mg")))
        .andExpect(jsonPath("$.['1509 Culver St'][0].Medications[1].designation",
            is("hydrapermazol")))
        .andExpect(jsonPath("$.['1509 Culver St'][0].Medications[1].posology", is("100mg")))
        .andExpect(jsonPath("$.['1509 Culver St'][0].Allergies.length()", is(1)))
        .andExpect(
            jsonPath("$.['1509 Culver St'][0].Allergies[0].designation", is("nillacilan")))
        .andExpect(jsonPath("$.['1509 Culver St'][1].Name", is("Boyd Tenley")))
        .andExpect(jsonPath("$.['1509 Culver St'][1].Phone", is("841-874-6512")))
        .andExpect(jsonPath("$.['1509 Culver St'][1].Age", notNullValue()))
        .andExpect(jsonPath("$.['1509 Culver St'][1].Medications.length()", is(0)))
        .andExpect(jsonPath("$.['1509 Culver St'][1].Allergies.length()", is(1)))
        .andExpect(jsonPath("$.['1509 Culver St'][1].Allergies[0].designation", is("peanut")))
        .andExpect(jsonPath("$.['1509 Culver St'][2].Name", is("Boyd Roger")))
        .andExpect(jsonPath("$.['1509 Culver St'][2].Phone", is("841-874-6512")))
        .andExpect(jsonPath("$.['1509 Culver St'][2].Age", notNullValue()))
        .andExpect(jsonPath("$.['1509 Culver St'][2].Medications.length()", is(0)))
        .andExpect(jsonPath("$.['1509 Culver St'][2].Allergies.length()", is(0)))
        .andExpect(jsonPath("$.['1509 Culver St'][3].Name", is("Boyd Jacob")))
        .andExpect(jsonPath("$.['1509 Culver St'][3].Phone", is("841-874-6513")))
        .andExpect(jsonPath("$.['1509 Culver St'][3].Age", notNullValue()))
        .andExpect(jsonPath("$.['1509 Culver St'][3].Medications.length()", is(3)))
        .andExpect(
            jsonPath("$.['1509 Culver St'][3].Medications[0].designation", is("pharmacol")))
        .andExpect(jsonPath("$.['1509 Culver St'][3].Medications[0].posology", is("5000mg")))
        .andExpect(
            jsonPath("$.['1509 Culver St'][3].Medications[1].designation", is("terazine")))
        .andExpect(jsonPath("$.['1509 Culver St'][3].Medications[1].posology", is("10mg")))
        .andExpect(
            jsonPath("$.['1509 Culver St'][3].Medications[2].designation", is("noznazol")))
        .andExpect(jsonPath("$.['1509 Culver St'][3].Medications[2].posology", is("250mg")))
        .andExpect(jsonPath("$.['1509 Culver St'][3].Allergies.length()", is(0)))
        .andExpect(jsonPath("$.['1509 Culver St'][4].Name", is("Boyd Felicia")))
        .andExpect(jsonPath("$.['1509 Culver St'][4].Phone", is("841-874-6544")))
        .andExpect(jsonPath("$.['1509 Culver St'][4].Age", notNullValue()))
        .andExpect(jsonPath("$.['1509 Culver St'][4].Medications.length()", is(1)))
        .andExpect(
            jsonPath("$.['1509 Culver St'][4].Medications[0].designation", is("tetracyclaz")))
        .andExpect(jsonPath("$.['1509 Culver St'][4].Medications[0].posology", is("650mg")))
        .andExpect(jsonPath("$.['1509 Culver St'][4].Allergies.length()", is(1)))
        .andExpect(
            jsonPath("$.['1509 Culver St'][4].Allergies[0].designation", is("xilliathal")))

        .andExpect(jsonPath("$.['29 15th St'][0].Name", is("Marrack Jonathan")))
        .andExpect(jsonPath("$.['29 15th St'][0].Phone", is("841-874-6513")))
        .andExpect(jsonPath("$.['29 15th St'][0].Age", is("not specified")))
        .andExpect(jsonPath("$.['29 15th St'][0].MedicalRecord", is("not yet created")))
        .andExpect(jsonPath("$.['29 15th St'][1].Name", is("Marrack Jonanathan")))
        .andExpect(jsonPath("$.['29 15th St'][1].Phone", is("841-874-6513")))
        .andExpect(jsonPath("$.['29 15th St'][1].Age", notNullValue()))
        .andExpect(jsonPath("$.['29 15th St'][1].Medications.length()", is(0)))
        .andExpect(jsonPath("$.['29 15th St'][1].Allergies.length()", is(0)))
        .andDo(print());

  }


  @Test
  @Order(11)
  void getPersonsWhenFlood_whenNoExistedStationInList_thenReturn404() throws Exception {

    //Given
    List<String> values = Arrays.asList("3", "18");

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.addAll("stations", values);

    //When & then

    MvcResult mvcResult = mockMvc.perform(get("/flood/stations").params(params))
        .andExpect(status().isNotFound()).andReturn();

    assertThat(mvcResult.getResolvedException())
        .isInstanceOf(FireStationNotFoundException.class);
    assertThat(mvcResult.getResolvedException().getMessage()).isEqualTo(
        "the Firestation with numberStation: 18 was not found."
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
        .andExpect(jsonPath("$.['29 15th St'][0].Name", is("Marrack Jonathan")))
        .andExpect(jsonPath("$.['29 15th St'][0].Phone", is("841-874-6513")))
        .andExpect(jsonPath("$.['29 15th St'][0].Age", is("not specified")))
        .andExpect(jsonPath("$.['29 15th St'][0].MedicalRecord", is("not yet created")))
        .andDo(print());

  }

  @Test
  @Order(13)
  void getPersonInfo_whenExistedPerson_thenReturn200() throws Exception {

    //given
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("firstName", "John");
    map.add("lastName", "Boyd");
    //when & Then
    mockMvc.perform(get("/personInfo").params(map)).andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(1)))
        .andExpect(jsonPath("$[0].Name", is("Boyd John")))
        .andExpect(jsonPath("$[0].Address", is("1509 Culver St")))
        .andExpect(jsonPath("$[0].Age", notNullValue()))
        .andExpect(jsonPath("$[0].Email", is("jaboyd@email.com")))
        .andExpect(jsonPath("$[0].Medications.length()", is(2)))
        .andExpect(jsonPath("$[0].Medications[0].designation", is("aznol")))
        .andExpect(jsonPath("$[0].Medications[0].posology", is("350mg")))
        .andExpect(jsonPath("$[0].Medications[1].designation",
            is("hydrapermazol")))
        .andExpect(jsonPath("$[0].Medications[1].posology", is("100mg")))
        .andExpect(jsonPath("$[0].Allergies.length()", is(1)))
        .andExpect(
            jsonPath("$[0].Allergies[0].designation", is("nillacilan")))
        .andDo(print());

  }

  @Test
  @Order(14)
  void getPersonsInfo_whenNoExistedPerson_thenReturn404() throws Exception {

    //Given

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("firstName", "Emilie");
    map.add("lastName", "Baudouin");

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
