package com.safetynet.alert.controller.admin;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.safetynet.alert.service.MedicalRecordService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
class MedicalRecordRestControllerIT {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private MedicalRecordService medicalRecordService;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {}

  @BeforeEach
  void setUp() throws Exception {}

  @Test
  @Order(1)
  void getMedicalRecords() throws Exception {

    mockMvc.perform(get("/medicalRecords")).andExpect(status().isOk())
           .andExpect(jsonPath("$.length()", is(23)))
           .andExpect(jsonPath("$[0].id_MedicalRecord", is(1)))
           .andExpect(jsonPath("$[0].person.idPerson", is(1)))
           .andExpect(jsonPath("$[0].person.address", is("1509 Culver St")))
           .andExpect(jsonPath("$[0].person.firstName", is("John")))
           .andExpect(jsonPath("$[0].person.lastName", is("Boyd")))
           .andExpect(jsonPath("$[0].person.birthDate", is("03/06/1984")))
           .andExpect(jsonPath("$[0].person.city", is("Culver")))
           .andExpect(jsonPath("$[0].person.zip", is(97451)))
           .andExpect(jsonPath("$[0].person.phone", is("841-874-6512")))
           .andExpect(jsonPath("$[0].person.email", is("jaboyd@email.com")))
           .andExpect(jsonPath("$[0].medications.length()", is(2)))
           .andExpect(jsonPath("$[0].medications[0].id_Medication", is(1)))
           .andExpect(jsonPath("$[0].medications[0].designation", is("aznol")))
           .andExpect(jsonPath("$[0].medications[0].posology", is("350mg")))
           .andExpect(jsonPath("$[0].medications[1].id_Medication", is(2)))
           .andExpect(jsonPath("$[0].medications[1].designation",
                               is("hydrapermazol")))
           .andExpect(jsonPath("$[0].medications[1].posology", is("100mg")))
           .andExpect(jsonPath("$[0].allergies[0].id_Allergy", is(1)))
           .andExpect(jsonPath("$[0].allergies[0].designation",
                               is("nillacilan")));
  }

}
