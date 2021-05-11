package com.safetynet.alert.controller.admin;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.safetynet.alert.service.FireStationService;
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
class FireStationRestControllerIT {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private FireStationService fireStationService;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {}

  @BeforeEach
  void setUp() throws Exception {}

  @Test
  @Order(1)
  void getFireStations() throws Exception {

    mockMvc.perform(get("/firestations")).andExpect(status().isOk())
           .andExpect(jsonPath("$.length()", is(4)))
           .andExpect(jsonPath("$[0].id_FireStation", is(1)))
           .andExpect(jsonPath("$[0].numberStation", is(3)));

  }

}
