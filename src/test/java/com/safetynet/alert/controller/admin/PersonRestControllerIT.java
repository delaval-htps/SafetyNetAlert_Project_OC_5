package com.safetynet.alert.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alert.CommandLineRunnerTaskExcecutor;
import com.safetynet.alert.database.LoadDataStrategyFactory;
import com.safetynet.alert.database.StrategyName;
import com.safetynet.alert.exceptions.person.PersonAlreadyExistedException;
import com.safetynet.alert.exceptions.person.PersonChangedNamesException;
import com.safetynet.alert.exceptions.person.PersonNotFoundException;
import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.model.MedicalRecord;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.FireStationService;
import com.safetynet.alert.service.PersonService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Log4j2
class PersonRestControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private PersonService personService;

  @Autowired
  FireStationService fireStationService;

  @Autowired
  private Jackson2ObjectMapperBuilder mapperBuilder;

  @MockBean
  private CommandLineRunnerTaskExcecutor clrte;

  @Autowired
  private LoadDataStrategyFactory loadDataStrategyFactory;

  private Person personTest;
  private SimpleDateFormat sdf;

  @BeforeEach
  void setup() throws ParseException {

    sdf = new SimpleDateFormat("MM/dd/yyyy");

    loadDataStrategyFactory.findStrategy(StrategyName.StrategyTest)
        .loadDatabaseFromSource();

    personTest = new Person(null, "Dorian", "Delaval", sdf.parse("12/27/1976"),
                            "26 av maréchal foch", "Cassis", 13260,
                            "061-846-0160", "delaval.htps@gmail.com",
                            null, null);

  }

  @Test
  @Order(1)
  void getPerson() throws Exception {

    // assume that we check one line of jsonPAth : if it's
    // correct then it is for the others
    mockMvc.perform(get("/person"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").exists())
        .andExpect(jsonPath("$.length()", is(2)))
        .andExpect(jsonPath("$[0].idPerson", is(1)))
        .andExpect(jsonPath("$[0].address", is("1509 Culver St")))
        .andExpect(jsonPath("$[0].birthDate", is("03/06/1984")))
        .andExpect(jsonPath("$[0].city", is("Culver")))
        .andExpect(jsonPath("$[0].email", is("jaboyd@email.com")))
        .andExpect(jsonPath("$[0].firstName", is("John")))
        .andExpect(jsonPath("$[0].lastName", is("Boyd")))
        .andExpect(jsonPath("$[0].phone", is("841-874-6512")))
        .andExpect(jsonPath("$[0].zip", is(97451)))
        .andExpect(jsonPath("$[1].idPerson", is(2)))
        .andExpect(jsonPath("$[1].address", is("29 15th St")))
        .andExpect(jsonPath("$[1].city", is("Culver")))
        .andExpect(jsonPath("$[1].email", is("drk@email.com")))
        .andExpect(jsonPath("$[1].firstName", is("Jonanathan")))
        .andExpect(jsonPath("$[1].lastName", is("Marrack")))
        .andExpect(jsonPath("$[1].phone", is("841-874-6513")))
        .andExpect(jsonPath("$[1].zip", is(97451))).andDo(print());

  }

  @Test
  @Order(2)
  void getPersonById() throws Exception {

    mockMvc.perform(get("/person/{id}", 1))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").exists())
        .andExpect(jsonPath("$.length()", is(9)))
        .andExpect(jsonPath("$.idPerson", is(1)))
        .andExpect(jsonPath("$.address", is("1509 Culver St")))
        .andExpect(jsonPath("$.birthDate", is("03/06/1984")))
        .andExpect(jsonPath("$.city", is("Culver")))
        .andExpect(jsonPath("$.email", is("jaboyd@email.com")))
        .andExpect(jsonPath("$.firstName", is("John")))
        .andExpect(jsonPath("$.lastName", is("Boyd")))
        .andExpect(jsonPath("$.phone", is("841-874-6512")))
        .andExpect(jsonPath("$.zip", is(97451))).andDo(print());

  }

  @Test
  @Order(3)
  void getPersonsById_whenPersonNotFound_thenReturn404() throws Exception {
    // given

    MvcResult result = mockMvc.perform(get("/person/{id}", 3)).andExpect(status().isNotFound())
        .andDo(print()).andReturn();

    assertThat(result.getResolvedException()).isInstanceOf(PersonNotFoundException.class);
    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("Unable to found a person with id:3");

  }

  @Test
  @Order(4)
  void postPerson_withValidInputAndAddressNotMapped_thenReturn201() throws Exception {

    // Given
    ObjectMapper mapper = mapperBuilder.build();

    // When and Then
    mockMvc.perform(post("/person")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(personTest)))

        .andExpect(status().isCreated())
        .andExpect(redirectedUrl(ServletUriComponentsBuilder.fromCurrentRequest()
            .build().toString() + "/person/3"))
        .andExpect(jsonPath("$").exists())
        .andExpect(jsonPath("$.length()", is(9)))
        .andExpect(jsonPath("$.idPerson", is(3)))
        .andExpect(jsonPath("$.address", is("26 av maréchal foch")))
        .andExpect(jsonPath("$.birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$.city", is("Cassis")))
        .andExpect(jsonPath("$.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.firstName", is("Dorian")))
        .andExpect(jsonPath("$.lastName", is("Delaval")))
        .andExpect(jsonPath("$.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.zip", is(13260))).andDo(print());

  }

  @Test
  @Order(5)
  void postPerson_WithAddressPersonMappedByFireStation_thenReturn201() throws Exception {

    //given
    ObjectMapper mapper = mapperBuilder.build();
    personTest.setAddress("29 15th St");

    //when & then

    mockMvc.perform(post("/person")
        .accept(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(personTest))
        .contentType(MediaType.APPLICATION_JSON))

        .andExpect(status().isCreated())
        .andExpect(redirectedUrlPattern("http://*/person/3"))
        .andExpect(jsonPath("$.idPerson", is(3)))
        .andExpect(jsonPath("$.length()", is(9)))
        .andExpect(jsonPath("$.idPerson", is(3)))
        .andExpect(jsonPath("$.address", is("29 15th St")))
        .andExpect(jsonPath("$.birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$.city", is("Cassis")))
        .andExpect(jsonPath("$.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.firstName", is("Dorian")))
        .andExpect(jsonPath("$.lastName", is("Delaval")))
        .andExpect(jsonPath("$.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.zip", is(13260))).andDo(print());

    //check if Person with this address was correctly mapped with fireStation 2L
    assertThat(personService.getPersonById(3L).get().getFireStation().getIdFireStation())
        .isEqualTo(2L);

  }

  @Test
  @Order(6)
  void postPerson_whenPersonAlreadyExist_thenReturn400() throws Exception {

    // given
    ObjectMapper mapper = mapperBuilder.build();

    Optional<Person> existedPerson = personService.getPersonById(1L);

    //when & then
    MvcResult result = mockMvc.perform(post("/person")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(existedPerson.get())))

        .andExpect(status().isBadRequest()).andDo(print()).andReturn();

    assertThat(result.getResolvedException())
        .isInstanceOf(PersonAlreadyExistedException.class);

    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("this Person with firstname:" + existedPerson.get().getFirstName()
            + " and lastname:" + existedPerson.get().getLastName()
            + " already exist ! Can't add an already existed Person!");

  }

  @ParameterizedTest
  @Order(7)
  @CsvSource({" , , Delaval, 12/27/1976, 26 av maréchal Foch, Cassis, 13260,"
      + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, , 12/27/1976, 26 av maréchal Foch, Cassis, 13260,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, , Cassis, 13260,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, 26 av maréchal Foch, , 13260,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, 26 av maréchal Foch, Cassis, -1,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, 26 av maréchal Foch, Cassis, 100000,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, 26 av maréchal Foch, Cassis, 13260,"
                  + " , delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, 26 av maréchal Foch, Cassis, 13260,"
                  + " 061-846-0160, , , "})
  void postPerson_WithNoValidInput_thenReturn400(ArgumentsAccessor args)
      throws Exception {

    Person falsePerson =
        new Person(args.getLong(0), args.getString(1), args.getString(2),
                   sdf.parse(args.getString(3)), args.getString(4), args.getString(5),
                   args.getInteger(6), args.getString(7), args.getString(8),
                   args.get(9, MedicalRecord.class), args.get(10, FireStation.class));

    ObjectMapper mapper = mapperBuilder.build();

    MvcResult result = mockMvc.perform(post("/person")
        .accept(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(falsePerson))
        .contentType(MediaType.APPLICATION_JSON))

        .andExpect(status().isBadRequest()).andReturn();

    assertThat(result.getResolvedException())
        .isInstanceOf(MethodArgumentNotValidException.class);

  }

  @Test
  @Order(8)
  void putPerson_withValidInputButSameAddress_thenReturn200() throws Exception {

    // Given
    personTest.setLastName("Boyd");
    personTest.setFirstName("John");
    personTest.setAddress("1509 Culver St");
    ObjectMapper mapper = mapperBuilder.build();

    // when & then
    mockMvc.perform(put("/person/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(personTest)))

        .andExpect(status().isOk()).andExpect(jsonPath("$.length()", is(9)))
        .andExpect(jsonPath("$.firstName", is("John")))
        .andExpect(jsonPath("$.lastName", is("Boyd")))
        .andExpect(jsonPath("$.address", is("1509 Culver St")))
        .andExpect(jsonPath("$.city", is("Cassis")))
        .andExpect(jsonPath("$.birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$.zip", is(13260)))
        .andExpect(jsonPath("$.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.email", is("delaval.htps@gmail.com")))
        .andDo(print());

  }

  @Test
  @Order(9)
  void putPerson_WithChangedAddressMappedByFireStation_thenReturn200() throws Exception {

    //given

    personTest.setAddress("29 15th St");
    personTest.setLastName("Boyd");
    personTest.setFirstName("John");
    ObjectMapper mapper = mapperBuilder.build();
    //when & then

    mockMvc.perform(put("/person/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(personTest)))

        .andExpect(status().isOk())
        .andExpect(jsonPath("$.idPerson", is(1)))
        .andExpect(jsonPath("$.length()", is(9)))
        .andExpect(jsonPath("$.idPerson", is(1)))
        .andExpect(jsonPath("$.address", is("29 15th St")))
        .andExpect(jsonPath("$.birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$.city", is("Cassis")))
        .andExpect(jsonPath("$.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.firstName", is("John")))
        .andExpect(jsonPath("$.lastName", is("Boyd")))
        .andExpect(jsonPath("$.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.zip", is(13260))).andDo(print());

    //check if Person with this address was correctly mapped with fireStation 2L
    assertThat(personService.getPersonById(1L).get().getFireStation().getIdFireStation())
        .isEqualTo(2L);

  }

  @Test
  @Order(10)
  void putPerson_WithChangedAddressNotMappedByFireStation_thenReturn200() throws Exception {

    //given

    personTest.setLastName("Boyd");
    personTest.setFirstName("John");
    personTest.setAddress("addressNotMapped");
    ObjectMapper mapper = mapperBuilder.build();

    //when & then

    mockMvc.perform(put("/person/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(personTest)))

        .andExpect(status().isOk())
        .andExpect(jsonPath("$.idPerson", is(1)))
        .andExpect(jsonPath("$.length()", is(9)))
        .andExpect(jsonPath("$.idPerson", is(1)))
        .andExpect(jsonPath("$.address", is("addressNotMapped")))
        .andExpect(jsonPath("$.birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$.city", is("Cassis")))
        .andExpect(jsonPath("$.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.firstName", is("John")))
        .andExpect(jsonPath("$.lastName", is("Boyd")))
        .andExpect(jsonPath("$.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.zip", is(13260))).andDo(print());

    //check if Person with this address was correctly mapped with fireStation 2L
    assertThat(personService.getPersonById(1L).get().getFireStation()).isNull();

  }

  @ParameterizedTest
  @Order(11)
  @CsvSource({" , , Delaval, 12/27/1976, 26 av maréchal Foch, Cassis, 13260,"
      + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, , 12/27/1976, 26 av maréchal Foch, Cassis, 13260,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, , Cassis, 13260,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, 26 av maréchal Foch, , 13260,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, 26 av maréchal Foch, Cassis, -1,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, 26 av maréchal Foch, Cassis, 100000,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, 26 av maréchal Foch, Cassis, 13260,"
                  + " , delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 12/27/1976, 26 av maréchal Foch, Cassis, 13260,"
                  + " 061-846-0160, , , "})
  void putPerson_WithNoValidInput_thenReturn400(ArgumentsAccessor args)
      throws Exception {

    Person falsePerson = new Person(args.getLong(0), args.getString(1),
                                    args.getString(2), sdf.parse(args.getString(3)),
                                    args.getString(4), args.getString(5),
                                    args.getInteger(6), args.getString(7),
                                    args.getString(8), args.get(9, MedicalRecord.class),
                                    args.get(10, FireStation.class));

    ObjectMapper mapper = mapperBuilder.build();

    MvcResult result = mockMvc.perform(put("/person/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(falsePerson)))

        .andExpect(status().is(400)).andDo(print()).andReturn();

    assertThat(result.getResolvedException())
        .isInstanceOf(MethodArgumentNotValidException.class);

  }

  @ParameterizedTest
  @CsvSource({"Dorian,Boyd",
              "John,Delaval",
              "Dorian,Delaval"})
  @Order(12)
  void putPerson_whenChangeNames_thenReturn400(ArgumentsAccessor args)
      throws JsonProcessingException, Exception {

    // Given, we change the names of persontTest to put with id: 1
    personTest.setFirstName(args.getString(0));
    personTest.setLastName(args.getString(1));

    ObjectMapper mapper = mapperBuilder.build();

    // when & then
    MvcResult result = mockMvc.perform(put("/person/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(personTest)))

        .andExpect(status().isBadRequest()).andDo(print()).andReturn();
    assertThat(result.getResolvedException())
        .isInstanceOf(PersonChangedNamesException.class);

    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("When updating a person with id:1 you can't change names");

  }

  @Test
  @Order(13)
  void putPerson_withNotFoundPerson_thenReturn404() throws Exception {

    ObjectMapper mapper = mapperBuilder.build();

    MvcResult result = mockMvc.perform(put("/person/{id}", 3)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(personTest)))

        .andExpect(status().isNotFound()).andDo(print()).andReturn();

    assertThat(result.getResolvedException())
        .isInstanceOf(PersonNotFoundException.class);

    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("Person to update with id: 3 was not found");

  }



  @Test
  @Order(14)
  void deletePerson_withValidInputCoupleNames_thenReturn200()
      throws Exception {

    mockMvc.perform(delete("/person/{lastName}/{firstName}", "Boyd", "John"))
        .andExpect(status().isOk());

  }

  @Test
  @Order(15)
  void deletePerson_withNoValidInputCoupleNames_thenReturn404()
      throws Exception {

    MvcResult result =
        mockMvc.perform(delete("/person/{lastName}/{firstName}", "Boyd", "Dorian"))
            .andExpect(status().isNotFound()).andDo(print()).andReturn();

    assertThat(result.getResolvedException())
        .isInstanceOf(PersonNotFoundException.class);

    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("Deleting Person with lastName: Boyd and FirstName: Dorian was not Found");

  }

}


