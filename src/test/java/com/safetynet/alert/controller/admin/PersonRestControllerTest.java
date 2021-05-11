package com.safetynet.alert.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
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
import com.safetynet.alert.database.LoadDatabaseService;
import com.safetynet.alert.model.MedicalRecord;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.PersonService;
import java.util.Arrays;
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

@TestMethodOrder(OrderAnnotation.class)
@WebMvcTest(controllers = PersonRestController.class)
class PersonRestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private Jackson2ObjectMapperBuilder mapperBuilder;

  @MockBean
  private PersonService personService;

  // need to mock LoadDatabaseService
  @MockBean
  private LoadDatabaseService lds;

  private static Person mockPerson1;
  private static Person mockPerson2;
  private static Person mockPersonWithId;
  private static Person mockPersonWithoutId;

  @BeforeEach
  void setUpBeforeClass() throws Exception {

    mockPerson1 =
        new Person(null, "Dorian", "Delaval", "27/12/1976",
                   "26 av marechal foch", "Cassis", 13260, "061-846-0160",
                   "delaval.htps@gmail.com", null, null);
    mockPerson2 = new Person(null, "Bernard", "Delaval", "28/12/1976",
                             "8 rue jean jaures", "Bruay Sur Escaut", 59860,
                             "061-846-0260", "delaval.b@email.com", null, null);
    mockPersonWithId =
        new Person(1L, "Dorian", "Delaval", "27/12/1976", "26 av marechal foch",
                   "Cassis", 13260, "061-846-0160", "delaval.htps@gmail.com",
                   null, null);
    mockPersonWithoutId =
        new Person(null, "Dorian", "Delaval", "27/12/1976",
                   "26 av marechal foch", "Cassis", 13260, "061-846-0160",
                   "delaval.htps@gmail.com", null, null);
  }

  @Test
  @Order(1)
  void testGetPersons() throws Exception {
    // given

    List<Person> persons = Arrays.asList(mockPerson1, mockPerson2);

    when(personService.getPersons()).thenReturn(persons);

    mockMvc.perform(get("/person")).andExpect(status().isOk()).andDo(print());
  }

  @Test
  @Order(2)
  void testGetPersonsById() throws Exception {
    // given

    when(personService.getPersonById(Mockito.anyLong())).thenReturn(Optional.of(mockPersonWithId));

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
           .andExpect(jsonPath("$.email", is("delaval.htps@gmail.com")))
           .andDo(print());
  }

  @Test
  @Order(3)
  void testGetPersonsById_whenPersonNotFound_thenReturn404() throws Exception {
    // given

    when(personService.getPersonById(Mockito.anyLong())).thenReturn(Optional.empty());

    mockMvc.perform(get("/person/{id}", 1)).andExpect(status().isNotFound())
           .andDo(print());
  }

  @Test
  @Order(2)
  void testPostPerson_WithValidInput_thenReturn201() throws Exception {

    ObjectMapper mapper = mapperBuilder.build();

    when(personService.savePerson(Mockito.any(Person.class))).thenReturn(mockPersonWithId);

    mockMvc.perform(post("/person").accept(MediaType.APPLICATION_JSON)
                                   .content(mapper.writeValueAsString(mockPersonWithoutId))
                                   .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isCreated())
           .andExpect(redirectedUrlPattern("http://*/person/1"))
           .andExpect(jsonPath("$.idPerson", is(1))).andDo(print());


    // verification du bon passage d'argument a personService.savePerson
    ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);

    verify(personService, times(1)).savePerson(personCaptor.capture());
    assertThat(personCaptor.getValue().getIdPerson()).isNull();
    assertThat(personCaptor.getValue().getFirstName()).isEqualTo("Dorian");
    assertThat(personCaptor.getValue().getLastName()).isEqualTo("Delaval");
    assertThat(personCaptor.getValue()
                           .getAddress()).isEqualTo("26 av marechal foch");
    assertThat(personCaptor.getValue()
                           .getEmail()).isEqualTo("delaval.htps@gmail.com");
    assertThat(personCaptor.getValue().getCity()).isEqualTo("Cassis");
    assertThat(personCaptor.getValue().getZip()).isEqualTo(13260);
    assertThat(personCaptor.getValue().getBirthDate()).isEqualTo("27/12/1976");
    assertThat(personCaptor.getValue().getPhone()).isEqualTo("061-846-0160");
  }

  // PersonService.savePerson(person) ne retourne jamais un null donc pas besoin
  // de faire ce test !!! de plus le retour 204 sert a dire que la request est
  // passée mais qu'il n'ya pas de corps de réponse. donc test ci desous faux!

  // @Test
  // @Order(3)
  // void testPostPerson_WhenSavePersonReturnNull_thenReturn204()
  // throws Exception {
  //
  // ObjectMapper mapper = mapperBuilder.build();
  //
  // when(personService.savePerson(Mockito.any(Person.class))).thenReturn(null);
  //
  // mockMvc.perform(post("/person").accept(MediaType.APPLICATION_JSON)
  // .content(mapper.writeValueAsString(mockPersonWithoutId))
  // .contentType(MediaType.APPLICATION_JSON))
  // .andExpect(status().isNoContent()).andDo(print());
  // }

  @ParameterizedTest
  @Order(3)
  @CsvSource(
    {" , , Delaval, 27/12/1976, 26 av maréchal Foch, Cassis, 13260,"
        + " 061-846-0160, delaval.htps@gmail.com, , ",
     " , Dorian, , 27/12/1976, 26 av maréchal Foch, Cassis, 13260,"
         + " 061-846-0160, delaval.htps@gmail.com, , ",
     // don't use with birthdate null because it's not a fields with @notnull
     // else the application doesn't work because data.json in person doesn't
     // have field birthdate
     // " , Dorian, Delaval, , 26 av maréchal Foch, Cassis, 13260,"
     // + " 061-846-0160, delaval.htps@gmail.com, , ",
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
     " , Dorian, Delaval, 27/12/1976, 26 av maréchal Foch, Cassis, 13260, 061-846-0160, , , "}
  )
  void testPostPerson_WithNoValidInput_thenReturn400(ArgumentsAccessor args)
      throws Exception {
    Person falsePerson =
        new Person(args.getLong(0), args.getString(1), args.getString(2),
                   args.getString(3), args.getString(4), args.getString(5),
                   args.getInteger(6), args.getString(7), args.getString(8),
                   args.get(9, MedicalRecord.class), args.get(10, Set.class));

    ObjectMapper mapper = mapperBuilder.build();


    mockMvc.perform(post("/person").accept(MediaType.APPLICATION_JSON)
                                   .content(mapper.writeValueAsString(falsePerson))
                                   .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());

    verify(personService, never()).savePerson(Mockito.any(Person.class));
  }

  @Test
  @Order(4)
  void testPutPerson_WithValidInput_thenReturn200() throws Exception {

    ObjectMapper mapper = mapperBuilder.build();


    when(personService.getPersonById(Mockito.anyLong())).thenReturn(Optional.of(mockPersonWithId));

    // modifie pour le test les champs Firstname et lastname et l'ID de
    // mockPerson2 pour qu'il correspondent a ceux de mockPerson1

    mockPerson2.setIdPerson(1L);
    mockPerson2.setFirstName(mockPersonWithId.getFirstName());
    mockPerson2.setLastName(mockPersonWithId.getLastName());

    mockMvc.perform(put("/person/{id}",
                        1).accept(MediaType.APPLICATION_JSON)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(mapper.writeValueAsString(mockPerson2)))
           .andExpect(status().isOk()).andDo(print());

    // verification de la bonne utilisation de personService.save(person)

    ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);
    verify(personService, times(1)).savePerson(personCaptor.capture());
    assertThat(personCaptor.getValue().getIdPerson()).isEqualTo(1L);
    assertThat(personCaptor.getValue().getFirstName()).isEqualTo("Dorian");
    assertThat(personCaptor.getValue().getLastName()).isEqualTo("Delaval");
    assertThat(personCaptor.getValue()
                           .getAddress()).isEqualTo("8 rue jean jaures")
                                         .isNotNull();
    assertThat(personCaptor.getValue()
                           .getEmail()).isEqualTo("delaval.b@email.com");
    assertThat(personCaptor.getValue().getCity()).isEqualTo("Bruay Sur Escaut");
    assertThat(personCaptor.getValue().getZip()).isEqualTo(59860);
    assertThat(personCaptor.getValue().getBirthDate()).isEqualTo("28/12/1976");
    assertThat(personCaptor.getValue().getPhone()).isEqualTo("061-846-0260");

  }

  @ParameterizedTest
  @Order(5)
  @CsvSource(
    {" , , Delaval, 27/12/1976, 26 av maréchal Foch, Cassis, 13260,"
        + " 061-846-0160, delaval.htps@gmail.com, , ",
     " , Dorian, , 27/12/1976, 26 av maréchal Foch, Cassis, 13260,"
         + " 061-846-0160, delaval.htps@gmail.com, , ",
     // don't use with birthdate null because it's not a fields with @notnull
     // else the application doesn't work because data.json in person doesn't
     // have field birthdate
     // " , Dorian, Delaval, , 26 av maréchal Foch, Cassis, 13260,"
     // + " 061-846-0160, delaval.htps@gmail.com, , ",
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
     " , Dorian, Delaval, 27/12/1976, 26 av maréchal Foch, Cassis, 13260, 061-846-0160, , , "}
  )
  void testPutPerson_WithNoValidInput_thenReturn400(ArgumentsAccessor args)
      throws Exception {

    Person falsePerson =
        new Person(args.getLong(0), args.getString(1), args.getString(2),
                   args.getString(3), args.getString(4), args.getString(5),
                   args.getInteger(6), args.getString(7), args.getString(8),
                   args.get(9, MedicalRecord.class), args.get(10, Set.class));

    ObjectMapper mapper = mapperBuilder.build();

    mockMvc.perform(put("/person/{id}",
                        1).accept(MediaType.APPLICATION_JSON)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(mapper.writeValueAsString(falsePerson)))
           .andExpect(status().is(400)).andDo(print());

    // on verifie que personService n'a pas enregistré de person
    verify(personService, never()).savePerson(Mockito.any(Person.class));

  }

  @ParameterizedTest
  @Order(6)
  @CsvSource(
    {"1, emilie , Delaval, 27/12/1976, 26 av maréchal Foch, Cassis, 13260,"
        + " 061-846-0160, delaval.htps@gmail.com, , ",
     " 1, Dorian , Baudouin, 27/12/1976, 26 av maréchal Foch, Cassis, 13260,"
         + " 061-846-0160, delaval.htps@gmail.com, , ",
     " 1, Emilie, Baudouin, 27/12/1976, 26 av maréchal Foch, Cassis, 13260,"
         + " 061-846-0160, delaval.htps@gmail.com, , "}
  )

  void testPutPerson_whenChangeNames_thenReturn400(ArgumentsAccessor args)
      throws JsonProcessingException, Exception {

    when(personService.getPersonById(Mockito.anyLong())).thenReturn(Optional.of(mockPerson1));

    ObjectMapper mapper = mapperBuilder.build();

    Person mockPerson1WithChangedNames =
        new Person(args.getLong(0), args.getString(1), args.getString(2),
                   args.getString(3), args.getString(4), args.getString(5),
                   args.getInteger(6), args.getString(7), args.getString(8),
                   args.get(9, MedicalRecord.class), args.get(10, Set.class));

    mockMvc.perform(put("/person/{id}",
                        1).accept(MediaType.APPLICATION_JSON)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(mapper.writeValueAsString(mockPerson1WithChangedNames)))
           .andExpect(status().isBadRequest()).andDo(print());

    // on verifie que personService n'a pas enregistré de person
    verify(personService, never()).savePerson(Mockito.any(Person.class));
  }


  @Test
  @Order(7)
  void testPutPerson_withNotFoundPerson_thenReturn404() throws Exception {

    ObjectMapper mapper = mapperBuilder.build();

    // id de la person n'existe pas et on retourne null ou empty for optional
    when(personService.getPersonById(Mockito.anyLong())).thenReturn(Optional.empty());

    mockMvc.perform(put("/person/{id}",
                        1).accept(MediaType.APPLICATION_JSON)
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(mapper.writeValueAsString(mockPerson1)))
           .andExpect(status().isNotFound()).andDo(print());

    // on verifie que personService n'a pas enregistré de person
    verify(personService, never()).savePerson(Mockito.any(Person.class));
  }

  @Test
  @Order(8)
  void testDeletePerson_withValidInputCoupleNames_thenReturn200()
      throws Exception {

    when(personService.getPersonByNames(Mockito.anyString(),
                                        Mockito.anyString())).thenReturn(Optional.of(mockPersonWithId));

    mockMvc.perform(delete("/person/{lastName}/{firstName}", "Dorian",
                           "Delaval"))
           .andExpect(status().isOk());

    ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);

    verify(personService, times(1)).deletePerson(personCaptor.capture());
    assertThat(personCaptor.getValue().getIdPerson()).isEqualTo(1L);
    assertThat(personCaptor.getValue().getFirstName()).isEqualTo("Dorian");
    assertThat(personCaptor.getValue().getLastName()).isEqualTo("Delaval");
    assertThat(personCaptor.getValue()
                           .getAddress()).isEqualTo("26 av marechal foch");

    assertThat(personCaptor.getValue()
                           .getEmail()).isEqualTo("delaval.htps@gmail.com");
    assertThat(personCaptor.getValue().getCity()).isEqualTo("Cassis");
    assertThat(personCaptor.getValue().getZip()).isEqualTo(13260);
    assertThat(personCaptor.getValue().getBirthDate()).isEqualTo("27/12/1976");
    assertThat(personCaptor.getValue().getPhone()).isEqualTo("061-846-0160");

  }

  @Test
  @Order(9)
  void testDeletePerson_withNoValidInputCoupleNames_thenReturn404()
      throws Exception {

    when(personService.getPersonByNames(Mockito.anyString(),
                                        Mockito.anyString())).thenReturn(Optional.empty());

    mockMvc.perform(delete("/person/{lastName}/{firstName}", "Dorian",
                           "Delaval"))
           .andExpect(status().isNotFound()).andDo(print());

    verify(personService, never()).deletePerson(Mockito.any(Person.class));

  }
}
