package com.safetynet.alert.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alert.exceptions.person.PersonAlreadyExistedException;
import com.safetynet.alert.exceptions.person.PersonChangedNamesException;
import com.safetynet.alert.exceptions.person.PersonNotFoundException;
import com.safetynet.alert.exceptions.person.PersonWithIdException;
import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.model.MedicalRecord;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.FireStationService;
import com.safetynet.alert.service.PersonService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
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

@TestMethodOrder(OrderAnnotation.class)
@WebMvcTest(controllers = PersonRestController.class)
class PersonRestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private Jackson2ObjectMapperBuilder mapperBuilder;

  @MockBean
  private PersonService personService;

  @MockBean
  private FireStationService fireStationService;


  private static Person mockPerson1;
  private static Person mockPerson2;
  private static Person mockPersonWithId;
  private static Person mockPersonWithoutId;
  private static Person mockPersonWithIdAndChanges;
  private static FireStation mockFireStation1;
  private static FireStation mockFireStation2;
  private static Set<String> addresses1;
  private static Set<String> addresses2;
  private static Set<FireStation> mockSetFireStation;
  private static Set<FireStation> mockSetFireStation2;
  private SimpleDateFormat sdf;


  @BeforeEach
  void setUpBeforeClass() throws Exception {

    sdf = new SimpleDateFormat("MM/dd/yyyy");

    mockPerson1 = new Person(null, "Dorian", "Delaval", sdf.parse("12/27/1976"),
                             "26 av marechal foch", "Cassis", 13260,
                             "061-846-0160", "delaval.htps@gmail.com",
                             null, null);

    mockPerson2 = new Person(null, "Bernard", "Delaval", sdf.parse("12/28/1976"),
                             "8 rue jean jaures", "Bruay Sur Escaut", 59860,
                             "061-846-0260", "delaval.b@email.com",
                             null, null);

    mockPersonWithId = new Person(1L, "Dorian", "Delaval", sdf.parse("12/27/1976"),
                                  "26 av marechal foch", "Cassis", 13260,
                                  "061-846-0160", "delaval.htps@gmail.com",
                                  null, null);

    mockPersonWithoutId = new Person(null, "Dorian", "Delaval", sdf.parse("12/27/1976"),
                                     "26 av marechal foch", "Cassis", 13260,
                                     "061-846-0160", "delaval.htps@gmail.com",
                                     null, null);
    mockPersonWithIdAndChanges = new Person(1L, "Dorian", "Delaval", sdf.parse("12/27/1976"),
                                            "26 av marechal foch", "Cassis", 13260,
                                            "061-846-0160", "delaval.htps@gmail.com",
                                            null, null);
    addresses1 = new HashSet<String>();
    addresses1.add("26 av marechal Foch");

    addresses2 = new HashSet<String>();
    addresses2.add("29 15th St");

    mockFireStation1 = new FireStation();
    mockFireStation1.setIdFireStation(1L);
    mockFireStation1.setNumberStation(1);
    mockFireStation1.setAddresses(addresses1);

    mockFireStation2 = new FireStation();
    mockFireStation2.setIdFireStation(2L);
    mockFireStation2.setNumberStation(2);
    mockFireStation2.setAddresses(addresses2);

    mockSetFireStation = new HashSet<>();
    mockSetFireStation2 = new HashSet<>();

  }

  @Test
  @Order(1)
  void getPersons() throws Exception {

    // Given
    mockPersonWithoutId.setIdPerson(2L);
    mockPersonWithoutId.setFirstName("Bernard");
    mockPersonWithoutId.setPhone("061-846-0260");
    List<Person> persons = Arrays.asList(mockPersonWithId, mockPersonWithoutId);

    when(personService.getPersons()).thenReturn(persons);

    //When & Then
    mockMvc.perform(get("/person")).andExpect(status().isOk())
        .andExpect(jsonPath("$").exists())
        .andExpect(jsonPath("$.length()", is(2)))
        .andExpect(jsonPath("$[0].idPerson", is(1)))
        .andExpect(jsonPath("$[0].address", is("26 av marechal foch")))
        .andExpect(jsonPath("$[0].birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$[0].city", is("Cassis")))
        .andExpect(jsonPath("$[0].email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$[0].firstName", is("Dorian")))
        .andExpect(jsonPath("$[0].lastName", is("Delaval")))
        .andExpect(jsonPath("$[0].phone", is("061-846-0160")))
        .andExpect(jsonPath("$[0].zip", is(13260)))
        .andExpect(jsonPath("$[1].idPerson", is(2)))
        .andExpect(jsonPath("$[1].address", is("26 av marechal foch")))
        .andExpect(jsonPath("$[1].city", is("Cassis")))
        .andExpect(jsonPath("$[1].email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$[1].firstName", is("Bernard")))
        .andExpect(jsonPath("$[1].lastName", is("Delaval")))
        .andExpect(jsonPath("$[1].phone", is("061-846-0260")))
        .andExpect(jsonPath("$[1].zip", is(13260))).andDo(print());

  }

  @Test
  @Order(2)
  void getPersonsById() throws Exception {
    // given

    when(personService.getPersonById(Mockito.anyLong()))
        .thenReturn(Optional.of(mockPersonWithId));

    mockMvc.perform(get("/person/{id}", 1)).andExpect(status().isOk())
        .andExpect(jsonPath("$").exists())
        .andExpect(jsonPath("$.length()", is(9)))
        .andExpect(jsonPath("$.idPerson", is(1)))
        .andExpect(jsonPath("$.firstName", is("Dorian")))
        .andExpect(jsonPath("$.lastName", is("Delaval")))
        .andExpect(jsonPath("$.address", is("26 av marechal foch")))
        .andExpect(jsonPath("$.city", is("Cassis")))
        .andExpect(jsonPath("$.zip", is(13260)))
        .andExpect(jsonPath("$.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$.email", is("delaval.htps@gmail.com")))
        .andDo(print());

  }

  @Test
  @Order(3)
  void getPersonsById_whenPersonNotFound_thenReturn404() throws Exception {
    // given

    when(personService.getPersonById(Mockito.anyLong()))
        .thenReturn(Optional.empty());

    MvcResult result = mockMvc.perform(get("/person/{id}", 1)).andExpect(status().isNotFound())
        .andDo(print()).andReturn();

    assertThat(result.getResolvedException()).isInstanceOf(PersonNotFoundException.class);
    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("Unable to found a person with id:1");

  }

  @Test
  @Order(4)
  void postPerson_WithValidInputAndAddressNotMapped_thenReturn201() throws Exception {

    // Given
    ObjectMapper mapper = mapperBuilder.build();

    when(personService.getPersonByNames(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(Optional.empty());

    // address of mockPersonWhitId  and mockPersonithoutId is "26 avenue marechal foch"
    //not mapped in dataTest.json by fireStation
    when(personService.savePerson(Mockito.any(Person.class))).thenReturn(mockPersonWithId);

    when(fireStationService.getFireStationsMappedToAddress(Mockito.anyString()))
        .thenReturn(new ArrayList());


    //When & then
    mockMvc.perform(post("/person")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockPersonWithoutId)))

        .andExpect(status().isCreated())
        .andExpect(redirectedUrlPattern("http://*/person/1"))
        .andExpect(jsonPath("$.length()", is(9)))
        .andExpect(jsonPath("$.idPerson", is(1)))
        .andExpect(jsonPath("$.firstName", is("Dorian")))
        .andExpect(jsonPath("$.lastName", is("Delaval")))
        .andExpect(jsonPath("$.address", is("26 av marechal foch")))
        .andExpect(jsonPath("$.city", is("Cassis")))
        .andExpect(jsonPath("$.zip", is(13260)))
        .andExpect(jsonPath("$.birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.email", is("delaval.htps@gmail.com"))).andDo(print());


    // verification du bon passage d'argument a personService.savePerson
    // only once to save person and not to update fireStations of Person
    ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);

    verify(personService, times(1)).savePerson(personCaptor.capture());
    assertThat(personCaptor.getValue().getIdPerson()).isNull();
    assertThat(personCaptor.getValue().getFirstName()).isEqualTo("Dorian");
    assertThat(personCaptor.getValue().getLastName()).isEqualTo("Delaval");
    assertThat(personCaptor.getValue().getAddress()).isEqualTo("26 av marechal foch");
    assertThat(personCaptor.getValue().getEmail()).isEqualTo("delaval.htps@gmail.com");
    assertThat(personCaptor.getValue().getCity()).isEqualTo("Cassis");
    assertThat(personCaptor.getValue().getZip()).isEqualTo(13260);
    assertThat(personCaptor.getValue().getBirthDate()).isEqualTo("1976-12-27");
    assertThat(personCaptor.getValue().getPhone()).isEqualTo("061-846-0160");



  }

  @Test
  @Order(5)
  void postPerson_WithAddressPersonMappedByFireStation_thenReturn201() throws Exception {

    //given
    when(personService.getPersonByNames(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(Optional.empty());

    // address of mockPersonWhitId  and mockPersonithoutId is "1509 Culver St"
    // mapped in dataTest.json by fireStation numberstation 3

    mockPersonWithoutId.setAddress("1509 Culver St");
    mockPersonWithoutId.setFireStations(mockSetFireStation);

    mockPersonWithId.setAddress("1509 Culver St");
    mockPersonWithId.setFireStations(mockSetFireStation);

    addresses1.add("1509 Culver St");
    mockFireStation1.setAddresses(addresses1);

    List<FireStation> fireStations = Arrays.asList(mockFireStation1);

    when(personService.savePerson(Mockito.any(Person.class))).thenReturn(mockPersonWithId);

    when(fireStationService.getFireStationsMappedToAddress(Mockito.anyString()))
        .thenReturn(fireStations);

    ObjectMapper mapper = mapperBuilder.build();

    //when & then
    mockMvc.perform(post("/person")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockPersonWithoutId)))

        .andExpect(status().isCreated())
        .andExpect(redirectedUrlPattern("http://*/person/1"))
        .andExpect(jsonPath("$.length()", is(9)))
        .andExpect(jsonPath("$.idPerson", notNullValue()))
        .andExpect(jsonPath("$.firstName", is("Dorian")))
        .andExpect(jsonPath("$.lastName", is("Delaval")))
        .andExpect(jsonPath("$.address", is("1509 Culver St")))
        .andExpect(jsonPath("$.city", is("Cassis")))
        .andExpect(jsonPath("$.zip", is(13260)))
        .andExpect(jsonPath("$.birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.email", is("delaval.htps@gmail.com"))).andDo(print());


    // verification du bon passage d'argument a personService.savePerson
    ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);

    verify(personService, times(2)).savePerson(personCaptor.capture());
    verify(fireStationService, times(1)).getFireStationsMappedToAddress(Mockito.anyString());
    List<Person> personsCaptured = personCaptor.getAllValues();
    assertThat(personsCaptured.get(0).getIdPerson()).isNull();
    assertThat(personsCaptured.get(0).getFirstName()).isEqualTo("Dorian");
    assertThat(personsCaptured.get(0).getLastName()).isEqualTo("Delaval");
    assertThat(personsCaptured.get(0).getAddress()).isEqualTo("1509 Culver St");
    assertThat(personsCaptured.get(0).getEmail()).isEqualTo("delaval.htps@gmail.com");
    assertThat(personsCaptured.get(0).getCity()).isEqualTo("Cassis");
    assertThat(personsCaptured.get(0).getZip()).isEqualTo(13260);
    assertThat(personsCaptured.get(0).getBirthDate()).isEqualTo("1976-12-27");
    assertThat(personsCaptured.get(0).getPhone()).isEqualTo("061-846-0160");

    assertThat(personsCaptured.get(1).getIdPerson()).isNotNull();
    assertThat(personsCaptured.get(1).getFireStations().size()).isEqualTo(1);
    Set<FireStation> fireStationsMapped = personsCaptured.get(1).getFireStations();
    assertThat(fireStationsMapped.contains(mockFireStation1));

  }

  @Test
  @Order(6)
  void postPerson_whenPersonAlreadyExist_thenReturn400() throws Exception {

    // given
    ObjectMapper mapper = mapperBuilder.build();

    when(personService.getPersonByNames(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(Optional.of(mockPerson1));

    //when & then
    MvcResult result = mockMvc.perform(post("/person")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockPerson1)))

        .andExpect(status().isBadRequest()).andDo(print()).andReturn();

    assertThat(result.getResolvedException())
        .isInstanceOf(PersonAlreadyExistedException.class);
    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("this Person with firstname:" + mockPerson1.getFirstName()
            + " and lastname:" + mockPerson1.getLastName()
            + " already exist ! Can't add an already existed Person!");

  }

  @ParameterizedTest
  @Order(7)
  @CsvSource({" , , Delaval, 12/27/1976, 26 av mar??chal Foch, Cassis, 13260,"
      + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, , 12/27/1976, 26 av mar??chal Foch, Cassis, 13260,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, , Cassis, 13260,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, 26 av mar??chal Foch, , 13260,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, 26 av mar??chal Foch, Cassis, -1,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, 26 av mar??chal Foch, Cassis, 100000,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, 26 av mar??chal Foch, Cassis, 13260,"
                  + " , delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, 26 av mar??chal Foch, Cassis, 13260,"
                  + " 061-846-0160, , , "})
  void postPerson_WithNoValidInput_thenReturn400(ArgumentsAccessor args)
      throws Exception {

    Person falsePerson = new Person(args.getLong(0), args.getString(1), args.getString(2),
                                    sdf.parse(args.getString(3)), args.getString(4),
                                    args.getString(5),
                                    args.getInteger(6), args.getString(7), args.getString(8),
                                    args.get(9, MedicalRecord.class),
                                    args.get(10, Set.class));

    ObjectMapper mapper = mapperBuilder.build();


    MvcResult result = mockMvc.perform(post("/person")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(falsePerson)))

        .andExpect(status().isBadRequest()).andReturn();

    verify(personService, never()).savePerson(Mockito.any(Person.class));

    assertThat(result.getResolvedException())
        .isInstanceOf(MethodArgumentNotValidException.class);

  }

  @Test
  @Order(8)
  void postPerson_whenIdPersonInBody_thenReturn400()
      throws JsonProcessingException, Exception {

    //given
    ObjectMapper mapper = mapperBuilder.build();
    //then
    MvcResult result = mockMvc.perform(post("/person")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockPersonWithId)))
        .andExpect(status().isBadRequest())
        .andDo(print())
        .andReturn();

    assertThat(result.getResolvedException()).isInstanceOf(PersonWithIdException.class);
    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("Don't need an id for Person to save it!");

  }

  @Test
  @Order(9)
  void putPerson_WithValidInputButSameAddress_thenReturn200() throws Exception {

    //given
    when(personService.getPersonById(Mockito.anyLong()))
        .thenReturn(Optional.of(mockPersonWithId));

    //just change the phone number for example. address is the same
    mockPersonWithoutId.setPhone("061-846-0260");
    mockPersonWithIdAndChanges.setPhone("601-846-0260");

    when(personService.savePerson(Mockito.any(Person.class)))
        .thenReturn(mockPersonWithIdAndChanges);

    ObjectMapper mapper = mapperBuilder.build();

    //when
    mockMvc.perform(put("/person/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockPersonWithoutId)))

        .andExpect(status().isOk()).andDo(print());

    // verification de la bonne utilisation de personService.save(person)

    ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);
    verify(personService, times(1)).savePerson(personCaptor.capture());
    assertThat(personCaptor.getValue().getIdPerson()).isNotNull();
    assertThat(personCaptor.getValue().getFirstName()).isEqualTo("Dorian");
    assertThat(personCaptor.getValue().getLastName()).isEqualTo("Delaval");
    assertThat(personCaptor.getValue().getAddress()).isEqualTo("26 av marechal foch");
    assertThat(personCaptor.getValue().getEmail()).isEqualTo("delaval.htps@gmail.com");
    assertThat(personCaptor.getValue().getCity()).isEqualTo("Cassis");
    assertThat(personCaptor.getValue().getZip()).isEqualTo(13260);
    assertThat(personCaptor.getValue().getBirthDate()).isEqualTo("1976-12-27");
    assertThat(personCaptor.getValue().getPhone()).isEqualTo("061-846-0260");

  }

  @Test
  @Order(10)
  void putPerson_WithChangedAddressMappedByFireStation_thenReturn200() throws Exception {

    //given
    //mockPersonWithId has adresss "26 av MarechalFoch" and mockFireStation1 mapped with it
    mockSetFireStation.add(mockFireStation1);
    mockPersonWithId.setFireStations(mockSetFireStation);

    //mockPersoWithouId has another address "29 15th St" mapped by mockFireStation2
    // but without SetFireStations updated (new hashSet)
    mockPersonWithoutId.setAddress("29 15th St");
    mockPersonWithoutId.setFireStations(new HashSet<>());

    mockPersonWithIdAndChanges.setAddress("29 15th St");
    mockPersonWithIdAndChanges.setFireStations(new HashSet());

    when(personService.getPersonById(Mockito.anyLong()))
        .thenReturn(Optional.of(mockPersonWithId));

    when(personService.savePerson(Mockito.any(Person.class)))
        .thenReturn(mockPersonWithIdAndChanges);

    List<FireStation> fireStations = Arrays.asList(mockFireStation2);

    when(fireStationService.getFireStationsMappedToAddress(Mockito.anyString()))
        .thenReturn(fireStations);

    ObjectMapper mapper = mapperBuilder.build();

    //When &then
    mockMvc.perform(put("/person/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockPersonWithoutId)))

        .andExpect(status().isOk())
        .andExpect(jsonPath("$.idPerson", notNullValue()))
        .andExpect(jsonPath("$.length()", is(9)))
        .andExpect(jsonPath("$.address", is("29 15th St")))
        .andExpect(jsonPath("$.birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$.city", is("Cassis")))
        .andExpect(jsonPath("$.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.firstName", is("Dorian")))
        .andExpect(jsonPath("$.lastName", is("Delaval")))
        .andExpect(jsonPath("$.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.zip", is(13260))).andDo(print());

    ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);
    verify(personService, times(2)).savePerson(personCaptor.capture());
    verify(fireStationService, times(1)).getFireStationsMappedToAddress(Mockito.anyString());

    List<Person> personsCaptured = personCaptor.getAllValues();
    assertThat(personsCaptured.get(0).getIdPerson()).isNotNull();
    assertThat(personsCaptured.get(0).getFirstName()).isEqualTo("Dorian");
    assertThat(personsCaptured.get(0).getLastName()).isEqualTo("Delaval");
    assertThat(personsCaptured.get(0).getAddress()).isEqualTo("29 15th St");
    assertThat(personsCaptured.get(0).getEmail()).isEqualTo("delaval.htps@gmail.com");
    assertThat(personsCaptured.get(0).getCity()).isEqualTo("Cassis");
    assertThat(personsCaptured.get(0).getZip()).isEqualTo(13260);
    assertThat(personsCaptured.get(0).getBirthDate()).isEqualTo("1976-12-27");
    assertThat(personsCaptured.get(0).getPhone()).isEqualTo("061-846-0160");

    assertThat(personsCaptured.get(1).getIdPerson()).isNotNull();
    assertThat(personsCaptured.get(1).getFireStations().size()).isEqualTo(1);
    Set<FireStation> fireStationsMapped = personsCaptured.get(1).getFireStations();
    assertThat(fireStationsMapped.contains(mockFireStation2));

  }

  @Test
  @Order(11)
  void putPerson_WithChangedAddressNotMappedByFireStation_thenReturn200() throws Exception {

    //given

    mockPersonWithId.setFireStations(mockSetFireStation);

    when(personService.getPersonById(Mockito.anyLong()))
        .thenReturn(Optional.of(mockPersonWithId));

    mockPersonWithoutId.setAddress("addressNotMapped");
    mockPersonWithIdAndChanges.setAddress("addressNotMapped");

    when(personService.savePerson(Mockito.any(Person.class)))
        .thenReturn(mockPersonWithIdAndChanges);

    when(fireStationService.getFireStationsMappedToAddress(Mockito.anyString()))
        .thenReturn(new ArrayList());

    ObjectMapper mapper = mapperBuilder.build();

    //when & then

    mockMvc.perform(put("/person/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockPersonWithoutId)))

        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(9)))
        .andExpect(jsonPath("$.idPerson", notNullValue()))
        .andExpect(jsonPath("$.address", is("addressNotMapped")))
        .andExpect(jsonPath("$.birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$.city", is("Cassis")))
        .andExpect(jsonPath("$.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.firstName", is("Dorian")))
        .andExpect(jsonPath("$.lastName", is("Delaval")))
        .andExpect(jsonPath("$.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.zip", is(13260))).andDo(print());

    ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);
    verify(personService, times(1)).savePerson(personCaptor.capture());
    assertThat(personCaptor.getValue().getFireStations()).isNullOrEmpty();

  }



  @ParameterizedTest
  @Order(12)
  @CsvSource({" , , Delaval, 12/27/1976, 26 av mar??chal Foch, Cassis, 13260,"
      + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, , 12/27/1976, 26 av mar??chal Foch, Cassis, 13260,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, , Cassis, 13260,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, 26 av mar??chal Foch, , 13260,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, 26 av mar??chal Foch, Cassis, -1,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, 26 av mar??chal Foch, Cassis, 100000,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, 26 av mar??chal Foch, Cassis, 13260,"
                  + " , delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, 26 av mar??chal Foch, Cassis, 13260,"
                  + " 061-846-0160, , , "})
  void putPerson_WithNoValidInput_thenReturn400(ArgumentsAccessor args)
      throws Exception {

    Person falsePerson = new Person(args.getLong(0), args.getString(1), args.getString(2),
                                    sdf.parse(args.getString(3)), args.getString(4),
                                    args.getString(5),
                                    args.getInteger(6), args.getString(7), args.getString(8),
                                    args.get(9, MedicalRecord.class),
                                    args.get(10, Set.class));

    ObjectMapper mapper = mapperBuilder.build();

    MvcResult result = mockMvc.perform(put("/person/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(falsePerson)))

        .andExpect(status().is(400)).andDo(print()).andReturn();

    // on verifie que personService n'a pas enregistr?? de person
    verify(personService, never()).savePerson(Mockito.any(Person.class));

    assertThat(result.getResolvedException())
        .isInstanceOf(MethodArgumentNotValidException.class);

  }

  @ParameterizedTest
  @Order(13)
  @CsvSource({", Emilie , Delaval, 12/27/1976, 26 av mar??chal Foch, Cassis, 13260,"
      + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian , Baudouin, 12/27/1976, 26 av mar??chal Foch, Cassis, 13260,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Emilie, Baudouin, 12/27/1976, 26 av mar??chal Foch, Cassis, 13260,"
                  + " 061-846-0160, delaval.htps@gmail.com, , "})

  void putPerson_whenChangeNames_thenReturn400(ArgumentsAccessor args)
      throws JsonProcessingException, Exception {

    when(personService.getPersonById(Mockito.anyLong())).thenReturn(Optional.of(mockPerson1));

    ObjectMapper mapper = mapperBuilder.build();

    Person mockPerson1WithChangedNames = new Person(args.getLong(0), args.getString(1),
                                                    args.getString(2),
                                                    sdf.parse(args.getString(3)),
                                                    args.getString(4), args.getString(5),
                                                    args.getInteger(6), args.getString(7),
                                                    args.getString(8),
                                                    args.get(9, MedicalRecord.class),
                                                    args.get(10, Set.class));

    MvcResult result = mockMvc.perform(put("/person/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockPerson1WithChangedNames)))

        .andExpect(status().isBadRequest()).andDo(print()).andReturn();

    // on verifie que personService n'a pas enregistr?? de person
    verify(personService, never()).savePerson(Mockito.any(Person.class));

    assertThat(result.getResolvedException())
        .isInstanceOf(PersonChangedNamesException.class);

    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("When updating a person with id:1 you can't change names");

  }


  @Test
  @Order(14)
  void putPerson_withNotFoundPerson_thenReturn404() throws Exception {

    ObjectMapper mapper = mapperBuilder.build();

    // id de la person n'existe pas et on retourne null ou empty for optional
    when(personService.getPersonById(Mockito.anyLong())).thenReturn(Optional.empty());

    MvcResult result = mockMvc.perform(put("/person/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockPerson1)))

        .andExpect(status().isNotFound()).andDo(print()).andReturn();

    // on verifie que personService n'a pas enregistr?? de person
    verify(personService, never()).savePerson(Mockito.any(Person.class));

    assertThat(result.getResolvedException())
        .isInstanceOf(PersonNotFoundException.class);

    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("Person to update with id: 1 was not found");

  }

  @Test
  @Order(15)
  void putPerson_whenIdPersonInBody_thenReturn400()
      throws JsonProcessingException, Exception {

    //given
    ObjectMapper mapper = mapperBuilder.build();
    //then
    MvcResult result = mockMvc.perform(put("/person/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockPersonWithId)))
        .andExpect(status().isBadRequest())
        .andDo(print())
        .andReturn();

    assertThat(result.getResolvedException()).isInstanceOf(PersonWithIdException.class);
    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("Don't need an id for Person to save it!");

  }

  @Test
  @Order(16)
  void deletePerson_withValidInputCoupleNames_thenReturn200()
      throws Exception {

    when(personService.getPersonByNames(Mockito.anyString(),
        Mockito.anyString())).thenReturn(Optional.of(mockPersonWithId));

    mockMvc.perform(delete("/person/{lastName}/{firstName}", "Dorian", "Delaval"))
        .andExpect(status().isOk());

    ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);

    verify(personService, times(1)).deletePerson(personCaptor.capture());
    assertThat(personCaptor.getValue().getIdPerson()).isNotNull();
    assertThat(personCaptor.getValue().getFirstName()).isEqualTo("Dorian");
    assertThat(personCaptor.getValue().getLastName()).isEqualTo("Delaval");
    assertThat(personCaptor.getValue().getAddress()).isEqualTo("26 av marechal foch");
    assertThat(personCaptor.getValue().getEmail()).isEqualTo("delaval.htps@gmail.com");
    assertThat(personCaptor.getValue().getCity()).isEqualTo("Cassis");
    assertThat(personCaptor.getValue().getZip()).isEqualTo(13260);
    assertThat(personCaptor.getValue().getBirthDate()).isEqualTo("1976-12-27");
    assertThat(personCaptor.getValue().getPhone()).isEqualTo("061-846-0160");

  }

  @Test
  @Order(17)
  void deletePerson_withNoValidInputCoupleNames_thenReturn404()
      throws Exception {

    when(personService.getPersonByNames(Mockito.anyString(),
        Mockito.anyString())).thenReturn(Optional.empty());

    MvcResult result =
        mockMvc.perform(delete("/person/{lastName}/{firstName}", "Delaval", "Dorian"))
            .andExpect(status().isNotFound()).andDo(print()).andReturn();

    verify(personService, never()).deletePerson(Mockito.any(Person.class));

    assertThat(result.getResolvedException())
        .isInstanceOf(PersonNotFoundException.class);

    assertThat(result.getResolvedException().getMessage())
        .isEqualTo(
            "Deleting Person with lastName: Delaval and FirstName: Dorian was not Found");

  }
}
