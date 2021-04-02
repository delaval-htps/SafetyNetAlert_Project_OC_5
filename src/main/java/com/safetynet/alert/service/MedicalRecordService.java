package com.safetynet.alert.service;

import com.safetynet.alert.model.MedicalRecord;
import com.safetynet.alert.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedicalRecordService {

  @Autowired
  private MedicalRecordRepository medicalRecordRepository;

  public MedicalRecord getMedicalRecordById(Long id) {
    return medicalRecordRepository.getOne(id);
  }

  public Iterable<MedicalRecord> getMedicalRecords() {
    return medicalRecordRepository.findAll();
  }

  public MedicalRecord saveMedicalRecord(MedicalRecord medicalRecord) {
    return medicalRecordRepository.save(medicalRecord);
  }
}
