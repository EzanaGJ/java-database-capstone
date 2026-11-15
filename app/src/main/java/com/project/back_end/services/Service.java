
package com.project.back_end.services;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.DTO.Login;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.format.DateTimeFormatter;

@org.springframework.stereotype.Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Service(
            TokenService tokenService,
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            DoctorService doctorService,
            PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public ResponseEntity<Map<String, String>> validateToken(
            String token, String user) {
        Map<String, String> response = new HashMap<>();

        try {
            if (!tokenService.validateToken(token, user)) {
                response.put("error", "Invalid or expired token");
                return ResponseEntity.status(401).body(response);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(response);
        }
    }

    public ResponseEntity<Map<String, String>> validateAdmin(
            Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();

        try {
            Admin admin = adminRepository.findByUsername(
                    receivedAdmin.getUsername());

            if (admin == null
                    || !admin.getPassword().equals(receivedAdmin.getPassword())) {
                response.put("error", "Invalid username or password");
                return ResponseEntity.status(401).body(response);
            }

            String token = tokenService.generateToken(admin.getUsername());
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Internal server error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    public Map<String, Object> filterDoctor(
            String name, String specialty, String time) {

        boolean hasName = name != null
                && !name.isBlank()
                && !name.equalsIgnoreCase("all");

        boolean hasSpecialty = specialty != null
                && !specialty.isBlank()
                && !specialty.equalsIgnoreCase("all");

        boolean hasTime = time != null
                && !time.isBlank()
                && !time.equalsIgnoreCase("all");

        if (hasName && hasSpecialty && hasTime) {
            return doctorService.filterDoctorsByNameSpecilityandTime(
                    name, specialty, time);
        }

        if (hasName && hasSpecialty) {
            return doctorService.filterDoctorByNameAndSpecility(
                    name, specialty);
        }

        if (hasName && hasTime) {
            return doctorService.filterDoctorByNameAndTime(name, time);
        }

        if (hasSpecialty && hasTime) {
            return doctorService.filterDoctorByTimeAndSpecility(
                    specialty, time);
        }

        if (hasName) {
            return doctorService.filterDoctorByNameAndTime(name, "");
        }

        if (hasSpecialty) {
            return doctorService.filterDoctorBySpecility(specialty);
        }

        if (hasTime) {
            return doctorService.filterDoctorsByTime(time);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorService.getDoctors());

        return response;
    }

    public int validateAppointment(Appointment appointment) {
        try {
            if (appointment == null
                    || appointment.getDoctor() == null
                    || appointment.getDoctor().getId() == null
                    || appointment.getAppointmentTime() == null) {
                return 0;
            }

            Doctor doctor = doctorRepository
                    .findById(appointment.getDoctor().getId())
                    .orElse(null);

            if (doctor == null) {
                return -1;
            }

            List<String> availableSlots = doctorService.getDoctorAvailability(
                    doctor.getId(),
                    appointment.getAppointmentDate());

            String appointmentTime = appointment.getAppointmentTimeOnly()
                    .format(DateTimeFormatter.ofPattern("HH:mm"));

            return availableSlots != null
                    && availableSlots.contains(appointmentTime) ? 1 : 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean validatePatient(Patient patient) {
        return patientRepository.findByEmailOrPhone(
                patient.getEmail(), patient.getPhone()) == null;
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(
            Login login) {
        Map<String, String> response = new HashMap<>();

        try {
            Patient patient = patientRepository.findByEmail(login.getEmail());

            if (patient == null
                    || !patient.getPassword().equals(login.getPassword())) {
                response.put("error", "Invalid email or password");
                return ResponseEntity.status(401).body(response);
            }

            String token = tokenService.generateToken(patient.getEmail());
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Internal server error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterPatient(
            String condition, String name, String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = tokenService.extractIdentifier(token);

            Patient patient = patientRepository.findByEmail(email);

            if (patient == null) {
                response.put("error", "Patient not found");
                return ResponseEntity.status(404).body(response);
            }

            ResponseEntity<Map<String, Object>> result;

            boolean hasCondition = condition != null && !condition.isBlank();
            boolean hasName = name != null && !name.isBlank();

            if (hasCondition && hasName) {
                result = patientService.filterByDoctorAndCondition(
                        condition, name, patient.getId());
            } else if (hasCondition) {
                result = patientService.filterByCondition(
                        condition, patient.getId());
            } else if (hasName) {
                result = patientService.filterByDoctor(
                        name, patient.getId());
            } else {
                result = patientService.getPatientAppointment(
                        patient.getId(), token);
            }

            return result;

        } catch (Exception e) {
            response.put("error", "Internal server error");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
