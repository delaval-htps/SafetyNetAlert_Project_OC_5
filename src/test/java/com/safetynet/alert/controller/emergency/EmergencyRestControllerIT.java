package com.safetynet.alert.controller.emergency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.safetynet.alert.CommandLineRunnerTaskExcecutor;
import com.safetynet.alert.database.LoadDataStrategyFactory;
import com.safetynet.alert.database.StrategyName;
import com.safetynet.alert.exceptions.address.AddressNotFoundException;
import com.safetynet.alert.exceptions.firestation.FireStationNotFoundException;
import com.safetynet.alert.service.PersonService;
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
        .andExpect(jsonPath("$.length()", is(3)))
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
        .andExpect(jsonPath("$.AdultCount", is(3)))
        .andExpect(jsonPath("$.ChildrenCount", is(2))).andDo(print());

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
        .andExpect(jsonPath("$.Children[0].firstName", is("Tenley")))
        .andExpect(jsonPath("$.Children[0].lastName", is("Boyd")))
        .andExpect(jsonPath("$.Children[0].birthDate", is("02/18/2012")))
        .andExpect(jsonPath("$.Children[1].firstName", is("Roger")))
        .andExpect(jsonPath("$.Children[1].lastName", is("Boyd")))
        .andExpect(jsonPath("$.Children[1].birthDate", is("09/06/2017")))
        .andExpect(jsonPath("$.OtherMember.length()", is(3)))
        .andExpect(jsonPath("$.OtherMember[0].firstName", is("John")))
        .andExpect(jsonPath("$.OtherMember[0].lastName", is("Boyd")))
        .andExpect(jsonPath("$.OtherMember[0].birthDate", is("03/06/1984")))
        .andExpect(jsonPath("$.OtherMember[1].firstName", is("Jacob")))
        .andExpect(jsonPath("$.OtherMember[1].lastName", is("Boyd")))
        .andExpect(jsonPath("$.OtherMember[1].birthDate", is("03/06/1989")))
        .andExpect(jsonPath("$.OtherMember[2].firstName", is("Felicia")))
        .andExpect(jsonPath("$.OtherMember[2].lastName", is("Boyd")))
        .andExpect(jsonPath("$.OtherMember[2].birthDate", is("01/08/1986")))
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

}
