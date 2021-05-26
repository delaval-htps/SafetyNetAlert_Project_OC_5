package com.safetynet.alert.controller.admin;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alert.CommandLineRunnerTaskExcecutor;
import com.safetynet.alert.database.LoadDataStrategyFactory;
import com.safetynet.alert.database.StrategyName;
import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.model.MedicalRecord;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.PersonService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@SpringBootTest
@ActiveProfiles("test")
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
  private Jackson2ObjectMapperBuilder mapperBuilder;

  @MockBean
  private CommandLineRunnerTaskExcecutor clrte;

  @Autowired
  private LoadDataStrategyFactory loadDataStrategyFactory;

  private Person personTest;

  @BeforeEach
  void setup() {

    loadDataStrategyFactory.findStrategy(StrategyName.StrategyTest)
        .loadDatabaseFromSource();
    personTest =
        new Person(null,
            "John",
            "Boyd",
            "27/12/76",
            "26 av maréchal foch",
            "Cassis",
            13260,
            "061-846-0160",
            "delaval.htps@gmail.com",
            null,
            null);

  }

  @Test
  @Order(1)
  void getPerson() throws Exception {

    // assume that we check one line of jsonPAth : if it's
    // correct then it is for the others
    mockMvc.perform(get("/person")).andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").exists())
        .andExpect(jsonPath("$.length()", is(1)))
        .andExpect(jsonPath("$[0].idPerson", is(1)))
        .andExpect(jsonPath("$[0].address", is("1509 Culver St")))
        .andExpect(jsonPath("$[0].birthDate", is("03/06/1984")))
        .andExpect(jsonPath("$[0].city", is("Culver")))
        .andExpect(jsonPath("$[0].email", is("jaboyd@email.com")))
        .andExpect(jsonPath("$[0].firstName", is("John")))
        .andExpect(jsonPath("$[0].lastName", is("Boyd")))
        .andExpect(jsonPath("$[0].phone", is("841-874-6512")))
        .andExpect(jsonPath("$[0].zip", is(97451))).andDo(print());

  }

  @Test
  @Order(2)
  void getPersonById() throws Exception {

    mockMvc.perform(get("/person/{id}", 1)).andExpect(status().isOk())
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
  void testGetPersonsById_whenPersonNotFound_thenReturn404() throws Exception {
    // given

    mockMvc.perform(get("/person/{id}", 2)).andExpect(status().isNotFound())
        .andDo(print());

  }

  @Test
  @Order(4)
  void postPerson_withValidInput_thenReturn201() throws Exception {

    // Given
    ObjectMapper mapper = mapperBuilder.build();

    // When and Then
    mockMvc.perform(post("/person").accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(personTest)))
        .andExpect(status().isCreated())
        .andExpect(redirectedUrl(ServletUriComponentsBuilder.fromCurrentRequest()
            .build()
            .toString()
            + "/person/2"))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").exists())
        .andExpect(jsonPath("$.length()", is(9)))
        .andExpect(jsonPath("$.idPerson", is(2)))
        .andExpect(jsonPath("$.address", is("26 av maréchal foch")))
        .andExpect(jsonPath("$.birthDate", is("27/12/76")))
        .andExpect(jsonPath("$.city", is("Cassis")))
        .andExpect(jsonPath("$.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.firstName", is("John")))
        .andExpect(jsonPath("$.lastName", is("Boyd")))
        .andExpect(jsonPath("$.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.zip", is(13260))).andDo(print());

  }

  @ParameterizedTest
  @Order(5)
  @CsvSource({" , , Delaval, 27/12/1976, 26 av maréchal Foch, Cassis, 13260,"
      + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, , 27/12/1976, 26 av maréchal Foch, Cassis, 13260,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 27/12/1976, , Cassis, 13260,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 27/12/1976, 26 av maréchal Foch, , 13260,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 27/12/1976, 26 av maréchal Foch, Cassis, -1,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 27/12/1976, 26 av maréchal Foch, Cassis, 100000,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 27/12/1976, 26 av maréchal Foch, Cassis, 13260,"
                  + " , delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 27/12/1976, 26 av maréchal Foch, Cassis, 13260, 061-846-0160, , , "})
  void testPostPerson_WithNoValidInput_thenReturn400(ArgumentsAccessor args)
      throws Exception {

    Person falsePerson =
        new Person(args.getLong(0),
            args.getString(1),
            args.getString(2),
            args.getString(3),
            args.getString(4),
            args.getString(5),
            args.getInteger(6),
            args.getString(7),
            args.getString(8),
            args.get(9, MedicalRecord.class),
            args.get(10, FireStation.class));

    ObjectMapper mapper = mapperBuilder.build();

    mockMvc.perform(post("/person").accept(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(falsePerson))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

  }

  @Test
  @Order(6)
  void putPerson_withValidInput_thenReturn200() throws Exception {

    // Given
    ObjectMapper mapper = mapperBuilder.build();

    // when & then
    mockMvc.perform(put("/person/{id}",
        1).accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(personTest)))
        .andExpect(status().isOk()).andExpect(jsonPath("$.length()", is(9)))
        .andExpect(jsonPath("$.firstName", is("John")))
        .andExpect(jsonPath("$.lastName", is("Boyd")))
        .andExpect(jsonPath("$.address", is("26 av maréchal foch")))
        .andExpect(jsonPath("$.city", is("Cassis")))
        .andExpect(jsonPath("$.birthDate", is("27/12/76")))
        .andExpect(jsonPath("$.zip", is(13260)))
        .andExpect(jsonPath("$.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.email", is("delaval.htps@gmail.com")))
        .andDo(print());

  }

  @ParameterizedTest
  @Order(7)
  @CsvSource({" , , Delaval, 27/12/1976, 26 av maréchal Foch, Cassis, 13260,"
      + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, , 27/12/1976, 26 av maréchal Foch, Cassis, 13260,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 27/12/1976, , Cassis, 13260,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 27/12/1976, 26 av maréchal Foch, , 13260,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 27/12/1976, 26 av maréchal Foch, Cassis, -1,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 27/12/1976, 26 av maréchal Foch, Cassis, 100000,"
                  + " 061-846-0160, delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 27/12/1976, 26 av maréchal Foch, Cassis, 13260,"
                  + " , delaval.htps@gmail.com, , ",
              " , Dorian, Delaval, 27/12/1976, 26 av maréchal Foch, Cassis, 13260, 061-846-0160, , , "})
  void testPutPerson_WithNoValidInput_thenReturn400(ArgumentsAccessor args)
      throws Exception {

    Person falsePerson =
        new Person(args.getLong(0),
            args.getString(1),
            args.getString(2),
            args.getString(3),
            args.getString(4),
            args.getString(5),
            args.getInteger(6),
            args.getString(7),
            args.getString(8),
            args.get(9, MedicalRecord.class),
            args.get(10, FireStation.class));

    ObjectMapper mapper = mapperBuilder.build();

    mockMvc.perform(put("/person/{id}",
        1).accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(falsePerson)))
        .andExpect(status().is(400)).andDo(print());

  }

  @Test
  @Order(8)
  void testPutPerson_whenChangeNames_thenReturn400()
      throws JsonProcessingException, Exception {

    // Given, we change the names of persontTest to put with id: 1
    personTest.setFirstName("Dorian");
    personTest.setLastName("Delaval");

    ObjectMapper mapper = mapperBuilder.build();

    // when & then
    mockMvc.perform(put("/person/{id}",
        1).accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(personTest)))
        .andExpect(status().isBadRequest()).andDo(print());

  }

  @Test
  @Order(9)
  void testPutPerson_withNotFoundPerson_thenReturn404() throws Exception {

    ObjectMapper mapper = mapperBuilder.build();

    mockMvc.perform(put("/person/{id}",
        2).accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(personTest)))
        .andExpect(status().isNotFound()).andDo(print());

  }

  @Test
  @Order(10)
  void testDeletePerson_withValidInputCoupleNames_thenReturn200()
      throws Exception {

    mockMvc.perform(delete("/person/{lastName}/{firstName}", "Boyd", "John"))
        .andExpect(status().isOk());

  }

  @Test
  @Order(11)
  void testDeletePerson_withNoValidInputCoupleNames_thenReturn404()
      throws Exception {

    mockMvc.perform(delete("/person/{lastName}/{firstName}", "Boyd", "Dorian"))
        .andExpect(status().isNotFound()).andDo(print());

  }



}


