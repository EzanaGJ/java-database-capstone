
package com.project.back_end.services;

import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;

import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.HashMap;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    public ResponseEntity<Map<String, String>> savePrescription(
            Prescription prescription) {
        Map<String, String> response = new HashMap<>();

        try {
            prescriptionRepository.save(prescription);
            response.put("message", "Prescription saved");
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            response.put("error", "Internal server error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> getPrescription(
            Long appointmentId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Prescription prescription =
                    prescriptionRepository.findByAppointmentId(appointmentId);

            response.put("prescription", prescription);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Internal server error");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
