package com.safetynet.alert.controller.emergency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.safetynet.alert.exceptions.firestation.FireStationNotFoundException;
import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.FireStationService;
import com.safetynet.alert.service.PersonService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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

@WebMvcTest(controllers = EmergencyRestController.class)
@TestMethodOrder(OrderAnnotation.class)
class EmergencyRestContollerTest {

  @MockBean
  private PersonService personService;

  @MockBean
  private FireStationService fireStationService;

  @Autowired
  private MockMvc mockMvc;

  private Person mockPerson1;
  private Person mockPerson2;
  private Person mockPerson3;
  private FireStation mockFireStation;

  private Map<String, Object> mapReponsebody;
  private Set<String> addresses;
  Set<Person> persons;

  @BeforeEach
  void setUp() throws Exception {

    mockPerson1 =
        new Person(null, "Person1", "Test",
                   new SimpleDateFormat("dd/MM/yyyy").parse("27/12/1976"), "address1",
                   null, null, "061-846-0160", null, null, mockFireStation);
    mockPerson2 =
        new Person(null, "Person2", "Test",
                   new SimpleDateFormat("dd/MM/yyyy").parse("22/02/1984"), "address1",
                   null, null, "061-846-0160", null, null, mockFireStation);

    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -18);
    Date birthDatePerson3 = cal.getTime();

    mockPerson3 =
        new Person(null, "Person3", "Test",
                   birthDatePerson3, "address2",
                   null, null, "061-846-0160", null, null, mockFireStation);

    addresses = new HashSet<String>();
    addresses.add("address1");
    addresses.add("address2");

    persons = new LinkedHashSet<Person>();
    persons.add(mockPerson1);
    persons.add(mockPerson2);
    persons.add(mockPerson3);

    mockFireStation = new FireStation(1L, 1, addresses, persons);

    mapReponsebody = new LinkedHashMap<>();
    mapReponsebody.put("AdultCount", "");

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
        .andExpect(jsonPath("$.persons[1].phone", is("061-846-0160")))
        .andExpect(jsonPath("$.persons[2].firstName", is("Person3")))
        .andExpect(jsonPath("$.persons[2].lastName", is("Test")))
        .andExpect(jsonPath("$.persons[2].phone", is("061-846-0160")))
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

}
