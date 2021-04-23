package com.safetynet.alert.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.safetynet.alert.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AdminRestControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() throws Exception {}

  @Nested
  @Tag("Test AdminController Person")
  class AdminControllerPersonIT {

    @Test
    void GetPersonsIT() throws Exception {

      mockMvc.perform(get("/persons")).andExpect(status().isOk())
          .andExpect(jsonPath("$[0].firstName", is("John")));

    }

    @Test
    void PostPersonIT() throws Exception {

      // Given
      Person personTest = new Person();
      personTest.setFirstName("Dorian");
      personTest.setLastName("Delaval");
      personTest.setAddress("26 avenue Maréchal Foch");
      personTest.setCity("Cassis");
      personTest.setZip(13260);
      personTest.setEmail("delaval.htps@gmail.com");
      personTest.setBirthDate("27/12/1976");
      personTest.setPhone("0618460160");


      // When and Then
      mockMvc.perform(post("/person")).andExpect(status().is(201))
          .andExpect(jsonPath("$[0].firstName", is("John")));

    }

  }

  @Test
  void GetFireStationsIT() throws Exception {

    mockMvc.perform(get("/firestations")).andExpect(status().isOk())
        .andExpect(jsonPath("$[0].station", is(3)));

  }

  @Test
  void GetMedicalRecordsIT() throws Exception {

    mockMvc.perform(get("/medicalrecords")).andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id_medical_record", is(1)));

  }

  @Test
  void GetMedicationsIT() throws Exception {
    mockMvc.perform(get("/medications")).andExpect(status().isOk())
        .andExpect(jsonPath("$[0].designation", is("aznol")));
  }

}
