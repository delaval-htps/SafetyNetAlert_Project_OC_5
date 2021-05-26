package com.safetynet.alert.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
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
import com.safetynet.alert.exceptions.firestation.FireStationNotFoundException;
import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.service.FireStationService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
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
import javax.validation.constraints.NotEmpty;

@WebMvcTest(controllers = FireStationRestController.class)
@TestMethodOrder(OrderAnnotation.class)
class FireStationRestContollerTest {

  @MockBean
  private FireStationService fireStationService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private Jackson2ObjectMapperBuilder mapperBuilder;

  private static FireStation fireStationTest1;
  private static FireStation fireStationTest2;
  private static FireStation fireStationWithId;
  private static FireStation fireStationWithoutId;

  private static List<FireStation> fireStations = new ArrayList<FireStation>();

  @BeforeAll
  static void setUpBeforeClass() throws Exception {

    final Set<@NotEmpty String> addresses1 = new LinkedHashSet();
    addresses1.add("26 av maréchal Foch");
    addresses1.add("310 rue jean jaures");


    final Set<@NotEmpty String> addresses2 = new LinkedHashSet();
    addresses2.add("300 av Victor Hugo");
    addresses2.add("350 rue Emile Zola");

    final Set<@NotEmpty String> addresses3 = new LinkedHashSet();
    addresses3.add("adressTest de fireStationTest3");
    addresses3.add("addressTest2 de fireStationTest3");

    fireStationTest1 = new FireStation(1L, 1, addresses1, null);
    fireStationTest2 = new FireStation(2L, 2, addresses2, null);
    fireStationWithId = new FireStation(3L, 3, addresses3, null);
    fireStationWithoutId = new FireStation(null, 3, addresses3, null);

    fireStations.add(fireStationTest1);
    fireStations.add(fireStationTest2);

  }

  @BeforeEach
  void setUp() throws Exception {}

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
    when(fireStationService.getFireStationById(Mockito.anyLong()))
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
  void postFireStation_withValidInput_thenReturn201() throws Exception {

    // Given
    ObjectMapper mapper = mapperBuilder.build();
    ArgumentCaptor<FireStation> fireStationCaptor = ArgumentCaptor.forClass(FireStation.class);
    String[] addressesTest = {"adressTest de fireStationTest3",
                              "addressTest2 de fireStationTest3"};
    when(fireStationService.saveFireStation(Mockito.any(FireStation.class)))
        .thenReturn(fireStationWithId);

    // when & then
    mockMvc.perform(post("/firestation").accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStationWithoutId)))

        .andExpect(status().isCreated())
        .andExpect(redirectedUrlPattern("http://*/firestation/3"))
        .andExpect(jsonPath("$.length()", is(3)))
        .andExpect(jsonPath("$.idFireStation", is(3)))
        .andExpect(jsonPath("$.numberStation", is(3)))
        .andExpect(jsonPath("$.addresses[0]", is("adressTest de fireStationTest3")))
        .andExpect(jsonPath("$.addresses[1]", is("addressTest2 de fireStationTest3")));

    verify(fireStationService, times(1)).saveFireStation(fireStationCaptor.capture());
    assertThat(fireStationCaptor.getValue().getIdFireStation()).isNull();
    assertThat(fireStationCaptor.getValue().getNumberStation()).isEqualTo(3);
    assertThat(fireStationCaptor.getValue().getAddresses())
        .containsExactlyInAnyOrder(addressesTest);

  }

  private static Stream<Arguments> factoryArgumentPost() {

    return Stream.of(
        Arguments.of(0, Arrays.asList("26av marechalFoch", "345 rue jean jaures")),
        Arguments.of(5, Arrays.asList(null, "34 rue fauvert")),
        Arguments.of(5, Arrays.asList("", "34ruefauvert")));

  }

  @ParameterizedTest
  @MethodSource("factoryArgumentPost")
  @Order(5)
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
  @Order(6)
  void putFireStation_withValidAddressNotMappedAndFireStation_thenReturn200()
      throws Exception {

    // given
    ObjectMapper mapper = mapperBuilder.build();
    ArgumentCaptor<FireStation> fireStationCaptor = ArgumentCaptor.forClass(FireStation.class);

    String addressToMap = "new address to map with FireStation";

    when(fireStationService.getFireStationMappedToAddress(Mockito.anyString()))
        .thenReturn(Optional.empty());

    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.of(fireStationTest1));

    // when & then
    mockMvc.perform(put("/firestation/{address}", addressToMap)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStationTest1)))

        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(1)))
        .andExpect(jsonPath("$[0].idFireStation", is(1)))
        .andExpect(jsonPath("$[0].addresses.length()", is(3)))
        .andExpect(jsonPath("$[0].addresses[0]", is("26 av maréchal Foch")))
        .andExpect(jsonPath("$[0].addresses[1]", is("310 rue jean jaures")))
        .andExpect(jsonPath("$[0].addresses[2]", is("new address to map with FireStation")))
        .andExpect(jsonPath("$[0].numberStation", is(1))).andDo(print());

    verify(fireStationService, times(1)).getFireStationMappedToAddress(Mockito.anyString());
    verify(fireStationService, times(1)).getFireStationByNumberStation(Mockito.anyInt());
    verify(fireStationService, times(1)).saveFireStation(fireStationCaptor.capture());

    assertThat(fireStationCaptor.getValue().getIdFireStation()).isEqualTo(1L);
    assertThat(fireStationCaptor.getValue().getAddresses())
        .containsExactlyInAnyOrder("26 av maréchal Foch",
            "310 rue jean jaures",
            "new address to map with FireStation");
    assertThat(fireStationCaptor.getValue().getNumberStation()).isEqualTo(1);

  }

  @Test
  @Order(7)
  void putFireStation_withValidAddressAllreadyMappedWithGivenFireStation_thenReturn200()
      throws Exception {

    // given
    ObjectMapper mapper = mapperBuilder.build();
    ArgumentCaptor<FireStation> fireStationCaptor = ArgumentCaptor.forClass(FireStation.class);

    //address already map to fireStationTest2"310 rue jean jaures"
    String addressToMap = "300 av Victor Hugo";

    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.of(fireStationTest1));

    when(fireStationService.getFireStationMappedToAddress(Mockito.anyString()))
        .thenReturn(Optional.of(fireStationTest2));

    // when & then
    mockMvc.perform(put("/firestation/{address}", addressToMap)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStationTest1)))

        .andExpect(status().isOk()).andDo(print());

    verify(fireStationService, times(1)).getFireStationMappedToAddress(Mockito.anyString());
    verify(fireStationService, times(1)).getFireStationByNumberStation(Mockito.anyInt());
    verify(fireStationService, times(2)).saveFireStation(fireStationCaptor.capture());

    fireStations = fireStationCaptor.getAllValues();

    // Modification for fireStation that normally expected
    fireStationTest2.getAddresses().remove(addressToMap);
    fireStationTest1.getAddresses().add(addressToMap);

    assertThat(fireStations.get(1)).isEqualTo(fireStationTest1);
    assertThat(fireStations.get(0)).isEqualTo(fireStationTest2);

  }

  @Test
  @Order(8)
  void putFireStation_withValidAddressAllreadyMappedWithGivenFireStation_thenReturn400()
      throws Exception {

    // given
    ObjectMapper mapper = mapperBuilder.build();

    String addressToMap = "26 av maréchal Foch";

    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.of(fireStationTest1));

    when(fireStationService.getFireStationMappedToAddress(Mockito.anyString()))
        .thenReturn(Optional.of(fireStationTest1));

    // when & then

    MvcResult result = mockMvc.perform(put("/firestation/{address}", addressToMap)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStationTest1)))

        .andExpect(status().isBadRequest()).andReturn();

    verify(fireStationService, never()).getFireStationMappedToAddress(Mockito.anyString());
    verify(fireStationService, times(1)).getFireStationByNumberStation(Mockito.anyInt());
    verify(fireStationService, never()).saveFireStation(Mockito.any(FireStation.class));

    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("This FireStation already mapped with given address."
            + "Please give a another fireStation to map !");

  }

  @Test
  @Order(9)
  void putFireStation_withNotFoundNumberStation_thenReturn404() throws Exception {

    // given
    ObjectMapper mapper = mapperBuilder.build();

    // when & then
    MvcResult result = mockMvc.perform(put("/firestation/{numberStation}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStationTest1)))

        .andExpect(status().isNotFound()).andReturn();

    verify(fireStationService, never()).saveFireStation(Mockito.any(FireStation.class));
    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("fireStation given in body request with numberStation:1 doesn't exist !");

  }

  @ParameterizedTest
  @NullAndEmptySource
  //  @ValueSource(strings = {"      "})  doesn't work!!!
  @Order(10)
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

  private static Stream<Arguments> factoryArgumentPutInvalidFireStation() {

    return Stream.of(

        Arguments.of("34 rue fauvert",
            2L,
            1,
            Arrays.asList("26 av maréchal Foch", "310 rue jean jaures")),
        Arguments.of("34 rue fauvert", 1L, 1, Arrays.asList("29 15th St")),
        Arguments.of("34 rue fauvert", 1L, 1, Arrays.asList("310 rue jean jaures")),
        Arguments.of("34 rue fauvert",
            1L,
            2,
            Arrays.asList("26 av maréchal Foch", "310 rue jean jaures")));

  }

  @ParameterizedTest
  @MethodSource("factoryArgumentPutInvalidFireStation")
  @Order(11)
  void putFireStation_withInValidInputFireStation_thenReturn400(String addressToMap,
      Long id,
      int numberStation,
      List<String> addresses) throws Exception {

    // given
    ObjectMapper mapper = mapperBuilder.build();

    Set<String> addressesTest = new HashSet<>(addresses);

    FireStation fireStationArgument =
        new FireStation(id, numberStation, addressesTest, null);

    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.of(fireStationTest1));

    // when & then
    MvcResult result = mockMvc.perform(put("/firestation/{address}", addressToMap)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(fireStationArgument)))

        .andExpect(status().isBadRequest()).andDo(print()).andReturn();

    verify(fireStationService, never()).saveFireStation(Mockito.any(FireStation.class));

    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("fireStation in body request doesn't match with a existed fireStation !"
            + " Check fields are correctly entered");

  }

  @Test
  @Order(12)
  void deleteMappingFireStation_withValidNumberStation_thenReturn200() throws Exception {

    //given

    when(fireStationService.getFireStationByNumberStation(Mockito.anyInt()))
        .thenReturn(Optional.of(fireStationTest1));

    ArgumentCaptor<FireStation> fireStationCaptor = ArgumentCaptor.forClass(FireStation.class);

    // when & then
    mockMvc.perform(delete("/firestation/station/{numberStation}", 1))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(3)))
        .andExpect(jsonPath("$.idFireStation", is(1)))
        .andExpect(jsonPath("$.addresses.length()", is(0)))
        .andExpect(jsonPath("$.numberStation", is(1))).andDo(print());

    verify(fireStationService, times(1)).saveFireStation(fireStationCaptor.capture());
    assertThat(fireStationCaptor.getValue().getAddresses()).isEmpty();
    assertThat(fireStationCaptor.getValue().getIdFireStation()).isEqualTo(1L);
    assertThat(fireStationCaptor.getValue().getNumberStation()).isEqualTo(1);

  }

  @Test
  @Order(13)
  void deleteMappingFireStation_withNoValidNumberStation_thenReturn400()
      throws Exception {

    //given

    // when & then
    mockMvc.perform(delete("/firestation/station/{numberStation}", "abcdef"))
        .andExpect(status().isBadRequest()).andDo(print());

    verify(fireStationService, never()).saveFireStation(Mockito.any(FireStation.class));

  }

  @Test
  @Order(14)
  void deleteAddressfromFireStation_withValidAddress_thenReturn200() throws Exception {

    //Given
    ArgumentCaptor<FireStation> fireStationCaptor = ArgumentCaptor.forClass(FireStation.class);

    when(fireStationService.getFireStationMappedToAddress(Mockito.anyString()))
        .thenReturn(Optional.of(fireStationTest1));

    // when & then
    mockMvc.perform(delete("/firestation/address/{address}", "26 av maréchal Foch"))
        .andExpect(status().isOk()).andDo(print()).andReturn();

    verify(fireStationService, times(1)).saveFireStation(fireStationCaptor.capture());
    assertThat(fireStationCaptor.getValue().getIdFireStation()).isEqualTo(1L);
    assertThat(fireStationCaptor.getValue().getNumberStation()).isEqualTo(1);
    assertThat(fireStationCaptor.getValue().getAddresses())
        .doesNotContain("26 av maréchal Foch");

  }

  @Test
  @Order(15)
  void deleteAddressfromFireStation_withNotFoundAddress_thenReturn404() throws Exception {

    //given
    when(fireStationService.getFireStationMappedToAddress(Mockito.anyString()))
        .thenReturn(Optional.empty());

    // when & then
    MvcResult result = mockMvc.perform(delete("/firestation/address/{address}", "testAddress"))
        .andExpect(status().isNotFound()).andReturn();

    verify(fireStationService, never()).saveFireStation(Mockito.any(FireStation.class));

    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("There is no FireStation mapped with this address:testAddress");

  }

  @ParameterizedTest
  @NullAndEmptySource
  //@ValueSource(strings = {" "})
  @Order(16)
  void deleteAddressfromFireStation_withNoValidAddress_thenReturn400(String address)
      throws Exception {

    // when & then
    mockMvc.perform(delete("/firestation/address/{address}", address))
        .andExpect(status().isBadRequest()).andDo(print());

    verify(fireStationService, never()).saveFireStation(Mockito.any(FireStation.class));

  }


}
