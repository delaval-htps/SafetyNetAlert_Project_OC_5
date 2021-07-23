package com.safetynet.alert.controller.admin;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.safetynet.alert.service.MedicationService;
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
class MedicationRestControllerIT {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private MedicationService medicationService;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {}

  @BeforeEach
  void setUp() throws Exception {}

  @Test
  @Order(1)
  void getMedications() throws Exception {

    mockMvc.perform(get("/medications")).andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(18)))
        .andExpect(jsonPath("$[0].idMedication", is(1)))
        .andExpect(jsonPath("$[0].designation", is("aznol")))
        .andExpect(jsonPath("$[0].posology", is("350mg")));

  }


}
