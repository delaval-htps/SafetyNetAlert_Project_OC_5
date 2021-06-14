package com.safetynet.alert.service;

import com.safetynet.alert.model.MedicalRecord;
import com.safetynet.alert.repository.MedicalRecordRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.validation.Valid;

@Service
public class MedicalRecordService {

  @Autowired
  private MedicalRecordRepository medicalRecordRepository;
  //  @Autowired
  //  private PersonService personService;

  public Optional<MedicalRecord> getMedicalRecordById(Long id) {

    return medicalRecordRepository.findById(id);

  }

  public Iterable<MedicalRecord> getMedicalRecords() {

    return medicalRecordRepository.findAll();

  }


  public Optional<MedicalRecord> getMedicalRecordJoinAllById(long l) {

    return medicalRecordRepository.getOneJoinAllOtherById(l);

  }

  public Optional<MedicalRecord> getMedicalRecordByNames(@Valid String lastName,
      @Valid String firstName) {

    return medicalRecordRepository.getOneByNames(lastName, firstName);

  }

  public MedicalRecord saveMedicalRecord(MedicalRecord medicalRecord) {

    return medicalRecordRepository.save(medicalRecord);

  }

  public void deleteMedicalRecord(MedicalRecord medicalRecord) {

    medicalRecordRepository.delete(medicalRecord);

  }


}
