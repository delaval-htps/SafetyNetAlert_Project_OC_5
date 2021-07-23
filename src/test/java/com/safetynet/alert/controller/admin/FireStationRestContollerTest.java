package com.safetynet.alert.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alert.exceptions.address.AddressNotFoundException;
import com.safetynet.alert.exceptions.firestation.FireStationAlreadyExistedException;
import com.safetynet.alert.exceptions.firestation.FireStationNotFoundException;
import com.safetynet.alert.exceptions.firestation.FireStationWithIdException;
import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.FireStationService;
import com.safetynet.alert.service.PersonService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

@WebMvcTest(controllers = FireStationRestController.class)
@TestMethodOrder(OrderAnnotation.class)
class FireStationRestContollerTest {

  @MockBean
  private FireStationService fireStationService;

  @MockBean
  private PersonService personService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private Jackson2ObjectMapperBuilder mapperBuilder;

  private FireStation fireStationTest1;
  private FireStation fireStationTest2;
  private FireStation fireStationWithId;
  private FireStation fireStationWithoutId;
  private Person person1;
  private Person person2;
  private Set<String> addresses1;
  private Set<String> addresses2;
  private Set<String> addresses3;
  private List<FireStation> fireStations;


  @BeforeEach
  void setUp() throws Exception {

    addresses1 = new LinkedHashSet();
    addresses1.add("26 av maréchal Foch");
    addresses1.add("310 rue jean jaures");


    addresses2 = new LinkedHashSet();
    addresses2.add("300 av Victor Hugo");
    addresses2.add("350 rue Emile Zola");

    addresses3 = new LinkedHashSet();
    addresses3.add("addressTest");
    addresses3.add("addressTest2");

    fireStationTest1 = new FireStation(1L, 1, addresses1, new HashSet<>());
    fireStationTest2 = new FireStation(2L, 2, addresses2, new HashSet<>());
    fireStationWithId = new FireStation(3L, 3, addresses2, new HashSet<>());
    fireStationWithoutId = new FireStation(null, 3, addresses2, new HashSet<>());

    fireStations = new ArrayList<FireStation>();
    fireStations.add(fireStationTest1);
    fireStations.add(fireStationTest2);

    person1 = new Person(1L, "Nom1", "Prenom1", new Date(System.currentTimeMillis() - 3600),
                         "addressTest", "Culver", 97456, "061-846-0160", "np@email.com", null,
                         null);
    person2 = new Person(2L, "Nom2", "Prenom2", new Date(System.currentTimeMillis() - 3600),
                         "addressTest2", "Culver", 97456, "061-846-0161", "np2@email.com",
                         null, null);

  }

  @Test
  @Order(1)
  void testGetFireStations() throws Exception {

    // Given
    when(fireStationService.getFireStations()).thenReturn(fireStations);

    // When &then
    mockMvc.perform(get("/firestation")).andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(2)))
        .andExpect(jsonPath("$[0].idFireStation", is(1)))
        .andExpect(jsonPath("$[0].numberStation", is(1)))
        .andExpect(jsonPath("$[0].addresses[0]", is("26 av maréchal Foch")))
        .andExpect(jsonPath("$[0].addresses[1]", is("310 rue jean jaures")))
        .andExpect(jsonPath("$[1].idFireStation", is(2)))
        .andExpect(jsonPath("$[1].numberStation", is(2)))
        .andExpect(jsonPath("$[1].addresses[0]", is("300 av Victor Hugo")))
        .andExpect(jsonPath("$[1].addresses[1]", is("350 rue Emile Zola")));

  }

  @Test
  @Order(2)
  void testGetFireStationsById_withValidId_thenReturn200() throws Exception {

    // given
    when(fireStationService.getFireStationJoinAddressesById(Mockito.anyLong()))
        .thenReturn(Optional.of(fireStationTest1));

    // When & then
    mockMvc.perform(get("/firestation/{id}", 1))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(3)))
        .andExpect(jsonPath("$.idFireStation", is(1)))
        .andExpect(jsonPath("$.addresses[0]", is("26 av maréchal Foch")))
        .andExpect(jsonPath("$.addresses[1]", is("310 rue jean jaures")))
        .andExpect(jsonPath("$.numberStation", is(1)));

  }

  @Test
  @Order(3)
  void testGetFireStationsById_withNotValidId_thenReturn404() throws Exception {

    // Given
    when(fireStationService.getFireStationById(Mockito.anyLong()))
        .thenReturn(Optional.empty());

    // when & then
    mockMvc.perform(get("/firestation/{id}", 1))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof FireStationNotFoundException))
        .andExpect(result -> assertEquals(result.getResolvedException().getMessage(),
            "FireStation with Id:1 was not found"));

  }

  @Test
  @Order(4)
  void postFireStation_withPersonsMappedWithAddresses_thenReturn201() throws Exception {

    // Given
    // fireStation doesn't exist.
    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.empty());

    when(fireStationService.saveFireStation(Mockito.any(FireStation.class)))
        .thenReturn(fireStationWithId);

    when(personService.getPersonsByAddress(Mockito.anyString()))
        .thenReturn(Arrays.asList(person1, person2));

    person1.setAddress("350 rue Emile Zola");
    person2.setAddress("300 av Victor Hugo");

    ObjectMapper mapper = mapperBuilder.build();

    // when & then
    mockMvc.perform(post("/firestation").accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStationWithoutId)))

        .andExpect(status().isCreated())
        .andExpect(redirectedUrlPattern("http://*/firestation/*"))
        .andExpect(jsonPath("$.length()", is(3)))
        .andExpect(jsonPath("$.idFireStation", notNullValue()))
        .andExpect(jsonPath("$.numberStation", is(3)))
        .andExpect(jsonPath("$.addresses[0]", is("300 av Victor Hugo")))
        .andExpect(jsonPath("$.addresses[1]", is("350 rue Emile Zola")));

    ArgumentCaptor<FireStation> fireStationCaptor = ArgumentCaptor.forClass(FireStation.class);
    verify(fireStationService, times(2)).saveFireStation(fireStationCaptor.capture());

    List<FireStation> values = fireStationCaptor.getAllValues();
    assertThat(values.get(0).getIdFireStation()).isNull();
    assertThat(values.get(0).getNumberStation()).isEqualTo(3);
    assertThat(values.get(0).getAddresses())
        .containsExactlyInAnyOrder("300 av Victor Hugo", "350 rue Emile Zola");
    assertThat(values.get(1).getPersons())
        .containsExactlyInAnyOrderElementsOf(Arrays.asList(person1, person2));

  }

  @Test
  @Order(5)
  void postFireStation_withNobodyMappedToAddresses_thenReturn201()
      throws Exception {

    // Given
    ObjectMapper mapper = mapperBuilder.build();

    // fireStation doesn't exist.
    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.empty());

    when(fireStationService.saveFireStation(Mockito.any(FireStation.class)))
        .thenReturn(fireStationWithId);

    // when check if persons mapped with address , then return a emptyList
    // nobody is mapped with address of new FireStation
    when(personService.getPersonsByAddress(Mockito.anyString()))
        .thenReturn(new ArrayList());

    // when & then
    mockMvc.perform(post("/firestation").accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStationWithoutId)))

        .andExpect(status().isCreated())
        .andExpect(redirectedUrlPattern("http://*/firestation/*"))
        .andExpect(jsonPath("$.length()", is(3)))
        .andExpect(jsonPath("$.idFireStation", notNullValue()))
        .andExpect(jsonPath("$.numberStation", is(3)))
        .andExpect(jsonPath("$.addresses[0]", is("300 av Victor Hugo")))
        .andExpect(jsonPath("$.addresses[1]", is("350 rue Emile Zola")));

    ArgumentCaptor<FireStation> fireStationCaptor = ArgumentCaptor.forClass(FireStation.class);
    verify(fireStationService, times(1)).saveFireStation(fireStationCaptor.capture());
    assertThat(fireStationCaptor.getValue().getIdFireStation()).isNull();
    assertThat(fireStationCaptor.getValue().getNumberStation()).isEqualTo(3);
    assertThat(fireStationCaptor.getValue().getAddresses())
        .containsExactlyInAnyOrder("300 av Victor Hugo", "350 rue Emile Zola");
    assertThat(fireStationCaptor.getValue().getPersons()).isEmpty();

  }

  @Test
  @Order(6)
  void postFireStation_whenFireStationEmptyAddress_thenReturn200() throws Exception {

    //Given
    // fireStation doesn't exist.
    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.empty());

    //fireStationWithId  and withoutId doesn't have addresses mapped with
    fireStationWithId.setAddresses(new LinkedHashSet());
    fireStationWithoutId.setAddresses(new LinkedHashSet());

    when(fireStationService.saveFireStation(Mockito.any(FireStation.class)))
        .thenReturn(fireStationWithId);

    ObjectMapper mapper = mapperBuilder.build();

    //when & then
    MvcResult result = mockMvc.perform(post("/firestation").accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStationWithoutId)))
        .andExpect(status().isCreated())
        .andExpect(redirectedUrlPattern("http://*/firestation/*"))
        .andExpect(jsonPath("$.length()", is(3)))
        .andExpect(jsonPath("$.idFireStation", notNullValue()))
        .andExpect(jsonPath("$.numberStation", is(3)))
        .andExpect(jsonPath("$.addresses.length()", is(0))).andDo(print()).andReturn();

    ArgumentCaptor<FireStation> fireStationCaptor = ArgumentCaptor.forClass(FireStation.class);
    verify(fireStationService, times(1)).saveFireStation(fireStationCaptor.capture());
    assertThat(fireStationCaptor.getValue().getAddresses()).isEmpty();
    assertThat(fireStationCaptor.getValue().getPersons()).isNullOrEmpty();

  }

  private static Stream<Arguments> factoryArgumentPost() {

    return Stream.of(
        Arguments.of(0, Arrays.asList("26av marechalFoch", "345 rue jean jaures")),
        Arguments.of(5, Arrays.asList(null, "34 rue fauvert")),
        Arguments.of(5, Arrays.asList("", "34ruefauvert")));

  }

  @ParameterizedTest
  @MethodSource("factoryArgumentPost")
  @Order(6)
  void postFireStation_withInValidInput_thenReturn400(int numberStation,
      List<String> addresses) throws Exception {

    // Given
    ObjectMapper mapper = mapperBuilder.build();

    Set<String> addressesTest = new HashSet<>(addresses);

    FireStation fireStation =
        new FireStation(null, numberStation, addressesTest, null);

    // when & then
    mockMvc.perform(post("/firestation").accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStation)))

        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof MethodArgumentNotValidException))
        .andExpect(result -> assertThat(result.getResolvedException().getMessage())
            .contains("Validation failed"))
        .andDo(print());

    verify(fireStationService, never()).saveFireStation(Mockito.any(FireStation.class));

  }

  @Test
  @Order(7)
  void postFireStation_whenFireStationAlreadyExisted_thenReturn400() throws Exception {

    //Given
    ObjectMapper mapper = mapperBuilder.build();

    fireStationTest1.setIdFireStation(null);

    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.of(fireStationTest1));

    //when& then
    MvcResult result = mockMvc
        .perform(post("/firestation").accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(fireStationTest1)))
        .andExpect(status().isBadRequest()).andDo(print()).andReturn();

    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("this FireStation with NumberStation: "
            + fireStationTest1.getNumberStation() + " already Existed");
    assertThat(result.getResolvedException())
        .isInstanceOf(FireStationAlreadyExistedException.class);

  }

  @Test
  @Order(8)
  void postFireStation_withIdFireStationInBody_thenReturn400() throws Exception {

    // given
    ObjectMapper mapper = mapperBuilder.build();

    // when & then
    MvcResult result = mockMvc.perform(post("/firestation")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStationTest1)))

        .andExpect(status().isBadRequest()).andReturn();

    assertThat(result.getResolvedException()).isInstanceOf(FireStationWithIdException.class);
    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("Don't put a id in Body to save new FireStation!");

  }

  @Test
  @Order(8)
  void putFireStation_withPersonsNotMappedWithAddress_thenReturn200()
      throws Exception {

    // given
    ObjectMapper mapper = mapperBuilder.build();

    String addressToMap = "new address";

    // existed FireStation
    when(fireStationService.getFireStationAllFetchByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.of(fireStationWithId));

    // no persons Mapped with address
    when(personService.getPersonsByAddress(Mockito.anyString())).thenReturn(new ArrayList());

    // when & then
    mockMvc.perform(put("/firestation/{address}", addressToMap)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStationWithoutId)))

        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(3)))
        .andExpect(jsonPath("$.idFireStation", notNullValue()))
        .andExpect(jsonPath("$.addresses.length()", is(3)))
        .andExpect(jsonPath("$.addresses[0]", is("300 av Victor Hugo")))
        .andExpect(jsonPath("$.addresses[1]", is("350 rue Emile Zola")))
        .andExpect(jsonPath("$.addresses[2]", is("new address")))
        .andExpect(jsonPath("$.numberStation", is(3))).andDo(print());

    ArgumentCaptor<FireStation> fireStationCaptor = ArgumentCaptor.forClass(FireStation.class);
    verify(fireStationService, times(1))
        .getFireStationAllFetchByNumberStation(Mockito.anyInt());
    verify(fireStationService, times(1)).saveFireStation(fireStationCaptor.capture());

    assertThat(fireStationCaptor.getValue().getIdFireStation()).isNotNull();
    assertThat(fireStationCaptor.getValue().getAddresses())
        .containsExactlyInAnyOrder("300 av Victor Hugo", "350 rue Emile Zola", "new address");
    assertThat(fireStationCaptor.getValue().getNumberStation()).isEqualTo(3);
    assertThat(fireStationCaptor.getValue().getPersons()).isEmpty();

  }

  @Test
  @Order(9)
  void putFireStation_withPersonsMappedWithAddress_thenReturn200()
      throws Exception {

    // given
    // existed FireStation
    when(fireStationService.getFireStationAllFetchByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.of(fireStationWithId));

    // persons Mapped with address
    person1.setAddress("addressMappedToPersons");
    person2.setAddress("addressMappedToPersons");

    when(personService.getPersonsByAddress(Mockito.anyString()))
        .thenReturn(Arrays.asList(person1, person2));

    ObjectMapper mapper = mapperBuilder.build();
    String addressToMap = "addressMappedToPersons";

    // when & then
    mockMvc.perform(put("/firestation/{address}", addressToMap)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStationWithoutId)))

        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(3)))
        .andExpect(jsonPath("$.idFireStation", notNullValue()))
        .andExpect(jsonPath("$.addresses.length()", is(3)))
        .andExpect(jsonPath("$.addresses[0]", is("300 av Victor Hugo")))
        .andExpect(jsonPath("$.addresses[1]", is("350 rue Emile Zola")))
        .andExpect(jsonPath("$.addresses[2]", is("addressMappedToPersons")))
        .andExpect(jsonPath("$.numberStation", is(3))).andDo(print());

    ArgumentCaptor<FireStation> fireStationCaptor = ArgumentCaptor.forClass(FireStation.class);
    verify(fireStationService, times(1))
        .getFireStationAllFetchByNumberStation(Mockito.anyInt());
    verify(fireStationService, times(1)).saveFireStation(fireStationCaptor.capture());

    List<FireStation> values = fireStationCaptor.getAllValues();
    assertThat(values.get(0).getIdFireStation()).isNotNull();
    assertThat(values.get(0).getAddresses()).containsExactlyInAnyOrder("300 av Victor Hugo",
        "350 rue Emile Zola",
        "addressMappedToPersons");
    assertThat(values.get(0).getNumberStation()).isEqualTo(3);
    assertThat(values.get(0).getPersons()).containsExactlyInAnyOrder(person1, person2);

  }


  @Test
  @Order(10)
  void putFireStation_withValidAddressAllreadyMappedWithGivenFireStation_thenReturn400()
      throws Exception {

    // given
    ObjectMapper mapper = mapperBuilder.build();

    String addressToMap = "300 av Victor Hugo";

    when(fireStationService.getFireStationAllFetchByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.of(fireStationWithId));

    // when & then

    MvcResult result = mockMvc.perform(put("/firestation/{address}", addressToMap)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStationWithoutId)))

        .andExpect(status().isBadRequest()).andReturn();

    verify(fireStationService, times(1))
        .getFireStationAllFetchByNumberStation(Mockito.anyInt());
    verify(fireStationService, never()).saveFireStation(Mockito.any(FireStation.class));

    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("This FireStation already mapped with given address."
            + "Please give a another fireStation to map !");

  }

  @Test
  @Order(11)
  void putFireStation_withNotFoundNumberStation_thenReturn404() throws Exception {

    // given
    ObjectMapper mapper = mapperBuilder.build();

    when(fireStationService.getFireStationAllFetchByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.empty());

    // when & then
    MvcResult result = mockMvc.perform(put("/firestation/{address}", "address")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStationWithoutId)))

        .andExpect(status().isNotFound()).andReturn();

    verify(fireStationService, never()).saveFireStation(Mockito.any(FireStation.class));
    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("fireStation given in body request with numberStation:3 doesn't exist !");

  }

  @Test
  @Order(12)
  void putFireStation_withIdFireStationInBody_thenReturn400() throws Exception {

    // given
    ObjectMapper mapper = mapperBuilder.build();

    // when & then
    MvcResult result = mockMvc.perform(put("/firestation/{numberStation}", 5)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStationTest1)))

        .andExpect(status().isBadRequest()).andReturn();

    assertThat(result.getResolvedException()).isInstanceOf(FireStationWithIdException.class);
    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("Don't put a id in Body to save new FireStation!");

  }

  @ParameterizedTest
  @NullAndEmptySource
  @Order(13)
  void putFireStation_withInValidInputAddress_thenReturn400(String addressToMap)
      throws Exception {

    // given
    ObjectMapper mapper = mapperBuilder.build();

    // when & then
    mockMvc.perform(put("/firestation/{address}", addressToMap)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStationTest1)))

        .andExpect(status().isBadRequest()).andDo(print());

    verify(fireStationService, never()).saveFireStation(Mockito.any(FireStation.class));

  }


  @Test
  @Order(14)
  void deleteMappingFireStation_withValidNumberStation_thenReturn200() throws Exception {

    //given

    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.of(fireStationTest1));

    // when & then
    mockMvc.perform(delete("/firestation/station/{numberStation}", 1))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$",
            is("FireStation with NumberStation:1 was deleted!")));

    ArgumentCaptor<FireStation> fireStationCaptor = ArgumentCaptor.forClass(FireStation.class);
    verify(fireStationService, times(1)).deleteFireStation(fireStationCaptor.capture());
    assertThat(fireStationCaptor.getValue().getAddresses()).isEqualTo(addresses1);
    assertThat(fireStationCaptor.getValue().getIdFireStation()).isNotNull();
    assertThat(fireStationCaptor.getValue().getNumberStation()).isEqualTo(1);

  }

  @Test
  @Order(15)
  void deleteMappingFireStation_withNoFoundNumberStation_thenReturn404()
      throws Exception {

    //given
    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.empty());

    // when & then
    MvcResult result =
        mockMvc.perform(delete("/firestation/station/{numberStation}", 1))
            .andExpect(status().isNotFound()).andDo(print()).andReturn();

    verify(fireStationService, never()).deleteFireStation(Mockito.any(FireStation.class));
    assertThat(result.getResolvedException()).isInstanceOf(FireStationNotFoundException.class);
    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("FireStation with NumberStation:1 was not found");

  }

  @Test
  @Order(16)
  void deleteAddressfromFireStation_withValidAddress_thenReturn200() throws Exception {

    //Given
    when(fireStationService.getFireStationsMappedToAddress(Mockito.anyString()))
        .thenReturn(Arrays.asList(fireStationTest1));

    // when & then
    mockMvc.perform(delete("/firestation/address/{address}", "26 av maréchal Foch"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$",
            is("FireStation with numberStations = {[1]} mapped to address was deleted")));

    ArgumentCaptor<FireStation> fireStationCaptor = ArgumentCaptor.forClass(FireStation.class);
    verify(fireStationService, times(1)).deleteFireStation(fireStationCaptor.capture());
    assertThat(fireStationCaptor.getValue().getAddresses()).isEqualTo(addresses1);
    assertThat(fireStationCaptor.getValue().getIdFireStation()).isNotNull();
    assertThat(fireStationCaptor.getValue().getNumberStation()).isEqualTo(1);

  }

  @Test
  @Order(17)
  void deleteAddressfromFireStation_withNotFoundAddress_thenReturn404() throws Exception {

    //given
    when(fireStationService.getFireStationsMappedToAddress(Mockito.anyString()))
        .thenReturn(new ArrayList());

    // when & then
    MvcResult result = mockMvc.perform(delete("/firestation/address/{address}", "testAddress"))
        .andExpect(status().isNotFound()).andReturn();

    verify(fireStationService, never()).deleteFireStation(Mockito.any(FireStation.class));
    assertThat(result.getResolvedException()).isInstanceOf(AddressNotFoundException.class);
    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("There is no FireStation mapped with this address:testAddress");

  }

  @ParameterizedTest
  @NullAndEmptySource
  @Order(18)
  void deleteAddressfromFireStation_withNoValidAddress_thenReturn400(String address)
      throws Exception {

    // when & then
    mockMvc.perform(delete("/firestation/address/{address}", address))
        .andExpect(status().isBadRequest()).andDo(print());

    verify(fireStationService, never()).deleteFireStation(Mockito.any(FireStation.class));

  }


}
