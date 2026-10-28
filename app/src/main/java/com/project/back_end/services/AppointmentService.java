package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.repo.DoctorRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@org.springframework.stereotype.Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;
    private final Service service;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            TokenService tokenService,
            Service service) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.service = service;
    }

    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(
            Appointment appointment) {

        Map<String, String> response = new HashMap<>();

        try {
            Optional<Appointment> existing =
                    appointmentRepository.findById(appointment.getId());

            if (existing.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            int result = service.validateAppointment(appointment);

            if (result == -1) {
                response.put("message", "Doctor not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (result == 0) {
                response.put("message", "Appointment time is not available");
                return ResponseEntity.badRequest().body(response);
            }

            appointmentRepository.save(appointment);
            response.put("message", "Appointment updated successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error updating appointment");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(
            long id, String token) {

        Map<String, String> response = new HashMap<>();

        try {
            Optional<Appointment> result = appointmentRepository.findById(id);

            if (result.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            String email = tokenService.extractIdentifier(token);
            Patient patient = patientRepository.findByEmail(email);

            if (patient == null
                    || !patient.getId().equals(result.get().getPatient().getId())) {
                response.put("message", "You cannot cancel this appointment");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            appointmentRepository.delete(result.get());
            response.put("message", "Appointment cancelled successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Error cancelling appointment");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAppointment(
            String pname, LocalDate date, String token) {

        Map<String, Object> response = new HashMap<>();

        try {
            String email = tokenService.extractIdentifier(token);
            Doctor doctor = doctorRepository.findByEmail(email);

            if (doctor == null) {
                response.put("error", "Doctor not found");
                return response;
            }

            List<Appointment> appointments =
                    appointmentRepository
                            .findByDoctorIdAndAppointmentTimeBetween(
                                    doctor.getId(),
                                    date.atStartOfDay(),
                                    date.plusDays(1).atStartOfDay().minusNanos(1));

            if (pname != null && !pname.isBlank()) {
                appointments = appointments.stream()
                        .filter(a -> a.getPatient().getName()
                                .toLowerCase()
                                .contains(pname.toLowerCase()))
                        .toList();
            }

            response.put("appointments", appointments);
            return response;

        } catch (Exception e) {
            response.put("error", "Error retrieving appointments");
            return response;
        }
    }
}
