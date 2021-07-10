package com.safetynet.alert.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alert.CommandLineRunnerTaskExcecutor;
import com.safetynet.alert.database.LoadDataStrategyFactory;
import com.safetynet.alert.database.StrategyName;
import com.safetynet.alert.exceptions.medicalrecord.MedicalRecordAlreadyExistedException;
import com.safetynet.alert.exceptions.medicalrecord.MedicalRecordChangedNamesException;
import com.safetynet.alert.exceptions.medicalrecord.MedicalRecordNotFoundException;
import com.safetynet.alert.model.Allergy;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestMethodOrder(OrderAnnotation.class)
class MedicalRecordRestControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MedicalRecordService medicalRecordService;

  @Autowired
  private PersonService personService;

  @Autowired
  private FireStationService fireStationService;

  @Autowired
  private MedicationService medicationService;

  @Autowired
  private AllergyService allergyService;

  @Autowired
  private Jackson2ObjectMapperBuilder mapperBuilder;

  @MockBean
  private CommandLineRunnerTaskExcecutor clrte;

  @Autowired
  private LoadDataStrategyFactory loadDataStrategyFactory;

  private MedicalRecord medicalRecordTest;
  private Person personTest;
  private Medication medicationTest;
  private Allergy allergyTest;
  private Set<Medication> medicationsTest;
  private Set<Allergy> allergiesTest;
  private SimpleDateFormat sdf;

  @BeforeEach
  void setUp() throws Exception {

    sdf = new SimpleDateFormat("MM/dd/yyyy");

    loadDataStrategyFactory.findStrategy(StrategyName.StrategyTest).loadDatabaseFromSource();

    personTest = new Person(null, "Bernard", "Delaval", sdf.parse("12/27/1976"),
                            "26 av maréchal Foch", "Cassis", 13260,
                            "061-846-0160", "delaval.htps@gmail.com",
                            null, null);

    medicationTest = new Medication(null, "medication1", "100mg", null);

    allergyTest = new Allergy(null, "allergy1", null);

    medicationsTest = new HashSet<Medication>();
    medicationsTest.add(medicationTest);

    allergiesTest = new HashSet<Allergy>();
    allergiesTest.add(allergyTest);

    medicalRecordTest = new MedicalRecord(null, personTest, medicationsTest, allergiesTest);

  }

  @Test
  @Order(1)
  void getMedicalRecords() throws Exception {

    mockMvc.perform(get("/medicalRecord")).andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(6)))
        .andExpect(jsonPath("$[0].idMedicalRecord", is(1)))
        .andExpect(jsonPath("$[0].person.idPerson", notNullValue()))
        .andExpect(jsonPath("$[0].person.address", is("1509 Culver St")))
        .andExpect(jsonPath("$[0].person.firstName", is("John")))
        .andExpect(jsonPath("$[0].person.lastName", is("Boyd")))
        .andExpect(jsonPath("$[0].person.birthDate", is("03/06/1984")))
        .andExpect(jsonPath("$[0].person.city", is("Culver")))
        .andExpect(jsonPath("$[0].person.zip", is(97451)))
        .andExpect(jsonPath("$[0].person.phone", is("841-874-6512")))
        .andExpect(jsonPath("$[0].person.email", is("jaboyd@email.com")))
        .andExpect(jsonPath("$[0].medications.length()", is(2)))
        .andExpect(jsonPath("$[0].medications[0].idMedication", notNullValue()))
        .andExpect(jsonPath("$[0].medications[0].designation", is("aznol")))
        .andExpect(jsonPath("$[0].medications[0].posology", is("350mg")))
        .andExpect(jsonPath("$[0].medications[1].idMedication", notNullValue()))
        .andExpect(jsonPath("$[0].medications[1].designation", is("hydrapermazol")))
        .andExpect(jsonPath("$[0].medications[1].posology", is("100mg")))
        .andExpect(jsonPath("$[0].allergies[0].idAllergy", notNullValue()))
        .andExpect(jsonPath("$[0].allergies[0].designation", is("nillacilan")))
        .andDo(print());

  }

  @Test
  @Order(2)
  void getMedicalRecordById_whenValidId_thenReturn200() throws Exception {

    //when &then
    mockMvc.perform(get("/medicalRecord/{id}", 1))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.idMedicalRecord", is(1)))
        .andExpect(jsonPath("$.person.idPerson", notNullValue()))
        .andExpect(jsonPath("$.person.address", is("1509 Culver St")))
        .andExpect(jsonPath("$.person.firstName", is("John")))
        .andExpect(jsonPath("$.person.lastName", is("Boyd")))
        .andExpect(jsonPath("$.person.birthDate", is("03/06/1984")))
        .andExpect(jsonPath("$.person.city", is("Culver")))
        .andExpect(jsonPath("$.person.zip", is(97451)))
        .andExpect(jsonPath("$.person.phone", is("841-874-6512")))
        .andExpect(jsonPath("$.person.email", is("jaboyd@email.com")))
        .andExpect(jsonPath("$.medications.length()", is(2)))
        .andExpect(jsonPath("$.medications[0].idMedication", notNullValue()))
        .andExpect(jsonPath("$.medications[0].designation", is("aznol")))
        .andExpect(jsonPath("$.medications[0].posology", is("350mg")))
        .andExpect(jsonPath("$.medications[1].idMedication", is(2)))
        .andExpect(jsonPath("$.medications[1].designation", is("hydrapermazol")))
        .andExpect(jsonPath("$.medications[1].posology", is("100mg")))
        .andExpect(jsonPath("$.allergies[0].idAllergy", notNullValue()))
        .andExpect(jsonPath("$.allergies[0].designation", is("nillacilan")));

  }

  @Test
  @Order(3)
  void getMedicalRecordById_whenNotFoundId_thenReturn404() throws Exception {

    //when &then
    MvcResult result = mockMvc.perform(get("/medicalRecord/{id}", 7))
        .andExpect(status().isNotFound()).andReturn();

    assertThat(result.getResolvedException().getMessage())
        .isEqualTo("MedicalRecord with id:7 was not found!");


  }

  @Test
  @Order(4)
  void postMedicalRecord_whenValidInputWithNoPersistedDatas_thenReturn201() throws Exception {

    //given
    ObjectMapper mapper = mapperBuilder.build();


    //when & then
    mockMvc.perform(post("/medicalRecord").accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(medicalRecordTest)))

        .andExpect(status().isCreated())
        .andExpect(redirectedUrlPattern("http://*/medicalRecord/7"))
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.idMedicalRecord", notNullValue()))
        .andExpect(jsonPath("$.person.idPerson", notNullValue()))
        .andExpect(jsonPath("$.person.address", is("26 av maréchal Foch")))
        .andExpect(jsonPath("$.person.firstName", is("Bernard")))
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
        .andExpect(jsonPath("$.allergies[0].designation", is("allergy1"))).andDo(print());

    MedicalRecord savedMedicalRecord =
        medicalRecordService.getMedicalRecordJoinAllById(7L).get();
    assertThat(savedMedicalRecord.getPerson().getMedicalRecord().getIdMedicalRecord())
        .isNotNull();
    // TODO lazy initialiszation person/fireStations
    //    assertThat(savedMedicalRecord.getPerson().getFireStations()).isNull();


  }

  @Test
  @Order(5)
  // TODO lazy initialiszation person/fireStations
  void postMedicalRecord_whenExistedPersonWithoutMedicalRecord_thenReturn201()
      throws Exception {

    //given
    Optional<Person> personWithoutMedicalRecord = personService.getPersonById(8L);
    Person currentPerson = personWithoutMedicalRecord.get();

    MedicalRecord postMedicalRecord = new MedicalRecord();
    postMedicalRecord.setPerson(currentPerson);
    postMedicalRecord.getPerson().setIdPerson(null);
    postMedicalRecord.setMedications(medicationsTest);
    postMedicalRecord.setAllergies(allergiesTest);

    ObjectMapper mapper = mapperBuilder.build();
    //when & then
    mockMvc.perform(post("/medicalRecord").accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(postMedicalRecord)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.idMedicalRecord", notNullValue()))
        .andExpect(jsonPath("$.person.idPerson", notNullValue()))
        .andExpect(jsonPath("$.person.address", is("1509 Av marechal foch")))
        .andExpect(jsonPath("$.person.firstName", is("Dorian")))
        .andExpect(jsonPath("$.person.lastName", is("Delaval")))
        .andExpect(jsonPath("$.person.city", is("Culver")))
        .andExpect(jsonPath("$.person.zip", is(97451)))
        .andExpect(jsonPath("$.person.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.person.email", is("dd@email.com")))
        .andExpect(jsonPath("$.medications.length()", is(1)))
        .andExpect(jsonPath("$.medications[0].idMedication", notNullValue()))
        .andExpect(jsonPath("$.medications[0].designation", is("medication1")))
        .andExpect(jsonPath("$.medications[0].posology", is("100mg")))
        .andExpect(jsonPath("$.allergies[0].idAllergy", notNullValue()))
        .andExpect(jsonPath("$.allergies[0].designation", is("allergy1"))).andDo(print());

    // check if person was correctly mapped to new medicalRecord
    assertThat(personService.getPersonById(8L).get().getMedicalRecord().getIdMedicalRecord())
        .isNotNull();
    //check if medicalRecord was correctly mapped to the existed person
    assertThat(postMedicalRecord.getPerson()).isEqualTo(currentPerson);

  }

  @Test
  @Order(6)
  // TODO lazy initialiszation person/fireStations
  void postMedicalRecord_whenMedicalRecordAlreadyExisted_thenReturn400() throws Exception {

    //given
    ObjectMapper mapper = mapperBuilder.build();

    // use a special query with inner join (see repository )to be able to retrieve
    //Person , Medications, Allergies from medicalRecord and avoid lazyInitializeException
    Optional<MedicalRecord> mrTest = medicalRecordService.getMedicalRecordJoinAllById(1L);

    //when & then
    mockMvc.perform(post("/medicalRecord").accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(mrTest.get())))
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

    //when & then
    MvcResult result = mockMvc.perform(put("/medicalRecord/{id}", 7)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(medicalRecordTest)))

        .andExpect(status().isNotFound())
        .andDo(print()).andReturn();

    assertThat(result.getResolvedException())
        .isInstanceOf(MedicalRecordNotFoundException.class);
    assertThat(result.getResolvedException().getMessage()).isEqualTo(
        "MedicalRecord with id: 7 was not found ! please chose a existed medicalRecord.");

  }

  @ParameterizedTest
  @CsvSource({"Bernard, Baudouin",
              "John, Delaval",
              "Bernard,",
              ",Delaval"})
  @Order(8)
  void putMedicalRecord_whenMedicalRecordPersonChangedNames_thenReturn400(
      ArgumentsAccessor args) throws Exception {

    //given
    ObjectMapper mapper = mapperBuilder.build();
    personTest.setFirstName(args.getString(0));
    personTest.setLastName(args.getString(1));

    //when & then
    MvcResult result = mockMvc.perform(put("/medicalRecord/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(medicalRecordTest)))

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

    // change of few fields in this MedicalRecord without lastName,firstName,address
    personTest.setFirstName("John");
    personTest.setLastName("Boyd");
    personTest.setAddress("1509 Culver St");
    personTest.setCity("Culver");

    medicationsTest.clear();
    medicationsTest.add(new Medication(null, "aznol", "350mg", null));
    medicationsTest.add(new Medication(null, "hydrapermazol", "100mg", null));

    MedicalRecord currentMedicalRecord = new MedicalRecord();
    currentMedicalRecord.setMedications(medicationsTest);

    currentMedicalRecord.setPerson(personTest);
    currentMedicalRecord.setAllergies(allergiesTest);

    ObjectMapper mapper = mapperBuilder.build();

    //when & then
    mockMvc.perform(put("/medicalRecord/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(currentMedicalRecord)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.idMedicalRecord", is(1)))
        .andExpect(jsonPath("$.person.idPerson", is(1)))
        .andExpect(jsonPath("$.person.address", is("1509 Culver St")))
        .andExpect(jsonPath("$.person.firstName", is("John")))
        .andExpect(jsonPath("$.person.lastName", is("Boyd")))
        .andExpect(jsonPath("$.person.city", is("Culver")))
        .andExpect(jsonPath("$.person.zip", is(13260)))
        .andExpect(jsonPath("$.person.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.person.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.medications.length()", is(2)))
        .andExpect(jsonPath("$.medications[*].idMedication", hasItems(1, 2)))
        .andExpect(
            jsonPath("$.medications[*].designation", hasItems("aznol", "hydrapermazol")))
        .andExpect(jsonPath("$.medications[*].posology", hasItems("350mg", "100mg")))
        .andExpect(jsonPath("$.allergies[0].idAllergy", is(4)))
        .andExpect(jsonPath("$.allergies[0].designation", is("allergy1"))).andDo(print());

  }

  @Test
  @Order(10)
  void putMedicalRecord_whenChangeAddressNotMappedByFireStation_thenReturn200()
      throws Exception {

    //given


    // No change in this MedicalRecord for lastName,firstName
    //but we change the address to check if fireStation will be correctly update
    personTest.setFirstName("John");
    personTest.setLastName("Boyd");
    personTest.setAddress("AddressNotMapped");

    medicationsTest.clear();
    medicationsTest.add(new Medication(null, "aznol", "350mg", null));
    medicationsTest.add(new Medication(null, "hydrapermazol", "100mg", null));

    MedicalRecord currentMedicalRecord = new MedicalRecord();
    currentMedicalRecord.setPerson(personTest);
    currentMedicalRecord.setMedications(medicationsTest);
    currentMedicalRecord.setAllergies(allergiesTest);

    ObjectMapper mapper = mapperBuilder.build();

    //when & then
    mockMvc.perform(put("/medicalRecord/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(currentMedicalRecord)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.idMedicalRecord", is(1)))
        .andExpect(jsonPath("$.person.idPerson", is(1)))
        .andExpect(jsonPath("$.person.address", is("AddressNotMapped")))
        .andExpect(jsonPath("$.person.firstName", is("John")))
        .andExpect(jsonPath("$.person.lastName", is("Boyd")))
        .andExpect(jsonPath("$.person.city", is("Cassis")))
        .andExpect(jsonPath("$.person.zip", is(13260)))
        .andExpect(jsonPath("$.person.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.person.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.medications.length()", is(2)))
        .andExpect(jsonPath("$.medications[*].idMedication", hasItems(1, 2)))
        .andExpect(
            jsonPath("$.medications[*].designation", hasItems("aznol", "hydrapermazol")))
        .andExpect(jsonPath("$.medications[*].posology", hasItems("350mg", "100mg")))
        .andExpect(jsonPath("$.allergies[0].idAllergy", is(4)))
        .andExpect(jsonPath("$.allergies[0].designation", is("allergy1"))).andDo(print());

    // use of join fetch query with fireStation cause of one to many in fetch Lazy
    // to verify if change of address doesn't change the fireStation for Person
    assertThat(personService.getPersonJoinFireStationById(1L)).isEmpty();

  }

  @Test
  @Order(11)
  void putMedicalRecord_whenChangeAddressMappedByFireStation_thenReturn200()
      throws Exception {

    //given

    // No change in this MedicalRecord for lastName,firstName
    //but we change the address to check if fireStation will be correctly update
    personTest.setFirstName("John");
    personTest.setLastName("Boyd");
    personTest.setAddress("29 15th St");

    medicationsTest.clear();
    medicationsTest.add(new Medication(null, "aznol", "350mg", null));
    medicationsTest.add(new Medication(null, "hydrapermazol", "100mg", null));

    MedicalRecord currentMedicalRecord = new MedicalRecord();
    currentMedicalRecord.setPerson(personTest);
    currentMedicalRecord.setMedications(medicationsTest);
    currentMedicalRecord.setAllergies(allergiesTest);

    ObjectMapper mapper = mapperBuilder.build();

    //when & then
    mockMvc.perform(put("/medicalRecord/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(currentMedicalRecord)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.idMedicalRecord", is(1)))
        .andExpect(jsonPath("$.person.idPerson", is(1)))
        .andExpect(jsonPath("$.person.address", is("29 15th St")))
        .andExpect(jsonPath("$.person.firstName", is("John")))
        .andExpect(jsonPath("$.person.lastName", is("Boyd")))
        .andExpect(jsonPath("$.person.city", is("Cassis")))
        .andExpect(jsonPath("$.person.zip", is(13260)))
        .andExpect(jsonPath("$.person.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.person.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.medications.length()", is(2)))
        .andExpect(jsonPath("$.medications[*].idMedication", hasItems(1, 2)))
        .andExpect(
            jsonPath("$.medications[*].designation", hasItems("aznol", "hydrapermazol")))
        .andExpect(jsonPath("$.medications[*].posology", hasItems("350mg", "100mg")))
        .andExpect(jsonPath("$.allergies[0].idAllergy", is(4)))
        .andExpect(jsonPath("$.allergies[0].designation", is("allergy1"))).andDo(print());

    // use of join fetch query with fireStation cause of one to many in fetch Lazy
    // to verify if change of address doesn't change the fireStation for Person
    Optional<Person> updatedPerson = personService.getPersonJoinFireStationById(1L);
    assertThat(updatedPerson).isNotEmpty();
    //assertThat(updatedPerson.get().getFireStations().getIdFireStation()).isEqualTo(2L);

  }

  @Test
  @Order(12)
  void putMedicalRecord_whenChangeMedicationWithExistedOne_thenReturn200()
      throws Exception {

    //Given
    // No change in this MedicalRecord for lastName,firstName
    personTest.setFirstName("John");
    personTest.setLastName("Boyd");

    MedicalRecord currentMedicalRecord = new MedicalRecord();
    currentMedicalRecord.setPerson(personTest);

    Medication saveNewMedication =
        medicationService
            .saveMedication(new Medication(null, "newExistedMedication", "100mg", null));

    medicationsTest.clear();
    medicationsTest.add(saveNewMedication);
    currentMedicalRecord.setMedications(medicationsTest);
    allergyTest.setDesignation("nillacilan");
    currentMedicalRecord.setAllergies(allergiesTest);

    ObjectMapper mapper = mapperBuilder.build();

    //when & then
    mockMvc.perform(put("/medicalRecord/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(currentMedicalRecord)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.idMedicalRecord", is(1)))
        .andExpect(jsonPath("$.person.idPerson", is(1)))
        .andExpect(jsonPath("$.person.address", is("26 av maréchal Foch")))
        .andExpect(jsonPath("$.person.firstName", is("John")))
        .andExpect(jsonPath("$.person.lastName", is("Boyd")))
        .andExpect(jsonPath("$.person.city", is("Cassis")))
        .andExpect(jsonPath("$.person.zip", is(13260)))
        .andExpect(jsonPath("$.person.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.person.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.medications.length()", is(1)))
        .andExpect(jsonPath("$.medications[0].idMedication", notNullValue()))
        .andExpect(jsonPath("$.medications[0].designation", is("newExistedMedication")))
        .andExpect(jsonPath("$.medications[0].posology", is("100mg")))
        .andExpect(jsonPath("$.allergies[0].idAllergy", is(1)))
        .andExpect(jsonPath("$.allergies[0].designation", is("nillacilan"))).andDo(print());

    //check that old medications are not mapped with MedicalRecord and are not deleted
    Iterable<Medication> medicationNotMappedByMedicalRecord =
        medicationService.getMedicationNotMappedByMedicalRecord();

    medicationNotMappedByMedicalRecord.forEach(medication -> {

      assertTrue(medication.getDesignation().equals("aznol")
          || medication.getDesignation().equals("hydrapermazol"));

      assertThat(medication.getMedicalRecords()).isNullOrEmpty();
    });

    //check that not new Allergy was created
    assertThat(allergyService.getAllergies()).hasSize(3);

  }

  @ParameterizedTest
  @CsvSource({"newMedication,100mg",
              "medication1,200mg"})
  @Order(13)
  void putMedicalRecord_whenChangedMedicationWithNewOneReturn200(ArgumentsAccessor args)
      throws Exception {

    //Given
    MedicalRecord currentMedicalRecord = new MedicalRecord();

    personTest.setFirstName("John");
    personTest.setLastName("Boyd");
    currentMedicalRecord.setPerson(personTest);

    medicationsTest.clear();
    Medication newMedication =
        new Medication(null, args.getString(0), args.getString(1), null);
    medicationsTest.add(newMedication);
    currentMedicalRecord.setMedications(medicationsTest);

    allergyTest.setDesignation("nillacilan");
    currentMedicalRecord.setAllergies(allergiesTest);

    ObjectMapper mapper = mapperBuilder.build();

    //when & then
    MvcResult result = mockMvc.perform(put("/medicalRecord/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(currentMedicalRecord)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.idMedicalRecord", is(1)))
        .andExpect(jsonPath("$.person.idPerson", is(1)))
        .andExpect(jsonPath("$.person.address", is("26 av maréchal Foch")))
        .andExpect(jsonPath("$.person.firstName", is("John")))
        .andExpect(jsonPath("$.person.lastName", is("Boyd")))
        .andExpect(jsonPath("$.person.city", is("Cassis")))
        .andExpect(jsonPath("$.person.zip", is(13260)))
        .andExpect(jsonPath("$.person.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.person.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.medications.length()", is(1)))
        .andExpect(jsonPath("$.medications[0].idMedication", notNullValue()))
        .andExpect(jsonPath("$.medications[0].designation", is(args.getString(0))))
        .andExpect(jsonPath("$.medications[0].posology", is(args.getString(1))))
        .andExpect(jsonPath("$.allergies[0].idAllergy", is(1)))
        .andExpect(jsonPath("$.allergies[0].designation", is("nillacilan"))).andDo(print())
        .andReturn();

    // check the new medication was saved in database
    Optional<Medication> savedMedication = medicationService
        .getMedicationByDesignationAndPosology(args.getString(0), args.getString(1));
    assertThat(savedMedication.get().getIdMedication()).isPositive();

    //check that there is no new  allergy saved
    assertThat(allergyService.getAllergies()).hasSize(3);

    //check that old medications are not mapped with MedicalRecord and are not deleted
    Iterable<Medication> medicationNotMappedByMedicalRecord =
        medicationService.getMedicationNotMappedByMedicalRecord();

    medicationNotMappedByMedicalRecord.forEach(medication -> {

      assertTrue(medication.getDesignation().equals("aznol")
          || medication.getDesignation().equals("hydrapermazol"));

      assertThat(medication.getMedicalRecords()).isNullOrEmpty();
    });


  }

  @Test
  @Order(14)
  void putMedicalRecord_whenChangeAllergiesWithExistedOne_thenReturn200()
      throws Exception {

    //Given
    MedicalRecord currentMedicalRecord = new MedicalRecord();
    personTest.setFirstName("John");
    personTest.setLastName("Boyd");
    currentMedicalRecord.setPerson(personTest);

    medicationsTest.clear();
    medicationsTest.add(new Medication(null, "aznol", "350mg", null));
    medicationsTest.add(new Medication(null, "hydrapermazol", "100mg", null));
    currentMedicalRecord.setMedications(medicationsTest);

    Allergy saveNewAllergy =
        allergyService
            .saveAllergy(new Allergy(null, "newExistedAllergy", null));

    allergiesTest.clear();
    allergiesTest.add(saveNewAllergy);
    currentMedicalRecord.setAllergies(allergiesTest);

    ObjectMapper mapper = mapperBuilder.build();

    //when & then
    mockMvc.perform(put("/medicalRecord/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(currentMedicalRecord)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.idMedicalRecord", is(1)))
        .andExpect(jsonPath("$.person.idPerson", is(1)))
        .andExpect(jsonPath("$.person.address", is("26 av maréchal Foch")))
        .andExpect(jsonPath("$.person.firstName", is("John")))
        .andExpect(jsonPath("$.person.lastName", is("Boyd")))
        .andExpect(jsonPath("$.person.city", is("Cassis")))
        .andExpect(jsonPath("$.person.zip", is(13260)))
        .andExpect(jsonPath("$.person.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.person.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.medications.length()", is(2)))
        .andExpect(jsonPath("$.medications[*].idMedication", hasItems(1, 2)))
        .andExpect(
            jsonPath("$.medications[*].designation", hasItems("aznol", "hydrapermazol")))
        .andExpect(jsonPath("$.medications[*].posology", hasItems("350mg", "100mg")))
        .andExpect(jsonPath("$.allergies[0].idAllergy", notNullValue()))
        .andExpect(jsonPath("$.allergies[0].designation", is("newExistedAllergy")))
        .andDo(print());

    //check that old allergies are not mapped with MedicalRecord and are not deleted
    Iterable<Allergy> allergiesNotMappedByMedicalRecord =
        allergyService.getAllergiesNotMappedByMedicalRecord();

    allergiesNotMappedByMedicalRecord.forEach(allergy -> {

      assertTrue(allergy.getDesignation().equals("nillacilan"));

      assertThat(allergy.getMedicalRecords()).isNullOrEmpty();
    });

    //check that not new medication was created
    assertThat(medicationService.getMedications()).hasSize(6);


  }

  @Test
  @Order(15)
  void putMedicalRecord_whenChangeAllergiesWithNewOne_thenReturn200()
      throws Exception {

    //Given
    MedicalRecord currentMedicalRecord = new MedicalRecord();

    personTest.setFirstName("John");
    personTest.setLastName("Boyd");
    currentMedicalRecord.setPerson(personTest);

    Allergy newAllergy = new Allergy(null, "newAllergy", null);
    allergiesTest.clear();
    allergiesTest.add(newAllergy);
    currentMedicalRecord.setAllergies(allergiesTest);

    medicationsTest.clear();
    medicationsTest.add(new Medication(null, "aznol", "350mg", null));
    medicationsTest.add(new Medication(null, "hydrapermazol", "100mg", null));
    currentMedicalRecord.setMedications(medicationsTest);

    ObjectMapper mapper = mapperBuilder.build();

    //when & then
    mockMvc.perform(put("/medicalRecord/{id}", 1)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(currentMedicalRecord)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(4)))
        .andExpect(jsonPath("$.idMedicalRecord", is(1)))
        .andExpect(jsonPath("$.person.idPerson", is(1)))
        .andExpect(jsonPath("$.person.address", is("26 av maréchal Foch")))
        .andExpect(jsonPath("$.person.firstName", is("John")))
        .andExpect(jsonPath("$.person.lastName", is("Boyd")))
        .andExpect(jsonPath("$.person.city", is("Cassis")))
        .andExpect(jsonPath("$.person.zip", is(13260)))
        .andExpect(jsonPath("$.person.phone", is("061-846-0160")))
        .andExpect(jsonPath("$.person.email", is("delaval.htps@gmail.com")))
        .andExpect(jsonPath("$.medications.length()", is(2)))
        .andExpect(jsonPath("$.medications[*].idMedication", hasItems(1, 2)))
        .andExpect(
            jsonPath("$.medications[*].designation", hasItems("aznol", "hydrapermazol")))
        .andExpect(jsonPath("$.medications[*].posology", hasItems("350mg", "100mg")))
        .andExpect(jsonPath("$.allergies[0].idAllergy", notNullValue()))
        .andExpect(jsonPath("$.allergies[0].designation", is("newAllergy")))
        .andDo(print());

    //check that old allergies are not mapped with MedicalRecord and are not deleted
    Iterable<Allergy> allergiesNotMappedByMedicalRecord =
        allergyService.getAllergiesNotMappedByMedicalRecord();

    allergiesNotMappedByMedicalRecord.forEach(allergy -> {

      assertTrue(allergy.getDesignation().equals("nillacilan"));

      assertThat(allergy.getMedicalRecords()).isNullOrEmpty();
    });

    //check that not new medication was created
    assertThat(medicationService.getMedications()).hasSize(6);


  }


  @Test
  @Order(12)
  void deleteMedicalRecord_whenValidInput_thenReturn200() throws Exception {

    //Given
    ObjectMapper mapper = mapperBuilder.build();

    //when& then
    mockMvc.perform(delete("/medicalRecord/{firtstName}/{lastName}", "Boyd", "John"))
        .andExpect(status().isOk()).andDo(print());

  }

  @Test
  @Order(13)
  void deleteMedicalRecord_whenMedicalRecordNotFound_thenReturn404() throws Exception {

    //Given
    ObjectMapper mapper = mapperBuilder.build();

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
