package com.safetynet.alert.controller.admin;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.PersonService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@Log4j2
class PersonRestControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private PersonService personService;



  private Person personTest =
      new Person(null, "Dorian", "Delaval", "27/12/76", "26 av maréchal foch",
                 "Cassis", 13260, "06-18-46-01-60", "delava.htps@gmail.com",
                 null, null);

  @Test
  @Order(1)
  void getPersons() throws Exception {

    // assume that we check one line of jsonPAth : if it's
    // correct then it is for the others
    mockMvc.perform(get("/persons")).andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$").exists())
           .andExpect(jsonPath("$.length()", is(23)))
           .andExpect(jsonPath("$[0].id_Person", is(1)))
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
  void postPerson() throws Exception {

    // When and Then
    mockMvc.perform(post("/person").accept(MediaType.APPLICATION_JSON)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .content(asJsonString(personTest)))
           .andExpect(status().isCreated())
           // how to verify URI ?
           .andExpect(header().exists("/person/24")).andDo(print());
  }

  @Test
  @Order(3)
  void putPerson() throws Exception {

    // Given add id= 1L to check if it changes the first person
    // in database
    personTest.setIdPerson(1L);


    mockMvc.perform(put("/person/{id}",
                        1).contentType(MediaType.APPLICATION_JSON)
                          .content(asJsonString(personTest)))
           .andExpect(status().isOk()).andExpect(jsonPath("$.length()", is(1)))
           .andExpect(jsonPath("$[0].firstName", is("Dorian")))
           .andExpect(jsonPath("$[0].lastName", is("Delaval")))
           .andExpect(jsonPath("$[0].address", is("26 av maréchal foch")))
           .andExpect(jsonPath("$[0].city", is("Cassis")))
           .andExpect(jsonPath("$[0].birthDate", is("27/12/1976")))
           .andExpect(jsonPath("$[0].zip", is(13260)))
           .andExpect(jsonPath("$[0].phone", is("06-18-46-01-60")))
           .andExpect(jsonPath("$[0].email", is("delaval.htps@gmail.com")))
           .andDo(print());
  }

  @Test
  @Order(4)
  void deletePerson() throws Exception {

    // Given : we pass firstName and lastName of the first
    // person
    LinkedMultiValueMap<String, String> multipleValues =
        new LinkedMultiValueMap<String, String>();

    multipleValues.add("firstName", "John");
    multipleValues.add("lastName", "Boyd");

    mockMvc.perform(delete("/person/").params(multipleValues))
           .andExpect(status().isAccepted()).andDo(print());
  }


  private String asJsonString(Person personToString) {
    ObjectMapper mapper = new ObjectMapper();
    String result;

    try {
      result = mapper.writeValueAsString(personToString);
    } catch (JsonProcessingException e) {
      log.error("Unable to parse Person to string", e);
      e.printStackTrace();
      return null;
    }
    return result;
  }

}


