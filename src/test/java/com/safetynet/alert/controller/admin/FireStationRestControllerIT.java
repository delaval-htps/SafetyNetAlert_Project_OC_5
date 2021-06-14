package com.safetynet.alert.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alert.CommandLineRunnerTaskExcecutor;
import com.safetynet.alert.database.LoadDataStrategyFactory;
import com.safetynet.alert.database.StrategyName;
import com.safetynet.alert.exceptions.firestation.FireStationAlreadyExistedException;
import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.service.FireStationService;
import java.util.Arrays;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestMethodOrder(OrderAnnotation.class)
class FireStationRestControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private FireStationService fireStationService;

  @Autowired
  private Jackson2ObjectMapperBuilder mapperBuilder;

  @MockBean
  private CommandLineRunnerTaskExcecutor clrte;

  @Autowired
  private LoadDataStrategyFactory loadDataStrategyFactory;

  private FireStation fireStationTest;
  private Set<String> addresses;

  @BeforeEach
  void setUp() throws Exception {

    loadDataStrategyFactory.findStrategy(StrategyName.StrategyTest)
        .loadDatabaseFromSource();

    addresses = new HashSet<String>();
    addresses.add("26 av maréchal Foch");
    addresses.add("310 av jean Jaures");

    fireStationTest = new FireStation();
    fireStationTest.setNumberStation(5);
    fireStationTest.setAddresses(addresses);

  }

  @Test
  @Order(1)
  void testGetFireStations() throws Exception {

    mockMvc.perform(get("/firestation"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(2)))
        .andExpect(jsonPath("$[0].idFireStation", is(1)))
        .andExpect(jsonPath("$[0].addresses[0]", is("1509 av marechal foch")))
        .andExpect(jsonPath("$[0].addresses[1]", is("1509 Culver St")))
        .andExpect(jsonPath("$[0].numberStation", is(3)))
        .andExpect(jsonPath("$[1].idFireStation", is(2)))
        .andExpect(jsonPath("$[1].addresses[0]", is("29 15th St")))
        .andExpect(jsonPath("$[1].numberStation", is(2)));

  }

  @Test
  @Order(2)
  void testGetFireStationsById_withValidId_thenReturn200() throws Exception {

    mockMvc.perform(get("/firestation/{id}", 1))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(3)))
        .andExpect(jsonPath("$.idFireStation", is(1)))
        .andExpect(jsonPath("$.addresses[0]", is("1509 av marechal foch")))
        .andExpect(jsonPath("$.addresses[1]", is("1509 Culver St")))
        .andExpect(jsonPath("$.numberStation", is(3)));

  }

  @Test
  @Order(3)
  void testGetFireStationsById_withNotValidId_thenReturn404() throws Exception {

    mockMvc.perform(get("/firestation/{id}", 3))
        .andExpect(status().isNotFound());

  }

  @Test
  @Order(4)
  void postFireStation_withValidInput_thenReturn201() throws Exception {

    // Given
    ObjectMapper mapper = mapperBuilder.build();

    // when & then
    mockMvc.perform(post("/firestation").accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStationTest)))

        .andExpect(status().isCreated())
        .andExpect(redirectedUrlPattern("http://*/firestation/3"))
        .andExpect(jsonPath("$.length()", is(3)))
        .andExpect(jsonPath("$.idFireStation", is(3)))
        .andExpect(jsonPath("$.numberStation", is(5)))
        .andExpect(jsonPath("$.addresses[0]", is("26 av maréchal Foch")))
        .andExpect(jsonPath("$.addresses[1]", is("310 av jean Jaures")));

  }

  @Test
  @Order(5)
  void postFireStation_whenFireStationAlreadyExisted_thenReturn400() throws Exception {

    //Given
    ObjectMapper mapper = mapperBuilder.build();
    Optional<FireStation> existedFireStation =
        fireStationService.getFireStationJoinAllById(1L);
    //when& then
    MvcResult result = mockMvc
        .perform(post("/firestation").accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(existedFireStation.get())))
        .andExpect(status().isBadRequest()).andDo(print()).andReturn();

    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("this FireStation with NumberStation: "
            + existedFireStation.get().getNumberStation() + " already Existed");
    assertThat(result.getResolvedException())
        .isInstanceOf(FireStationAlreadyExistedException.class);

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
        .andDo(print());

  }

  @Test
  @Order(7)
  void putFireStation_withValidAddressNotMappedAndFireStation_thenReturn200()
      throws Exception {

    // given
    ObjectMapper mapper = mapperBuilder.build();

    String addressToMap = "new address to map with FireStation";

    Set<String> addressestest = new LinkedHashSet<String>();
    addressestest.add("1509 av marechal foch");
    addressestest.add("1509 Culver St");


    FireStation existedFireStation = new FireStation(1L, 3, addressestest, null);

    // when & then
    mockMvc.perform(put("/firestation/{address}", addressToMap)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(existedFireStation)))

        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(1)))
        .andExpect(jsonPath("$[0].idFireStation", is(1)))
        .andExpect(jsonPath("$[0].addresses.length()", is(3)))
        .andExpect(jsonPath("$[0].addresses[0]", is("1509 av marechal foch")))
        .andExpect(jsonPath("$[0].addresses[1]", is("1509 Culver St")))
        .andExpect(jsonPath("$[0].addresses[2]", is("new address to map with FireStation")))
        .andExpect(jsonPath("$[0].numberStation", is(3))).andDo(print());

  }

  @Test
  @Order(8)
  void putFireStation_withValidAddressAlreadyMappedAndFireStation_thenReturn200()
      throws Exception {

    // given
    ObjectMapper mapper = mapperBuilder.build();

    // address already mapped to fireStation N°2
    String addressToMap = "29 15th St";

    //creation of exited fireStation N°3
    Set<String> addressestest = new LinkedHashSet<String>();
    addressestest.add("1509 av marechal foch");
    addressestest.add("1509 Culver St");

    FireStation existedFireStation = new FireStation(1L, 3, addressestest, null);

    // when & then
    mockMvc.perform(put("/firestation/{address}", addressToMap)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(existedFireStation)))

        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(2)))
        .andExpect(jsonPath("$[0].idFireStation", is(2)))
        .andExpect(jsonPath("$[0].addresses.length()", is(0)))
        .andExpect(jsonPath("$[0].numberStation", is(2)))
        .andExpect(jsonPath("$[1].idFireStation", is(1)))
        .andExpect(jsonPath("$[1].addresses.length()", is(3)))
        .andExpect(jsonPath("$[1].addresses[0]", is("1509 av marechal foch")))
        .andExpect(jsonPath("$[1].addresses[1]", is("1509 Culver St")))
        .andExpect(jsonPath("$[1].addresses[2]", is("29 15th St")))
        .andExpect(jsonPath("$[1].numberStation", is(3))).andDo(print());

  }

  @Test
  @Order(9)
  void putFireStation_withValidAddressAllreadyMappedWithGivenFireStation_thenReturn400()
      throws Exception {

    // given
    ObjectMapper mapper = mapperBuilder.build();

    String addressToMap = "1509 Culver St";

    Set<String> addresses = new LinkedHashSet<String>();
    addresses.add("1509 Culver St");
    addresses.add("1509 av marechal foch");
    FireStation existedFireStation = new FireStation(1L, 3, addresses, null);

    // when & then
    MvcResult result = mockMvc.perform(put("/firestation/{address}", addressToMap)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(existedFireStation)))

        .andExpect(status().isBadRequest()).andReturn();

    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("This FireStation already mapped with given address."
            + "Please give a another fireStation to map !");

  }

  @Test
  @Order(10)
  void putFireStation_withNotFoundNumberStation_thenReturn404() throws Exception {

    // given
    ObjectMapper mapper = mapperBuilder.build();

    // when & then
    MvcResult result = mockMvc.perform(put("/firestation/{numberStation}", 5)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStationTest)))

        .andExpect(status().isNotFound()).andReturn();

    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("fireStation given in body request with numberStation:5 doesn't exist !");

  }

  @ParameterizedTest
  @NullAndEmptySource
  // @ValueSource(strings= {" "})    doesn't work !!!
  @Order(11)
  void putFireStation_withInValidInputAddress_thenReturn400(String addressToMap)
      throws Exception {

    // given
    ObjectMapper mapper = mapperBuilder.build();

    // when & then
    mockMvc.perform(put("/firestation/{address}", addressToMap)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStationTest)))

        .andExpect(status().isBadRequest()).andDo(print());

  }

  private static Stream<Arguments> factoryArgumentPutInvalidFireStation() {

    return Stream.of(

        Arguments.of("34 rue fauvert", 1L, 2, Arrays.asList("29 15th St")),
        Arguments.of("34 rue fauvert", 2L, 3, Arrays.asList("29 15th St")),
        Arguments.of("34 rue fauvert", 2L, 2, Arrays.asList("1509 Culver St")));

  }

  @ParameterizedTest
  @MethodSource("factoryArgumentPutInvalidFireStation")
  @Order(12)
  void putFireStation_withInValidInputFireStation_thenReturn400(String addressToMap,
      Long id,
      int numberStation,
      List<String> addresses) throws Exception {

    // given
    ObjectMapper mapper = mapperBuilder.build();

    Set<String> addressesTest = new HashSet<>(addresses);

    FireStation fireStationArgument =
        new FireStation(id, numberStation, addressesTest, null);

    // when & then
    MvcResult result = mockMvc.perform(put("/firestation/{address}", addressToMap)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStationArgument)))

        .andExpect(status().isBadRequest()).andDo(print()).andReturn();

    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("fireStation in body request doesn't match with a existed fireStation !"
            + " Check fields are correctly entered");

  }

  @Test
  @Order(13)
  void deleteMappingFireStation_withValidNumberStation_thenReturn200() throws Exception {

    // when & then
    mockMvc.perform(delete("/firestation/station/{numberStation}", 3))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(3)))
        .andExpect(jsonPath("$.idFireStation", is(1)))
        .andExpect(jsonPath("$.addresses.length()", is(0)))
        .andExpect(jsonPath("$.numberStation", is(3))).andDo(print());

  }

  @Test
  @Order(14)
  void deleteMappingFireStation_withNotFoundNumberStation_thenReturn404() throws Exception {

    // when & then
    mockMvc.perform(delete("/firestation/station/{numberStation}", 1))
        .andExpect(status().isNotFound()).andDo(print());

  }

  @Test
  @Order(15)
  void deleteMappingFireStation_withNoValidNumberStation_thenReturn400()
      throws Exception {

    //given

    // when & then
    mockMvc.perform(delete("/firestation/station/{numberStation}", "abcdef"))
        .andExpect(status().isBadRequest()).andDo(print());

  }

  @Test
  @Order(16)
  void deleteAddressfromFireStation_withValidAddress_thenReturn200() throws Exception {

    // when & then
    MvcResult result =
        mockMvc.perform(delete("/firestation/address/{address}", "1509 Culver St"))
            .andExpect(status().isOk()).andDo(print()).andReturn();

    String contentResult = result.getResponse().getContentAsString();
    assertThat(contentResult).doesNotContain("1509 Culver St");

  }

  @Test
  @Order(17)
  void deleteAddressfromFireStation_withNotFoundAddress_thenReturn404() throws Exception {

    // when & then
    mockMvc.perform(delete("/firestation/address/{address}", "testAddress"))
        .andExpect(status().isNotFound());

  }

  @ParameterizedTest
  @NullAndEmptySource
  //@ValueSource(strings = {" "})
  @Order(18)
  void deleteAddressfromFireStation_withNoValidAddress_thenReturn400(String address)
      throws Exception {

    // when & then
    mockMvc.perform(delete("/firestation/address/{address}", address))
        .andExpect(status().isBadRequest()).andDo(print());

  }


}
