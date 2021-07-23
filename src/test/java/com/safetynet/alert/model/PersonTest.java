package com.safetynet.alert.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
class PersonTest {
  private Person classUnderTest;
  private FireStation mockFireStation;
  private Set<FireStation> mockSetFireStations;

  @BeforeEach
  void setUp() throws Exception {

    mockFireStation = new FireStation(1L, 1, new HashSet<>(), new HashSet<>());
    mockSetFireStations = new LinkedHashSet<>();

    classUnderTest = new Person(1L, "Dorian", "Delaval", null, "address", "Culver", 97451,
                                "061-846-0160", "dd@email.com", null, mockSetFireStations);

  }

  @Test
  void testAddFireStation() {

    //Given
    //when
    classUnderTest.addFireStation(mockFireStation);
    //then

    assertThat(classUnderTest.getFireStations()).hasSize(1);
    classUnderTest.getFireStations().forEach(fireStation -> {

      assertThat(fireStation).isEqualTo(mockFireStation);
    });

  }

  @Test
  void testAddFireStation_whenFireStationNull() {

    //Given
    mockFireStation = null;
    //when
    classUnderTest.addFireStation(mockFireStation);
    //then

    assertThat(classUnderTest.getFireStations()).hasSize(0);


  }

  @Test
  void testAddFireStations() {

    //Given
    mockSetFireStations.add(mockFireStation);
    //when
    classUnderTest.addFireStations(new ArrayList(mockSetFireStations));
    //then

    assertThat(classUnderTest.getFireStations()).hasSize(1);
    classUnderTest.getFireStations().forEach(fireStation -> {

      assertThat(fireStation).isEqualTo(mockFireStation);
    });

  }

  @Test
  void testAddFireStations_whenSetEmpty() {

    //Given
    mockSetFireStations = new HashSet<>();
    //when
    classUnderTest.addFireStations(new ArrayList(mockSetFireStations));
    //then

    assertThat(classUnderTest.getFireStations()).hasSize(0);

  }


  @Test
  void testRemoveFireStation() {

    //Given
    classUnderTest.addFireStation(mockFireStation);
    //when
    classUnderTest.removeFireStation(mockFireStation);
    //then

    assertThat(classUnderTest.getFireStations()).hasSize(0);

  }

  @Test
  void testRemoveFireStation_whenFireStationNull() {

    //Given
    classUnderTest.addFireStation(mockFireStation);
    FireStation mockNewFireStation = null;
    //when
    classUnderTest.removeFireStation(mockNewFireStation);
    //then

    assertThat(classUnderTest.getFireStations()).hasSize(1);
    classUnderTest.getFireStations().forEach(fireStation -> {

      assertThat(fireStation).isEqualTo(mockFireStation);
    });

  }

  @Test
  void testClearFireStations() {

    //given

    classUnderTest.addFireStation(mockFireStation);
    //when
    classUnderTest.clearFireStations();
    //then
    assertThat(classUnderTest.getFireStations()).isEmpty();

  }

}
