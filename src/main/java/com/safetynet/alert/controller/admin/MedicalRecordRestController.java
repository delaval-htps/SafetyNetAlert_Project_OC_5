package com.safetynet.alert.controller.admin;

import com.safetynet.alert.model.MedicalRecord;
import com.safetynet.alert.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MedicalRecordRestController {

  @Autowired
  MedicalRecordService medicalRecordService;

  @GetMapping("/medicalRecords")
  public Iterable<MedicalRecord> getMedicalRecords() {
    return medicalRecordService.getMedicalRecords();
  }
}
