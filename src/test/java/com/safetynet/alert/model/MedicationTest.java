package com.safetynet.alert.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
class MedicationTest {

  private Medication classUnderTest;
  private MedicalRecord mockMedicalRecord;

  private HashSet<MedicalRecord> medicalRecords;

  @BeforeEach
  void setUp() throws Exception {

    mockMedicalRecord =
        new MedicalRecord(1L,
                          new Person("Dorian", "Delaval",
                                     new Date(System.currentTimeMillis() - 3600)),
                          new HashSet<Medication>(), new HashSet<Allergy>());

    medicalRecords = new HashSet<MedicalRecord>();

    classUnderTest =
        new Medication(1L, "designationMedication", "posologyMedication", medicalRecords);

  }

  @Test
  @Order(1)
  void addMedicalRecord_whenNewMedicalRecord() {

    //given

    //when
    classUnderTest.add(mockMedicalRecord);

    //then
    assertThat(classUnderTest.getMedicalRecords()).hasSize(1);
    classUnderTest.getMedicalRecords().forEach(medicalRecord -> {

      assertThat(medicalRecord).isEqualTo(mockMedicalRecord);
    });

  }

  @Test
  @Order(2)
  void addMedicalRecord_whenMedicalRecordNull() {

    //given
    mockMedicalRecord = null;
    //when
    classUnderTest.add(mockMedicalRecord);

    //then
    assertThat(classUnderTest.getMedicalRecords()).hasSize(0);

  }

  @Test
  @Order(3)
  void addMedicalRecord_whenMedicalRecordAlreadyInSet() {

    //given

    medicalRecords.add(mockMedicalRecord);

    classUnderTest.setMedicalRecords(medicalRecords);

    //when
    classUnderTest.add(mockMedicalRecord);

    //then
    assertThat(classUnderTest.getMedicalRecords()).hasSize(1);

  }

  @Test
  @Order(4)
  void removeMedicalRecord_whenMedicalRecordsContainsIt() {

    //given
    classUnderTest.getMedicalRecords().add(mockMedicalRecord);
    //when
    classUnderTest.remove(mockMedicalRecord);
    //then
    assertThat(classUnderTest.getMedicalRecords()).isEmpty();

  }

  @Test
  @Order(4)
  void removeMedicalRecord_whenMedicalRecordsDontContainIt() {

    //given
    classUnderTest.add(mockMedicalRecord);
    MedicalRecord mockNewMedicalRecord = new MedicalRecord(1L,
                                                           new Person("Bernard", "Delaval",
                                                                      new Date(System
                                                                          .currentTimeMillis()
                                                                          - 3600)),
                                                           new HashSet<Medication>(),
                                                           new HashSet<Allergy>());
    //when
    classUnderTest.remove(mockNewMedicalRecord);
    //then
    assertThat(classUnderTest.getMedicalRecords()).hasSize(1);
    classUnderTest.getMedicalRecords().forEach(medicalRecord -> {

      assertThat(medicalRecord).isEqualTo(mockMedicalRecord);
    });

  }

  @Test
  @Order(5)
  void removeMedicalRecord_whenMedicalRecordNull() {

    //given
    classUnderTest.add(mockMedicalRecord);
    MedicalRecord mockNewMedicalRecord = null;
    //when
    classUnderTest.remove(mockNewMedicalRecord);
    //then
    assertThat(classUnderTest.getMedicalRecords()).hasSize(1);
    classUnderTest.getMedicalRecords().forEach(medicalRecord -> {

      assertThat(medicalRecord).isEqualTo(mockMedicalRecord);
    });

  }
}
