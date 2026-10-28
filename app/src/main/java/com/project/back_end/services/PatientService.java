
package com.project.back_end.services;

import com.project.back_end.models.Patient;
import com.project.back_end.models.Appointment;
import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.repo.AppointmentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientAppointment(
            Long id, String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = tokenService.getEmailFromToken(token);
            Patient patient = patientRepository.findByEmail(email).orElse(null);

            if (patient == null || !patient.getId().equals(id)) {
                response.put("error", "Unauthorized");
                return ResponseEntity.status(401).body(response);
            }

            List<Appointment> appointments =
                    appointmentRepository.findByPatientId(id);

            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(AppointmentDTO::new)
                    .toList();

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Internal server error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> filterByCondition(
            String condition, Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            int status;

            if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else {
                response.put("error", "Invalid condition");
                return ResponseEntity.badRequest().body(response);
            }

            List<Appointment> appointments =
                    appointmentRepository.findByPatientIdAndStatus(id, status);

            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(AppointmentDTO::new)
                    .toList();

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Internal server error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctor(
            String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Appointment> appointments =
                    appointmentRepository.findByPatientIdAndDoctorNameContainingIgnoreCase(
                            patientId, name);

            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(AppointmentDTO::new)
                    .toList();

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Internal server error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(
            String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();

        try {
            int status;

            if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else {
                response.put("error", "Invalid condition");
                return ResponseEntity.badRequest().body(response);
            }

            List<Appointment> appointments =
                    appointmentRepository
                            .findByPatientIdAndDoctorNameContainingIgnoreCaseAndStatus(
                                    patientId, name, status);

            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(AppointmentDTO::new)
                    .toList();

            response.put("appointments", appointmentDTOs);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Internal server error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = tokenService.getEmailFromToken(token);
            Patient patient = patientRepository.findByEmail(email).orElse(null);

            if (patient == null) {
                response.put("error", "Patient not found");
                return ResponseEntity.status(404).body(response);
            }

            response.put("patient", patient);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Internal server error");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
