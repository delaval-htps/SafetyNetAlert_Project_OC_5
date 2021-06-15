package com.safetynet.alert.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alert.model.Person;
import com.safetynet.alert.service.AllergyService;
import com.safetynet.alert.service.FireStationService;
import com.safetynet.alert.service.MedicalRecordService;
import com.safetynet.alert.service.MedicationService;
import com.safetynet.alert.service.PersonService;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;


@ExtendWith(MockitoExtension.class)
@PropertySource("classpath:application.properties")
class LoadDatabaseFromJsonTest {

  @Mock
  private PersonService personService;
  @Mock
  private FireStationService fireStationService;
  @Mock
  private MedicalRecordService medicalRecordService;
  @Mock
  private MedicationService medicationService;
  @Mock
  private AllergyService allergyService;

  @Mock
  private ObjectMapper objectMapper;
  @Mock
  private ResourceLoader resourceLoader;
  @Mock
  private Resource resource;

  private LogCaptor logCaptor =
      LogCaptor.forClass(LoadDatabaseProdFromJson.class);

  private static String[] expectedErrorMessages = {"File Data.json is not Found in resources",
                                                   "Reading Failure for File Data.json",
                                                   "Json's datas are not valid",
                                                   "File Data.json is missing to be parsed",
                                                   "problem to parse persons with objectMapper"};

  private LoadDatabaseProdFromJson classUnderTest;
  private String filepath;
  private File mockFileJson;

  @BeforeEach
  void setup() {

    filepath = "classpath:${filejson.test}";
    mockFileJson = new File(filepath);

    classUnderTest =
        new LoadDatabaseProdFromJson(objectMapper, resourceLoader, filepath);

  }

  @Test
  void loadDatabaseService_shouldNotPersistData_whenObjectMapperCantReadFireStation()
      throws IOException {
    // Given

    ObjectMapper mapper = new ObjectMapper();
    JsonNode mockJsonNodeRoot = mapper.readTree("{\"persons\":"
        + " [ { \"firstName\":\"John\"," + "\"lastName\":\"Boyd\","
        + " \"address\":\"1509 Culver St\"," + " \"city\":\"Culver\", "
        + "\"zip\":\"97451\"," + " \"phone\":\"841-874-6512\","
        + " \"email\":\"jaboyd@email.com\"}],"
        + "\"firestations\": [{ \"address\":\"1509 Culver St\",  \"station\":\"3\" }],"
        + "\"medicalrecords\": [" + "{ \"firstName\":\"John\","
        + " \"lastName\":\"Boyd\"," + " \"birthdate\":\"03/06/1984\", "
        + "\"medications\":[\"aznol:350mg\", " + "\"hydrapermazol:100mg\"],"
        + " \"allergies\":[\"nillacilan\"] }]}");
    //
    when(resourceLoader.getResource(Mockito.anyString())).thenReturn(resource);
    when(resource.getFile()).thenReturn(mockFileJson);

    when(objectMapper.readTree(Mockito.any(File.class))).thenReturn(mockJsonNodeRoot);

    when(objectMapper.readValue(Mockito.anyString(),
        Mockito.eq(Person.class))).thenThrow(JsonProcessingException.class);

    // When
    boolean result = classUnderTest.loadDatabaseFromSource();

    // Then
    assertFalse(result);
    assertThat(logCaptor.getErrorLogs()).containsExactly(expectedErrorMessages[4]);
    // assertThatThrownBy(() -> objectMapper
    // .readValue(Mockito.anyString(), Mockito.eq(Person.class)))
    // .isInstanceOfAny(JsonProcessingException.class);
    verify(objectMapper, times(1)).readValue(Mockito.anyString(),
        Mockito.eq(Person.class));

  }

  @Test
  void loadDatabaseService_shouldNotPersistData_whenObjectMapperCantReadPerson()
      throws IOException {
    // Given

    ObjectMapper mapper = new ObjectMapper();
    JsonNode mockJsonNodeRoot = mapper.readTree("{\"persons\":"
        + " [ { \"firstName\":\"John\"," + "\"lastName\":\"Boyd\","
        + " \"address\":\"1509 Culver St\"," + " \"city\":\"Culver\", "
        + "\"zip\":\"97451\"," + " \"phone\":\"841-874-6512\","
        + " \"email\":\"jaboyd@email.com\"}],"
        + "\"firestations\": [{ \"address\":\"1509 Culver St\",  \"station\":\"3\" }],"
        + "\"medicalrecords\": [" + "{ \"firstName\":\"John\","
        + " \"lastName\":\"Boyd\"," + " \"birthdate\":\"03/06/1984\", "
        + "\"medications\":[\"aznol:350mg\", " + "\"hydrapermazol:100mg\"],"
        + " \"allergies\":[\"nillacilan\"] }]}");

    when(resourceLoader.getResource(Mockito.anyString())).thenReturn(resource);
    when(resource.getFile()).thenReturn(mockFileJson);
    when(objectMapper.readTree(Mockito.any(File.class))).thenReturn(mockJsonNodeRoot);

    when(objectMapper.readValue(Mockito.anyString(),
        Mockito.eq(Person.class))).thenThrow(JsonProcessingException.class);

    // When
    boolean result = classUnderTest.loadDatabaseFromSource();

    // Then
    assertFalse(result);
    assertThat(logCaptor.getErrorLogs()).containsExactly(expectedErrorMessages[4]);
    // assertThatThrownBy(() -> objectMapper
    // .readValue(Mockito.anyString(), Mockito.eq(Person.class)))
    // .isInstanceOfAny(JsonProcessingException.class);
    verify(objectMapper, times(1)).readValue(Mockito.anyString(),
        Mockito.eq(Person.class));

  }

  @Test
  void loadDatabaseService_shouldNotPersistData_whenObjectMapperNoContentIsFound()
      throws IOException {

    // Given
    // when(classPathResource.getFile()).thenReturn(mockFile);
    when(resourceLoader.getResource(Mockito.anyString())).thenReturn(resource);
    when(resource.getFile()).thenReturn(mockFileJson);
    when(objectMapper.readTree(Mockito.any(File.class))).thenReturn(null);

    // When
    boolean result = classUnderTest.loadDatabaseFromSource();

    // Then
    assertFalse(result);

  }

  @Test
  void loadDatabaseService_shouldThrowsException_whenFileDataJSonIsNullOrNotFound()
      throws IOException {

    // Given
    when(resourceLoader.getResource(Mockito.anyString())).thenReturn(resource);
    when(resource.getFile()).thenReturn(mockFileJson);
    when(resourceLoader.getResource(Mockito.anyString())
        .getFile()).thenThrow(FileNotFoundException.class);

    // When

    boolean result = classUnderTest.loadDatabaseFromSource();
    // Then

    assertFalse(result);
    assertThat(logCaptor.getErrorLogs()).containsExactly(expectedErrorMessages[0]);
    // assertThatThrownBy(() -> resourceLoader.getResource(Mockito.anyString())
    // .getFile()).isInstanceOf(FileNotFoundException.class);

  }

  @Test
  void loadDatabaseService_shouldThrowsException_whenFileDataJsonNotReadable()
      throws IOException {

    // Given
    when(resourceLoader.getResource(Mockito.anyString())).thenReturn(resource);
    when(resource.getFile()).thenThrow(IOException.class);

    // When
    boolean result = classUnderTest.loadDatabaseFromSource();
    // Then
    assertFalse(result);
    assertThat(logCaptor.getErrorLogs()).containsExactly(expectedErrorMessages[1]);
    assertThatThrownBy((
    ) -> resourceLoader.getResource(Mockito.anyString())
        .getFile()).isInstanceOf(IOException.class);

  }

  @Test
  void loadDatabaseService_shouldThrowsException_whenDatasJsonNotValid()
      throws JsonProcessingException, IOException {
    // Given

    when(resourceLoader.getResource(Mockito.anyString())).thenReturn(resource);
    when(resource.getFile()).thenReturn(mockFileJson);
    when(objectMapper.readTree(Mockito.any(File.class)))
        .thenThrow(JsonProcessingException.class);

    // When
    boolean result = classUnderTest.loadDatabaseFromSource();

    // Then
    assertFalse(result);
    assertThat(logCaptor.getErrorLogs()).containsExactly(expectedErrorMessages[2]);

  }

  @Test
  void loadDatabaseService_shouldThrowsException_whenDatasJsonIsMissed()
      throws JsonProcessingException, IOException {

    // Given
    when(resourceLoader.getResource(Mockito.anyString())).thenReturn(resource);
    when(resource.getFile()).thenReturn(mockFileJson);
    when(objectMapper.readTree(Mockito.any(File.class))).thenThrow(IOException.class);

    // When
    boolean result = classUnderTest.loadDatabaseFromSource();

    // Then
    assertFalse(result);
    assertThat(logCaptor.getErrorLogs()).containsExactly(expectedErrorMessages[3]);

    // assertThatThrownBy(() -> resourceLoader.getResource(Mockito.anyString())
    // .getFile()).isInstanceOfAny(IOException.class);
  }
}
