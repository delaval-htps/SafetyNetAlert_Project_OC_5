package com.safetynet.alert.controller.admin;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.safetynet.alert.service.AllergyService;
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
class AllergyRestControllerIT {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private AllergyService allergyService;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {}

  @BeforeEach
  void setUp() throws Exception {}

  @Test
  @Order(1)
  void getAllergies() throws Exception {
    mockMvc.perform(get("/allergies")).andExpect(status().isOk())
           .andExpect(jsonPath("$.length()", is(6)))
           .andExpect(jsonPath("$[0].id_Allergy", is(1)))
           .andExpect(jsonPath("$[0].designation", is("nillacilan")));
  }

}
