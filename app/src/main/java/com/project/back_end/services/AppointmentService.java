
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
import java.util.ArrayList;
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

    // Book an appointment using the authenticated patient's token
    @Transactional
    public int bookAppointment(Appointment appointment, String token) {

        if (appointment == null
                || appointment.getDoctor() == null
                || appointment.getDoctor().getId() == null
                || appointment.getAppointmentTime() == null) {

            throw new IllegalArgumentException(
                    "Doctor and appointment time are required");
        }

        // Get the authenticated patient from the JWT
        String email = tokenService.extractIdentifier(token);

        Patient patient = patientRepository.findByEmail(email);

        if (patient == null) {
            throw new IllegalArgumentException("Patient not found");
        }

        // Get the actual doctor from the database
        Long doctorId = appointment.getDoctor().getId();

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Doctor not found"));

        // Attach managed entities to the appointment
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        // New appointments start as Scheduled
        appointment.setStatus(0);

        // Save appointment
        appointmentRepository.saveAndFlush(appointment);

        System.out.println("Appointment successfully saved.");
        System.out.println("Patient ID: " + patient.getId());
        System.out.println("Doctor ID: " + doctor.getId());
        System.out.println("Appointment time: "
                + appointment.getAppointmentTime());

        return 1;
    }

    // Update an appointment
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(
            Appointment appointment) {

        Map<String, String> response = new HashMap<>();

        try {
            Optional<Appointment> existing =
                    appointmentRepository.findById(appointment.getId());

            if (existing.isEmpty()) {
                response.put("message", "Appointment not found");

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(response);
            }

            int result = service.validateAppointment(appointment);

            if (result == -1) {
                response.put("message", "Doctor not found");

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(response);
            }

            if (result == 0) {
                response.put("message",
                        "Appointment time is not available");

                return ResponseEntity.badRequest().body(response);
            }

            appointmentRepository.saveAndFlush(appointment);

            response.put("message",
                    "Appointment updated successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();

            response.put("message", "Error updating appointment");

            return ResponseEntity.internalServerError().body(response);
        }
    }

    // Cancel an appointment
    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(
            long id, String token) {

        Map<String, String> response = new HashMap<>();

        try {
            Optional<Appointment> result =
                    appointmentRepository.findById(id);

            if (result.isEmpty()) {
                response.put("message", "Appointment not found");

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(response);
            }

            String email = tokenService.extractIdentifier(token);

            Patient patient = patientRepository.findByEmail(email);

            if (patient == null
                    || !patient.getId().equals(
                            result.get().getPatient().getId())) {

                response.put("message",
                        "You cannot cancel this appointment");

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(response);
            }

            appointmentRepository.delete(result.get());

            response.put("message",
                    "Appointment cancelled successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();

            response.put("message", "Error cancelling appointment");

            return ResponseEntity.internalServerError().body(response);
        }
    }

    // Get appointments for a doctor on a specific date
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
                                    date.plusDays(1)
                                            .atStartOfDay()
                                            .minusNanos(1));

            // Apply patient-name filter only when a name is provided
            if (pname != null
                    && !pname.isBlank()
                    && !pname.equalsIgnoreCase("all")) {

                appointments = appointments.stream()
                        .filter(a -> a.getPatient() != null
                                && a.getPatient().getName() != null
                                && a.getPatient().getName()
                                        .toLowerCase()
                                        .contains(pname.toLowerCase()))
                        .toList();
            }

            response.put("appointments", appointments);

            return response;

        } catch (Exception e) {
            e.printStackTrace();

            response.put("error", "Error retrieving appointments");

            return response;
        }
    }

    // Get ALL appointments for a doctor
    @Transactional(readOnly = true)
    public Map<String, Object> getAllAppointments(
            String pname, String token) {

        Map<String, Object> response = new HashMap<>();

        try {
            String email = tokenService.extractIdentifier(token);

            Doctor doctor = doctorRepository.findByEmail(email);

            if (doctor == null) {
                response.put("error", "Doctor not found");
                return response;
            }

            List<Appointment> appointments =
                    appointmentRepository.findAll()
                            .stream()
                            .filter(a -> a.getDoctor() != null
                                    && doctor.getId().equals(
                                            a.getDoctor().getId()))
                            .filter(a -> pname == null
                                    || pname.isBlank()
                                    || pname.equalsIgnoreCase("all")
                                    || (a.getPatient() != null
                                    && a.getPatient().getName() != null
                                    && a.getPatient().getName()
                                            .toLowerCase()
                                            .contains(pname.toLowerCase())))
                            .toList();

            // Build a simple response instead of returning full entities.
            // This prevents lazy-loading errors when Jackson converts to JSON.
            List<Map<String, Object>> appointmentList =
                    new ArrayList<>();

            for (Appointment appointment : appointments) {

                Map<String, Object> patientData = new HashMap<>();

                patientData.put("id",
                        appointment.getPatient().getId());
                patientData.put("name",
                        appointment.getPatient().getName());
                patientData.put("phone",
                        appointment.getPatient().getPhone());
                patientData.put("email",
                        appointment.getPatient().getEmail());

                Map<String, Object> doctorData = new HashMap<>();

                doctorData.put("id",
                        appointment.getDoctor().getId());

                Map<String, Object> appointmentData = new HashMap<>();

                appointmentData.put("id", appointment.getId());
                appointmentData.put("patient", patientData);
                appointmentData.put("doctor", doctorData);
                appointmentData.put("appointmentTime",
                        appointment.getAppointmentTime());
                appointmentData.put("appointmentDate",
                        appointment.getAppointmentDate());
                appointmentData.put("appointmentTimeOnly",
                        appointment.getAppointmentTimeOnly());
                appointmentData.put("status",
                        appointment.getStatus());

                appointmentList.add(appointmentData);
            }

            response.put("appointments", appointmentList);

            return response;

        } catch (Exception e) {
            e.printStackTrace();

            response.put("error", "Error retrieving appointments");

            return response;
        }
    }
}
