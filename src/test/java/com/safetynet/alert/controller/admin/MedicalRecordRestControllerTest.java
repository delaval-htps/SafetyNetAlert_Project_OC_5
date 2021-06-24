package com.safetynet.alert.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alert.exceptions.medicalrecord.MedicalRecordAlreadyExistedException;
import com.safetynet.alert.exceptions.medicalrecord.MedicalRecordChangedNamesException;
import com.safetynet.alert.exceptions.medicalrecord.MedicalRecordNotFoundException;
import com.safetynet.alert.model.Allergy;
import com.safetynet.alert.model.FireStation;
import com.safetynet.alert.model.MedicalRecord;
import com.safetynet.alert.model.Medication;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.AllergyService;
import com.safetynet.alert.service.FireStationService;
import com.safetynet.alert.service.MedicalRecordService;
import com.safetynet.alert.service.MedicationService;
import com.safetynet.alert.service.PersonService;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = MedicalRecordRestController.class)
class MedicalRecordRestControllerTest {

  @MockBean
  private MedicalRecordService medicalRecordService;
  @MockBean
  private PersonService personService;
  @MockBean
  private FireStationService fireStationService;
  @MockBean
  private MedicationService medicationService;
  @MockBean
  private AllergyService allergyService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private Jackson2ObjectMapperBuilder mapperBuilder;

  private Person mockPerson1;
  private Medication mockMedication1;
  private Allergy mockAllergy1;
  private MedicalRecord mockMedicalRecord1;
  private Set<Allergy> mockAllergies1;
  private Set<Medication> mockMedications1;

  private Person mockPerson2;
  private Medication mockMedication2;
  private Allergy mockAllergy2;
  private MedicalRecord mockMedicalRecord2;
  private Set<Medication> mockMedications2;
  private Set<Allergy> mockAllergies2;

  private Set<MedicalRecord> mockMedicalRecords;

  private Person mockPersonWithoutId;
  private Medication mockMedicationWithoutId;
  private Allergy mockAllergyWithoutId;
  private HashSet<Medication> mockMedicationsWithoutId;
  private HashSet<Allergy> mockAllergiesWithoutId;
  private MedicalRecord mockMedicalRecordWithoutId;

  private Person mockPerson1WithMedicalRecord;

  private FireStation mockFireStation;
  private Set<@NotBlank String> mockAddresses;
  private Set<Person> mockPersons;
  private SimpleDateFormat sdf;

  @BeforeEach
  void setUpBeforeTest() throws Exception {

    sdf = new SimpleDateFormat("MM/dd/yyyy");

    // **************** mockMedicalRecord1 with all fields *********************
    mockPerson1 = new Person(1L, "Dorian", "Delaval", sdf.parse("12/27/1976"),
                             "26 av maréchal Foch", "Cassis", 13260,
                             "061-846-0160", "delaval.htps@gmail.com",
                             null, null);
    mockMedication1 = new Medication(1L, "medication1", "100mg", mockMedicalRecords);
    mockMedications1 = new HashSet<Medication>();

    mockMedications1.add(mockMedication1);

    mockAllergy1 = new Allergy(1L, "allergy1", mockMedicalRecords);

    mockAllergies1 = new HashSet<Allergy>();
    mockAllergies1.add(mockAllergy1);

    mockMedicalRecord1 = new MedicalRecord(1L, mockPerson1, mockMedications1, mockAllergies1);

    mockPerson1.setMedicalRecord(mockMedicalRecord1);

    //  mockPerson1WithMedicalRecord :same that mockPerson1 but mapped with MedicalRecord
    mockPerson1WithMedicalRecord = new Person(1L, "Dorian", "Delaval", sdf.parse("12/27/1976"),
                                              "26 av maréchal Foch", "Cassis", 13260,
                                              "061-846-0160", "delaval.htps@gmail.com",
                                              mockMedicalRecord1, null);


    // ******************  mockMedicalRecord2 with all fields *************
    mockPerson2 = new Person(2L, "Emilie", "Delaval", sdf.parse("02/22/1984"),
                             "150 rue de la  Prairie", "Mulbach sur Munster", 67000,
                             "061-846-0260", "delaval.emilie@gmail.com",
                             null, null);

    mockMedication2 = new Medication(2L, "medication2", "200mg", mockMedicalRecords);

    mockMedications2 = new HashSet<Medication>();
    mockMedications2.add(mockMedication2);

    mockAllergy2 = new Allergy(2L, "allergy2", mockMedicalRecords);

    mockAllergies2 = new HashSet<Allergy>();
    mockAllergies2.add(mockAllergy2);

    mockMedicalRecord2 = new MedicalRecord(2L, mockPerson2, mockMedications2, mockAllergies2);

    mockPerson2.setMedicalRecord(mockMedicalRecord2);

    // ***  mockMedicalRecordWithoutId: same that mockMedicalRecord1 but without id ***
    mockPersonWithoutId = new Person(null, "Dorian", "Delaval", sdf.parse("12/27/1976"),
                                     "26 av maréchal Foch", "Cassis", 13260,
                                     "061-846-0160", "delaval.htps@gmail.com",
                                     null, null);
    mockMedicationWithoutId = new Medication(null, "medication1", "100mg", null);

    mockMedicationsWithoutId = new HashSet<Medication>();
    mockMedicationsWithoutId.add(mockMedicationWithoutId);

    mockAllergyWithoutId = new Allergy(null, "allergy1", null);

    mockAllergiesWithoutId = new HashSet<Allergy>();
    mockAllergiesWithoutId.add(mockAllergyWithoutId);

    mockMedicalRecordWithoutId =
        new MedicalRecord(null, mockPersonWithoutId, mockMedicationsWithoutId,
                          mockAllergiesWithoutId);

    // *********** medicalRecords to getMedicalRecords ******************
    mockMedicalRecords = new LinkedHashSet<MedicalRecord>();
    mockMedicalRecords.add(mockMedicalRecord1);
    mockMedicalRecords.add(mockMedicalRecord2);

    // ********************* mockFireStation with mockAddresses and mockPersons **************
    mockAddresses = new HashSet<String>();
    mockAddresses.add("AddressMappedByFireStation");

    mockPersons = new HashSet<Person>();

    mockFireStation = new FireStation(1L, 1, mockAddresses, mockPersons);

  }

  @BeforeEach
  void setUp() throws Exception {}

  @Test
  @Order(1)
  void getMedicalRecords() throws Exception {
    //given

    when(medicalRecordService.getMedicalRecords())
        .thenReturn(mockMedicalRecords);

    mockMvc.perform(get("/medicalRecord")).andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(2)))
        .andExpect(jsonPath("$[0].idMedicalRecord", is(1)))
        .andExpect(jsonPath("$[0].person.idPerson", is(1)))
        .andExpect(jsonPath("$[0].person.address", is("26 av maréchal Foch")))
        .andExpect(jsonPath("$[0].person.firstName", is("Dorian")))
        .andExpect(jsonPath("$[0].person.lastName", is("Delaval")))
        .andExpect(jsonPath("$[0].person.birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$[0].person.city", is("Cassis")))
        .andExpect(jsonPath("$[0].person.zip", is(13260)))
        .andExpect(jsonPath("$[0].person.phone", is("061-846-0160")))
        .andExpect(jsonPath("$[0].person.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$[0].medications.length()", is(1)))
        .andExpect(jsonPath("$[0].medications.[0]idMedication", is(1)))
        .andExpect(jsonPath("$[0].medications[0].designation", is("medication1")))
        .andExpect(jsonPath("$[0].medications[0].posology", is("100mg")))
        .andExpect(jsonPath("$[0].allergies.length()", is(1)))
        .andExpect(jsonPath("$[0].allergies[0].idAllergy", is(1)))
        .andExpect(jsonPath("$[0].allergies[0].designation", is("allergy1")))
        .andExpect(jsonPath("$[1].idMedicalRecord", is(2)))
        .andExpect(jsonPath("$[1].person.idPerson", is(2)))
        .andExpect(jsonPath("$[1].person.address", is("150 rue de la  Prairie")))
        .andExpect(jsonPath("$[1].person.firstName", is("Emilie")))
        .andExpect(jsonPath("$[1].person.lastName", is("Delaval")))
        .andExpect(jsonPath("$[1].person.birthDate", is("02/22/1984")))
        .andExpect(jsonPath("$[1].person.city", is("Mulbach sur Munster")))
        .andExpect(jsonPath("$[1].person.zip", is(67000)))
        .andExpect(jsonPath("$[1].person.phone", is("061-846-0260")))
        .andExpect(jsonPath("$[1].person.email", is("delaval.emilie@gmail.com")))
        .andExpect(jsonPath("$[1].medications.length()", is(1)))
        .andExpect(jsonPath("$[1].medications[0].idMedication", is(2)))
        .andExpect(jsonPath("$[1].medications[0].designation", is("medication2")))
        .andExpect(jsonPath("$[1].medications[0].posology", is("200mg")))
        .andExpect(jsonPath("$[1].allergies.length()", is(1)))
        .andExpect(jsonPath("$[1].allergies[0].idAllergy", is(2)))
        .andExpect(jsonPath("$[1].allergies[0].designation", is("allergy2")))
        .andDo(print());

  }

  @Test
  @Order(2)
  void getMedicalRecordById_whenValidId_thenReturn200() throws Exception {

    //Given
    when(medicalRecordService.getMedicalRecordById(Mockito.anyLong()))
        .thenReturn(Optional.of(mockMedicalRecord1));

    //when &then
    mockMvc.perform(get("/medicalRecord/{id}", 1))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.idMedicalRecord", is(1)))
        .andExpect(jsonPath("$.person.idPerson", is(1)))
        .andExpect(jsonPath("$.person.address", is("26 av maréchal Foch")))
        .andExpect(jsonPath("$.person.firstName", is("Dorian")))
        .andExpect(jsonPath("$.person.lastName", is("Delaval")))
        .andExpect(jsonPath("$.person.birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$.person.city", is("Cassis")))
        .andExpect(jsonPath("$.person.zip", is(13260)))
        .andExpect(jsonPath("$.person.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.person.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.medications.length()", is(1)))
        .andExpect(jsonPath("$.medications[0].idMedication", is(1)))
        .andExpect(jsonPath("$.medications[0].designation", is("medication1")))
        .andExpect(jsonPath("$.medications[0].posology", is("100mg")))
        .andExpect(jsonPath("$.allergies[0].idAllergy", is(1)))
        .andExpect(jsonPath("$.allergies[0].designation", is("allergy1")));

  }

  @Test
  @Order(3)
  void getMedicalRecordById_whenNotFoundId_thenReturn404() throws Exception {

    //Given
    when(medicalRecordService.getMedicalRecordById(Mockito.anyLong()))
        .thenReturn(Optional.empty());

    //when &then
    MvcResult result = mockMvc.perform(get("/medicalRecord/{id}", 5))
        .andExpect(status().isNotFound()).andReturn();

    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("MedicalRecord with id:5 was not found!");


  }

  @Test
  @Order(4)
  void postMedicalRecord_whenValidInputWithNoPersistedDatas_thenReturn201() throws Exception {

    //Given
    ObjectMapper mapper = mapperBuilder.build();

    when(personService.getPersonByNames(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(Optional.empty());

    when(medicalRecordService.saveMedicalRecord(Mockito.any(MedicalRecord.class)))
        .thenReturn(mockMedicalRecord1);

    //when & then
    mockMvc.perform(post("/medicalRecord").accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockMedicalRecordWithoutId)))

        .andExpect(status().isCreated())
        .andExpect(redirectedUrlPattern("http://*/medicalRecord/1"))
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.idMedicalRecord", is(1)))
        .andExpect(jsonPath("$.person.idPerson", is(1)))
        .andExpect(jsonPath("$.person.address", is("26 av maréchal Foch")))
        .andExpect(jsonPath("$.person.firstName", is("Dorian")))
        .andExpect(jsonPath("$.person.lastName", is("Delaval")))
        .andExpect(jsonPath("$.person.birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$.person.city", is("Cassis")))
        .andExpect(jsonPath("$.person.zip", is(13260)))
        .andExpect(jsonPath("$.person.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.person.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.medications.length()", is(1)))
        .andExpect(jsonPath("$.medications[0].idMedication", is(1)))
        .andExpect(jsonPath("$.medications[0].designation", is("medication1")))
        .andExpect(jsonPath("$.medications[0].posology", is("100mg")))
        .andExpect(jsonPath("$.allergies[0].idAllergy", is(1)))
        .andExpect(jsonPath("$.allergies[0].designation", is("allergy1"))).andDo(print());

    ArgumentCaptor<MedicalRecord> medicalRecordCaptor =
        ArgumentCaptor.forClass(MedicalRecord.class);
    verify(medicalRecordService, times(1)).saveMedicalRecord(medicalRecordCaptor.capture());
    assertThat(medicalRecordCaptor.getValue().getPerson().getIdPerson()).isNull();
    assertThat(medicalRecordCaptor.getValue().getPerson().getLastName()).isEqualTo("Delaval");
    assertThat(medicalRecordCaptor.getValue().getPerson().getFirstName()).isEqualTo("Dorian");
    assertThat(medicalRecordCaptor.getValue().getPerson().getAddress())
        .isEqualTo("26 av maréchal Foch");
    assertThat(medicalRecordCaptor.getValue().getPerson().getCity()).isEqualTo("Cassis");
    assertThat(medicalRecordCaptor.getValue().getPerson().getZip()).isEqualTo(13260);
    assertThat(medicalRecordCaptor.getValue().getPerson().getEmail())
        .isEqualTo("delaval.htps@gmail.com");
    assertThat(medicalRecordCaptor.getValue().getPerson().getPhone())
        .isEqualTo("061-846-0160");
    assertThat(medicalRecordCaptor.getValue().getMedications().size()).isEqualTo(1);
    assertThat(medicalRecordCaptor.getValue().getMedications().toString()).isEqualTo(
        "[Medication(idMedication=null, designation=medication1, posology=100mg)]");
    assertThat(medicalRecordCaptor.getValue().getAllergies().toString()).isEqualTo(
        "[Allergy(idAllergy=null, designation=allergy1)]");

  }

  @Test
  @Order(5)
  void postMedicalRecord_whenExistedPersonWithoutMedicalRecord_thenReturn201()
      throws Exception {

    //given

    // we delete medicalRecord from Person1
    mockPerson1.setMedicalRecord(null);

    // we retrieve person without medicalRecord
    when(personService.getPersonByNames(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(Optional.of(mockPerson1));

    when(medicalRecordService.saveMedicalRecord(Mockito.any(MedicalRecord.class)))
        .thenReturn(mockMedicalRecord1);

    when(personService.savePerson(Mockito.any(Person.class)))
        .thenReturn(mockPerson1WithMedicalRecord);

    ObjectMapper mapper = mapperBuilder.build();
    //when & then
    mockMvc.perform(post("/medicalRecord").accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockMedicalRecordWithoutId)))
        .andExpect(status().isCreated())
        .andExpect(redirectedUrlPattern("http://*/medicalRecord/1"))
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.idMedicalRecord", is(1)))
        .andExpect(jsonPath("$.person.idPerson", is(1)))
        .andExpect(jsonPath("$.person.address", is("26 av maréchal Foch")))
        .andExpect(jsonPath("$.person.firstName", is("Dorian")))
        .andExpect(jsonPath("$.person.lastName", is("Delaval")))
        .andExpect(jsonPath("$.person.birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$.person.city", is("Cassis")))
        .andExpect(jsonPath("$.person.zip", is(13260)))
        .andExpect(jsonPath("$.person.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.person.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.medications.length()", is(1)))
        .andExpect(jsonPath("$.medications[0].idMedication", is(1)))
        .andExpect(jsonPath("$.medications[0].designation", is("medication1")))
        .andExpect(jsonPath("$.medications[0].posology", is("100mg")))
        .andExpect(jsonPath("$.allergies[0].idAllergy", is(1)))
        .andExpect(jsonPath("$.allergies[0].designation", is("allergy1"))).andDo(print());

    // check if person was correctly mapped to new medicalRecord
    ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);
    verify(personService, times(1)).savePerson(personCaptor.capture());
    assertThat(personCaptor.getValue().getMedicalRecord()).isEqualTo(mockMedicalRecord1);

    //check if medicalRecord was correctly mapped to the existed person
    ArgumentCaptor<MedicalRecord> medicalRecordCaptor =
        ArgumentCaptor.forClass(MedicalRecord.class);
    verify(medicalRecordService, times(1)).saveMedicalRecord(medicalRecordCaptor.capture());
    assertThat(medicalRecordCaptor.getValue().getPerson()).isEqualTo(mockPerson1);

  }

  @Test
  @Order(6)
  void postMedicalRecord_whenMedicalRecordAlreadyExisted_thenReturn400() throws Exception {

    //given
    ObjectMapper mapper = mapperBuilder.build();

    when(personService.getPersonByNames(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(Optional.of(mockPerson1));

    //when & then
    mockMvc.perform(post("/medicalRecord").accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockMedicalRecord1)))
        .andExpect(status().isBadRequest())
        .andDo(print())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof MedicalRecordAlreadyExistedException))
        .andExpect(result -> assertEquals(result.getResolvedException().getMessage(),
            "MedicalRecord for this person already exist!"
                + " Please chose another Person to map with"));

  }

  @Test
  @Order(7)
  void putMedicalRecord_whenNotFoundMedicalRecord_thenReturn404() throws Exception {

    //given
    ObjectMapper mapper = mapperBuilder.build();

    when(medicalRecordService.getMedicalRecordById(Mockito.anyLong()))
        .thenReturn(Optional.empty());

    //when & then
    MvcResult result = mockMvc.perform(put("/medicalRecord/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockMedicalRecord1)))

        .andExpect(status().isNotFound())
        .andDo(print()).andReturn();

    assertThat(result.getResolvedException())
        .isInstanceOf(MedicalRecordNotFoundException.class);
    assertThat(result.getResolvedException().getMessage()).isEqualTo(
        "MedicalRecord with id: 1 was not found ! please chose a existed medicalRecord.");

  }

  @ParameterizedTest
  @CsvSource({"Dorian, Baudouin",
              "John, Delaval",
              "Dorian,",
              ",Delaval"})
  @Order(8)
  void putMedicalRecord_whenMedicalRecordPersonChangedNames_thenReturn400(
      ArgumentsAccessor args) throws Exception {

    //given

    when(medicalRecordService.getMedicalRecordById(Mockito.anyLong()))
        .thenReturn(Optional.of(mockMedicalRecord1));

    mockPersonWithoutId.setFirstName(args.getString(0));
    mockPersonWithoutId.setLastName(args.getString(1));

    ObjectMapper mapper = mapperBuilder.build();

    //when & then
    MvcResult result = mockMvc.perform(put("/medicalRecord/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockMedicalRecordWithoutId)))

        .andExpect(status().isBadRequest())
        .andDo(print()).andReturn();

    assertThat(result.getResolvedException())
        .isInstanceOf(MedicalRecordChangedNamesException.class);
    assertThat(result.getResolvedException().getMessage()).isEqualTo(
        "Can't change names of person in a MedicalRecord! "
            + "Please don't modify fistName and LastName of the Person");

  }

  @Test
  @Order(9)
  void putMedicalRecord_whenSameAddressMedicationAllergy_thenReturn200()
      throws Exception {

    //given
    when(medicalRecordService.getMedicalRecordById(Mockito.anyLong()))
        .thenReturn(Optional.of(mockMedicalRecord1));

    when(fireStationService.getFireStationMappedToAddress(Mockito.anyString()))
        .thenReturn(Optional.empty());

    // mockMedicalRecordWithId represents the return of medicalRecord.save()
    MedicalRecord mockMedicalRecordWithId = mockMedicalRecord1;
    mockMedicalRecordWithId.getPerson()
        .setBirthDate(new SimpleDateFormat("MM/dd/yyyy").parse("12/25/1976"));
    mockMedicalRecordWithId.getPerson().setPhone("061-846-0260");

    when(medicalRecordService.saveMedicalRecord(Mockito.any(MedicalRecord.class)))
        .thenReturn(mockMedicalRecordWithId);

    // change of few fields in this MedicalRecord without lastName,firstName,address
    mockMedicalRecordWithoutId.getPerson()
        .setBirthDate(new SimpleDateFormat("MM/dd/yyyy").parse("12/25/1976"));
    mockMedicalRecordWithoutId.getPerson().setPhone("061-846-0260");

    ObjectMapper mapper = mapperBuilder.build();

    //when & then
    mockMvc.perform(put("/medicalRecord/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockMedicalRecordWithoutId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.idMedicalRecord", is(1)))
        .andExpect(jsonPath("$.person.idPerson", is(1)))
        .andExpect(jsonPath("$.person.address", is("26 av maréchal Foch")))
        .andExpect(jsonPath("$.person.firstName", is("Dorian")))
        .andExpect(jsonPath("$.person.lastName", is("Delaval")))
        .andExpect(jsonPath("$.person.birthDate", is("12/25/1976")))
        .andExpect(jsonPath("$.person.city", is("Cassis")))
        .andExpect(jsonPath("$.person.zip", is(13260)))
        .andExpect(jsonPath("$.person.phone", is("061-846-0260")))
        .andExpect(jsonPath("$.person.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.medications.length()", is(1)))
        .andExpect(jsonPath("$.medications[0].idMedication", is(1)))
        .andExpect(jsonPath("$.medications[0].designation", is("medication1")))
        .andExpect(jsonPath("$.medications[0].posology", is("100mg")))
        .andExpect(jsonPath("$.allergies[0].idAllergy", is(1)))
        .andExpect(jsonPath("$.allergies[0].designation", is("allergy1"))).andDo(print());

    //check that there is no new medication nor allergy saved
    verify(medicationService, never()).saveMedication(Mockito.any(Medication.class));
    verify(allergyService, never()).saveAllergy(Mockito.any(Allergy.class));

    //check that was a existedMedicalRecord with its id that was correctly saved
    ArgumentCaptor<MedicalRecord> medicalRecordCaptor =
        ArgumentCaptor.forClass(MedicalRecord.class);
    verify(medicalRecordService, times(1)).saveMedicalRecord(medicalRecordCaptor.capture());
    assertThat(medicalRecordCaptor.getValue().getIdMedicalRecord()).isPositive();
    assertThat(medicalRecordCaptor.getValue().getPerson().getIdPerson()).isPositive();

  }

  @Test
  @Order(10)
  void putMedicalRecord_whenChangeAddressNotMappedByFireStation_thenReturn200()
      throws Exception {

    //given
    when(medicalRecordService.getMedicalRecordById(Mockito.anyLong()))
        .thenReturn(Optional.of(mockMedicalRecord1));

    when(fireStationService.getFireStationMappedToAddress(Mockito.anyString()))
        .thenReturn(Optional.empty());

    when(medicationService.saveMedication(Mockito.any(Medication.class)))
        .thenReturn(mockMedication1);

    when(allergyService.saveAllergy(Mockito.any(Allergy.class)))
        .thenReturn(mockAllergy1);

    // mockMedicalRecordWithId represents the return of medicalRecord.save()
    MedicalRecord mockMedicalRecordWithId = mockMedicalRecord1;

    when(medicalRecordService.saveMedicalRecord(Mockito.any(MedicalRecord.class)))
        .thenReturn(mockMedicalRecordWithId);

    // change of address with one not mapped by any FireStation
    mockMedicalRecordWithoutId.getPerson().setAddress("addressNotMapped");

    ObjectMapper mapper = mapperBuilder.build();

    //when & then
    MvcResult result = mockMvc.perform(put("/medicalRecord/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockMedicalRecordWithoutId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.idMedicalRecord", is(1)))
        .andExpect(jsonPath("$.person.idPerson", is(1)))
        .andExpect(jsonPath("$.person.address", is("addressNotMapped")))
        .andExpect(jsonPath("$.person.firstName", is("Dorian")))
        .andExpect(jsonPath("$.person.lastName", is("Delaval")))
        .andExpect(jsonPath("$.person.birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$.person.city", is("Cassis")))
        .andExpect(jsonPath("$.person.zip", is(13260)))
        .andExpect(jsonPath("$.person.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.person.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.medications.length()", is(1)))
        .andExpect(jsonPath("$.medications[0].idMedication", is(1)))
        .andExpect(jsonPath("$.medications[0].designation", is("medication1")))
        .andExpect(jsonPath("$.medications[0].posology", is("100mg")))
        .andExpect(jsonPath("$.allergies[0].idAllergy", is(1)))
        .andExpect(jsonPath("$.allergies[0].designation", is("allergy1"))).andDo(print())
        .andReturn();

    //check if FireStation is null for person to saved in medicalRecord
    ArgumentCaptor<MedicalRecord> medicalRecordCaptor =
        ArgumentCaptor.forClass(MedicalRecord.class);
    verify(medicalRecordService, times(1)).saveMedicalRecord(medicalRecordCaptor.capture());
    assertThat(medicalRecordCaptor.getValue().getPerson().getFireStation()).isNull();
    //check that there is no new medication nor allergy saved
    verify(medicationService, never()).saveMedication(Mockito.any(Medication.class));
    verify(allergyService, never()).saveAllergy(Mockito.any(Allergy.class));

  }

  @Test
  @Order(11)
  void putMedicalRecord_whenChangeAddressMappedByFireStation_thenReturn200()
      throws Exception {

    //given
    when(medicalRecordService.getMedicalRecordById(Mockito.anyLong()))
        .thenReturn(Optional.of(mockMedicalRecord1));

    when(fireStationService.getFireStationMappedToAddress(Mockito.anyString()))
        .thenReturn(Optional.of(mockFireStation));

    when(medicationService.saveMedication(Mockito.any(Medication.class)))
        .thenReturn(mockMedication1);

    when(allergyService.saveAllergy(Mockito.any(Allergy.class)))
        .thenReturn(mockAllergy1);

    // mockMedicalRecordWithId represents the return of medicalRecord.save()
    MedicalRecord mockMedicalRecordWithId = mockMedicalRecord1;

    when(medicalRecordService.saveMedicalRecord(Mockito.any(MedicalRecord.class)))
        .thenReturn(mockMedicalRecordWithId);

    // change of address with one not mapped by any FireStation
    mockMedicalRecordWithoutId.getPerson().setAddress("AddressMappedByFireStation");

    ObjectMapper mapper = mapperBuilder.build();

    //when & then
    MvcResult result = mockMvc.perform(put("/medicalRecord/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockMedicalRecordWithoutId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.idMedicalRecord", is(1)))
        .andExpect(jsonPath("$.person.idPerson", is(1)))
        .andExpect(jsonPath("$.person.address", is("AddressMappedByFireStation")))
        .andExpect(jsonPath("$.person.firstName", is("Dorian")))
        .andExpect(jsonPath("$.person.lastName", is("Delaval")))
        .andExpect(jsonPath("$.person.birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$.person.city", is("Cassis")))
        .andExpect(jsonPath("$.person.zip", is(13260)))
        .andExpect(jsonPath("$.person.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.person.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.medications.length()", is(1)))
        .andExpect(jsonPath("$.medications[0].idMedication", is(1)))
        .andExpect(jsonPath("$.medications[0].designation", is("medication1")))
        .andExpect(jsonPath("$.medications[0].posology", is("100mg")))
        .andExpect(jsonPath("$.allergies[0].idAllergy", is(1)))
        .andExpect(jsonPath("$.allergies[0].designation", is("allergy1"))).andDo(print())
        .andReturn();

    //check if FireStation was correctly mapped with person by address
    ArgumentCaptor<MedicalRecord> medicalRecordCaptor =
        ArgumentCaptor.forClass(MedicalRecord.class);
    verify(medicalRecordService, times(1)).saveMedicalRecord(medicalRecordCaptor.capture());
    assertThat(medicalRecordCaptor.getValue().getPerson().getFireStation().getIdFireStation())
        .isPositive();
    //check that there is no new medication nor allergy saved
    verify(medicationService, never()).saveMedication(Mockito.any(Medication.class));
    verify(allergyService, never()).saveAllergy(Mockito.any(Allergy.class));

  }

  @Test
  @Order(12)
  void putMedicalRecord_whenChangeMedicationsWithExistedOne_thenReturn200()
      throws Exception {

    //Given
    when(medicalRecordService.getMedicalRecordById(Mockito.anyLong()))
        .thenReturn(Optional.of(mockMedicalRecord1));

    when(fireStationService.getFireStationMappedToAddress(Mockito.anyString()))
        .thenReturn(Optional.empty());

    when(medicationService.getMedicationByDesignationAndPosology(Mockito.anyString(),
        Mockito.anyString())).thenReturn(Optional.of(mockMedication2));

    when(allergyService.getAllergyByDesignation(Mockito.anyString()))
        .thenReturn(Optional.of(mockAllergy1));

    // mockMedicalRecordWithId represents the return of medicalRecord.save()
    HashSet<Medication> mockMedicationsWithId = new HashSet<Medication>();
    HashSet<Allergy> mockAllergiesWithId = new HashSet<Allergy>();

    Medication mockMedicationWithId = new Medication(2L, "medication2", "200mg", null);
    Allergy mockAllergyWithId = new Allergy(1L, "allergy1", null);
    mockMedicationsWithId.add(mockMedicationWithId);
    mockAllergiesWithId.add(mockAllergyWithId);

    MedicalRecord mockMedicalRecordWithId = new MedicalRecord();
    mockMedicalRecordWithId.setAllergies(mockAllergiesWithId);
    mockMedicalRecordWithId.setIdMedicalRecord(1L);
    mockMedicalRecordWithId.setMedications(mockMedicationsWithId);
    mockMedicalRecordWithId.setPerson(mockPerson1);


    when(medicalRecordService.saveMedicalRecord(Mockito.any(MedicalRecord.class)))
        .thenReturn(mockMedicalRecordWithId);

    // mockMedicalRecordWithoutId represents the requestBody: change medication only
    mockMedicationsWithoutId.clear();
    mockMedicationWithoutId.setIdMedication(null);
    mockMedicationWithoutId.setDesignation("medication2");
    mockMedicationWithoutId.setPosology("200mg");
    mockMedicationWithoutId.setMedicalRecords(null);
    mockMedicationsWithoutId.add(mockMedicationWithoutId);
    mockMedicalRecordWithoutId.setMedications(mockMedicationsWithoutId);

    ObjectMapper mapper = mapperBuilder.build();

    //when & then
    MvcResult result = mockMvc.perform(put("/medicalRecord/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockMedicalRecordWithoutId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.idMedicalRecord", is(1)))
        .andExpect(jsonPath("$.person.idPerson", is(1)))
        .andExpect(jsonPath("$.person.address", is("26 av maréchal Foch")))
        .andExpect(jsonPath("$.person.firstName", is("Dorian")))
        .andExpect(jsonPath("$.person.lastName", is("Delaval")))
        .andExpect(jsonPath("$.person.birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$.person.city", is("Cassis")))
        .andExpect(jsonPath("$.person.zip", is(13260)))
        .andExpect(jsonPath("$.person.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.person.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.medications.length()", is(1)))
        .andExpect(jsonPath("$.medications[0].idMedication", is(2)))
        .andExpect(jsonPath("$.medications[0].designation", is("medication2")))
        .andExpect(jsonPath("$.medications[0].posology", is("200mg")))
        .andExpect(jsonPath("$.allergies[0].idAllergy", is(1)))
        .andExpect(jsonPath("$.allergies[0].designation", is("allergy1"))).andDo(print())
        .andReturn();

    //check that there is no new medication nor allergy saved
    verify(medicationService, never()).saveMedication(Mockito.any(Medication.class));
    verify(allergyService, Mockito.never()).saveAllergy(Mockito.any(Allergy.class));

    //check that new existed medication was added to medicalRecord and old medication was removed
    // and allergies didn't change
    ArgumentCaptor<MedicalRecord> medicalRecordCaptor =
        ArgumentCaptor.forClass(MedicalRecord.class);
    verify(medicalRecordService, times(1)).saveMedicalRecord(medicalRecordCaptor.capture());
    assertThat(medicalRecordCaptor.getValue().getMedications().toString())
        .isEqualTo("[Medication(idMedication=2, designation=medication2, posology=200mg)]");
    assertThat(medicalRecordCaptor.getValue().getAllergies().toString())
        .isEqualTo("[Allergy(idAllergy=1, designation=allergy1)]");

  }



  @ParameterizedTest
  @CsvSource({"newMedication,100mg",
              "medication1,200mg"})
  @Order(13)
  void putMedicalRecord_whenChangedMedicationWithNewOneReturn200(ArgumentsAccessor args)
      throws Exception {

    //Given
    when(medicalRecordService.getMedicalRecordById(Mockito.anyLong()))
        .thenReturn(Optional.of(mockMedicalRecord1));

    when(fireStationService.getFireStationMappedToAddress(Mockito.anyString()))
        .thenReturn(Optional.empty());

    when(medicationService.getMedicationByDesignationAndPosology(Mockito.anyString(),
        Mockito.anyString())).thenReturn(Optional.empty());

    when(allergyService.getAllergyByDesignation(Mockito.anyString()))
        .thenReturn(Optional.of(mockAllergy1));

    // mockMedicalRecordWithId represents the return of medicalRecord.save()
    HashSet<Medication> mockMedicationsWithId = new HashSet<Medication>();
    HashSet<Allergy> mockAllergiesWithId = new HashSet<Allergy>();

    // new medication
    Medication mockMedicationWithId =
        new Medication(3L, args.getString(0), args.getString(1), null);
    Allergy mockAllergyWithId = new Allergy(1L, "allergy1", null);

    mockMedicationsWithId.add(mockMedicationWithId);
    mockAllergiesWithId.add(mockAllergyWithId);

    MedicalRecord mockMedicalRecordWithId = new MedicalRecord();
    mockMedicalRecordWithId.setAllergies(mockAllergiesWithId);
    mockMedicalRecordWithId.setIdMedicalRecord(1L);
    mockMedicalRecordWithId.setMedications(mockMedicationsWithId);
    mockMedicalRecordWithId.setPerson(mockPerson1);

    when(medicalRecordService.saveMedicalRecord(Mockito.any(MedicalRecord.class)))
        .thenReturn(mockMedicalRecordWithId);

    // change of medication wihtoutId
    Medication mockNewMedicationWithoutId =
        new Medication(null, args.getString(0), args.getString(1), null);
    mockMedicationsWithoutId.clear();
    mockMedicationsWithoutId.add(mockNewMedicationWithoutId);
    mockMedicalRecordWithoutId.setMedications(mockMedicationsWithoutId);

    ObjectMapper mapper = mapperBuilder.build();

    //when & then
    MvcResult result = mockMvc.perform(put("/medicalRecord/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockMedicalRecordWithoutId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.idMedicalRecord", is(1)))
        .andExpect(jsonPath("$.person.idPerson", is(1)))
        .andExpect(jsonPath("$.person.address", is("26 av maréchal Foch")))
        .andExpect(jsonPath("$.person.firstName", is("Dorian")))
        .andExpect(jsonPath("$.person.lastName", is("Delaval")))
        .andExpect(jsonPath("$.person.birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$.person.city", is("Cassis")))
        .andExpect(jsonPath("$.person.zip", is(13260)))
        .andExpect(jsonPath("$.person.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.person.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.medications.length()", is(1)))
        .andExpect(jsonPath("$.medications[0].idMedication", notNullValue()))
        .andExpect(jsonPath("$.medications[0].designation", is(args.getString(0))))
        .andExpect(jsonPath("$.medications[0].posology", is(args.getString(1))))
        .andExpect(jsonPath("$.allergies[0].idAllergy", is(1)))
        .andExpect(jsonPath("$.allergies[0].designation", is("allergy1"))).andDo(print())
        .andReturn();

    // check the new medication was saved in database
    ArgumentCaptor<Medication> medicationCaptor = ArgumentCaptor.forClass(Medication.class);

    verify(medicationService, times(1)).saveMedication(medicationCaptor.capture());
    assertThat(medicationCaptor.getValue().getIdMedication()).isNull();
    assertThat(medicationCaptor.getValue().getDesignation()).isEqualTo(args.getString(0));
    assertThat(medicationCaptor.getValue().getPosology()).isEqualTo(args.getString(1));

    //check that there is no new  allergy saved
    verify(allergyService, Mockito.never()).saveAllergy(Mockito.any(Allergy.class));

    //check that new existed medication was added to medicalRecord and old medication was removed
    // and allergies didn't change
    ArgumentCaptor<MedicalRecord> medicalRecordCaptor =
        ArgumentCaptor.forClass(MedicalRecord.class);
    verify(medicalRecordService, times(1)).saveMedicalRecord(medicalRecordCaptor.capture());
    assertThat(medicalRecordCaptor.getValue().getMedications().size()).isEqualTo(1);

    assertThat(medicalRecordCaptor.getValue().getMedications().toString()).isEqualTo(
        "[Medication(idMedication=null, designation=" + args.getString(0) + ", posology="
            + args.getString(1) + ")]");
    assertThat(medicalRecordCaptor.getValue().getAllergies().toString())
        .isEqualTo("[Allergy(idAllergy=1, designation=allergy1)]");

  }


  @Test
  @Order(14)
  void putMedicalRecord_whenChangeAllergiesWithNewOne_thenReturn200()
      throws Exception {

    //Given
    when(medicalRecordService.getMedicalRecordById(Mockito.anyLong()))
        .thenReturn(Optional.of(mockMedicalRecord1));

    when(fireStationService.getFireStationMappedToAddress(Mockito.anyString()))
        .thenReturn(Optional.empty());

    // medication don't change from existed MedicalRecord

    when(medicationService.getMedicationByDesignationAndPosology(Mockito.anyString(),
        Mockito.anyString())).thenReturn(Optional.of(mockMedication1));

    when(allergyService.getAllergyByDesignation(Mockito.anyString()))
        .thenReturn(Optional.empty());

    // mockMedicalRecordWithId represents the return of medicalRecord.save()
    HashSet<Medication> mockMedicationsWithId = new HashSet<Medication>();
    HashSet<Allergy> mockAllergiesWithId = new HashSet<Allergy>();

    Medication mockMedicationWithId = mockMedication1;

    Allergy mockAllergyWithId = new Allergy(2L, "newAllergy", null);

    mockMedicationsWithId.add(mockMedicationWithId);
    mockAllergiesWithId.add(mockAllergyWithId);

    MedicalRecord mockMedicalRecordWithId = new MedicalRecord();
    mockMedicalRecordWithId.setAllergies(mockAllergiesWithId);
    mockMedicalRecordWithId.setIdMedicalRecord(1L);
    mockMedicalRecordWithId.setMedications(mockMedicationsWithId);
    mockMedicalRecordWithId.setPerson(mockPerson1);

    when(medicalRecordService.saveMedicalRecord(Mockito.any(MedicalRecord.class)))
        .thenReturn(mockMedicalRecordWithId);

    // change of medication wihtoutId
    Allergy mockAllergyWithoutId = new Allergy(null, "newAllergy", null);
    mockAllergiesWithoutId.clear();
    mockAllergiesWithoutId.add(mockAllergyWithoutId);
    mockMedicalRecordWithoutId.setAllergies(mockAllergiesWithoutId);

    ObjectMapper mapper = mapperBuilder.build();

    //when & then
    MvcResult result = mockMvc.perform(put("/medicalRecord/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockMedicalRecordWithoutId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.idMedicalRecord", is(1)))
        .andExpect(jsonPath("$.person.idPerson", is(1)))
        .andExpect(jsonPath("$.person.address", is("26 av maréchal Foch")))
        .andExpect(jsonPath("$.person.firstName", is("Dorian")))
        .andExpect(jsonPath("$.person.lastName", is("Delaval")))
        .andExpect(jsonPath("$.person.birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$.person.city", is("Cassis")))
        .andExpect(jsonPath("$.person.zip", is(13260)))
        .andExpect(jsonPath("$.person.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.person.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.medications.length()", is(1)))
        .andExpect(jsonPath("$.medications[0].idMedication", notNullValue()))
        .andExpect(jsonPath("$.medications[0].designation", is("medication1")))
        .andExpect(jsonPath("$.medications[0].posology", is("100mg")))
        .andExpect(jsonPath("$.allergies[0].idAllergy", notNullValue()))
        .andExpect(jsonPath("$.allergies[0].designation", is("newAllergy"))).andDo(print())
        .andReturn();

    // check the new allergy was saved in database
    ArgumentCaptor<Allergy> allergyCaptor = ArgumentCaptor.forClass(Allergy.class);

    verify(allergyService, times(1)).saveAllergy(allergyCaptor.capture());
    assertThat(allergyCaptor.getValue().getIdAllergy()).isNull();
    assertThat(allergyCaptor.getValue().getDesignation()).isEqualTo("newAllergy");

    //check that there is no new  Medication saved
    verify(medicationService, Mockito.never()).saveMedication(Mockito.any(Medication.class));

    //check that new existed Allergy was added to medicalRecord and old Allergy was removed
    // and Medications didn't change
    ArgumentCaptor<MedicalRecord> medicalRecordCaptor =
        ArgumentCaptor.forClass(MedicalRecord.class);

    verify(medicalRecordService, times(1)).saveMedicalRecord(medicalRecordCaptor.capture());

    assertThat(medicalRecordCaptor.getValue().getMedications().size()).isEqualTo(1);

    assertThat(medicalRecordCaptor.getValue().getMedications().toString()).isEqualTo(
        "[Medication(idMedication=1, designation=medication1, posology=100mg)]");

    assertThat(medicalRecordCaptor.getValue().getAllergies().toString())
        .isEqualTo("[Allergy(idAllergy=null, designation=newAllergy)]");

  }

  @Test
  @Order(15)
  void putMedicalRecord_whenChangeAllergiesWithExistedOne_thenReturn200()
      throws Exception {

    //Given
    when(medicalRecordService.getMedicalRecordById(Mockito.anyLong()))
        .thenReturn(Optional.of(mockMedicalRecord1));

    when(fireStationService.getFireStationMappedToAddress(Mockito.anyString()))
        .thenReturn(Optional.empty());

    when(medicationService.getMedicationByDesignationAndPosology(Mockito.anyString(),
        Mockito.anyString())).thenReturn(Optional.of(mockMedication1));

    when(allergyService.getAllergyByDesignation(Mockito.anyString()))
        .thenReturn(Optional.of(mockAllergy2));

    // mockMedicalRecordWithId represents the return of medicalRecord.save()
    HashSet<Medication> mockMedicationsWithId = new HashSet<Medication>();
    HashSet<Allergy> mockAllergiesWithId = new HashSet<Allergy>();

    Medication mockMedicationWithId = mockMedication1;
    Allergy mockAllergyWithId = mockAllergy2;
    mockMedicationsWithId.add(mockMedicationWithId);
    mockAllergiesWithId.add(mockAllergyWithId);

    MedicalRecord mockMedicalRecordWithId = new MedicalRecord();
    mockMedicalRecordWithId.setAllergies(mockAllergiesWithId);
    mockMedicalRecordWithId.setIdMedicalRecord(1L);
    mockMedicalRecordWithId.setMedications(mockMedicationsWithId);
    mockMedicalRecordWithId.setPerson(mockPerson1);


    when(medicalRecordService.saveMedicalRecord(Mockito.any(MedicalRecord.class)))
        .thenReturn(mockMedicalRecordWithId);

    // mockMedicalRecordWithoutId represents the requestBody: change medication only
    mockAllergiesWithoutId.clear();
    mockAllergyWithoutId.setIdAllergy(null);
    mockAllergyWithoutId.setDesignation("allergy2");
    mockAllergyWithoutId.setMedicalRecords(null);
    mockAllergiesWithoutId.add(mockAllergyWithoutId);
    mockMedicalRecordWithoutId.setAllergies(mockAllergiesWithoutId);

    ObjectMapper mapper = mapperBuilder.build();

    //when & then
    MvcResult result = mockMvc.perform(put("/medicalRecord/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mockMedicalRecordWithoutId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.idMedicalRecord", is(1)))
        .andExpect(jsonPath("$.person.idPerson", is(1)))
        .andExpect(jsonPath("$.person.address", is("26 av maréchal Foch")))
        .andExpect(jsonPath("$.person.firstName", is("Dorian")))
        .andExpect(jsonPath("$.person.lastName", is("Delaval")))
        .andExpect(jsonPath("$.person.birthDate", is("12/27/1976")))
        .andExpect(jsonPath("$.person.city", is("Cassis")))
        .andExpect(jsonPath("$.person.zip", is(13260)))
        .andExpect(jsonPath("$.person.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.person.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.medications.length()", is(1)))
        .andExpect(jsonPath("$.medications[0].idMedication", is(1)))
        .andExpect(jsonPath("$.medications[0].designation", is("medication1")))
        .andExpect(jsonPath("$.medications[0].posology", is("100mg")))
        .andExpect(jsonPath("$.allergies[0].idAllergy", is(2)))
        .andExpect(jsonPath("$.allergies[0].designation", is("allergy2"))).andDo(print())
        .andReturn();

    //check that there is no new medication nor allergy saved
    verify(medicationService, never()).saveMedication(Mockito.any(Medication.class));
    verify(allergyService, Mockito.never()).saveAllergy(Mockito.any(Allergy.class));

    //check that new existed allergy was added to medicalRecord and old allergy was removed
    // and medications didn't change
    ArgumentCaptor<MedicalRecord> medicalRecordCaptor =
        ArgumentCaptor.forClass(MedicalRecord.class);

    verify(medicalRecordService, times(1)).saveMedicalRecord(medicalRecordCaptor.capture());

    assertThat(medicalRecordCaptor.getValue().getMedications().toString())
        .isEqualTo("[Medication(idMedication=1, designation=medication1, posology=100mg)]");
    assertThat(medicalRecordCaptor.getValue().getAllergies().toString())
        .isEqualTo("[Allergy(idAllergy=2, designation=allergy2)]");

  }

  @Test
  void deleteMedicalRecord_whenValidInput_thenReturn200() throws Exception {

    //Given
    when(
        medicalRecordService.getMedicalRecordByNames(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(Optional.of(mockMedicalRecord1));

    ObjectMapper mapper = mapperBuilder.build();

    //when& then
    mockMvc.perform(delete("/medicalRecord/{firtstName}/{lastName}", "Delaval", "Dorian"))
        .andExpect(status().isOk()).andDo(print());

  }

  @Test
  void deleteMedicalRecord_whenMedicalRecordNotFound_thenReturn404() throws Exception {

    //Given
    ObjectMapper mapper = mapperBuilder.build();
    when(
        medicalRecordService.getMedicalRecordByNames(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(Optional.empty());

    //when& then
    MvcResult result =
        mockMvc.perform(delete("/medicalRecord/{firtstName}/{lastName}", "Delaval", "John"))
            .andExpect(status().isNotFound()).andDo(print()).andReturn();

    assertThat(result.getResolvedException())
        .isInstanceOf(MedicalRecordNotFoundException.class);
    assertThat(result.getResolvedException().getMessage()).isEqualTo(
        "MedicalRecord was not found because lastname and firstname didn't match with anybody: "
            + "Please chose valid couple firstName/LastName");

  }

}
