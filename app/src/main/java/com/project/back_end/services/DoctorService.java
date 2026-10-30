
package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;

@org.springframework.stereotype.Service
@Transactional
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 1. Get available time slots for a doctor on a specific date
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);

        if (doctor == null || doctor.getAvailableTimes() == null) {
            return List.of();
        }

        List<String> bookedTimes = appointmentRepository.findAll().stream()
                .filter(a -> a.getDoctor() != null
                        && a.getDoctor().getId().equals(doctorId))
                .filter(a -> a.getAppointmentTime() != null
                        && a.getAppointmentTime().toLocalDate().equals(date))
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .collect(Collectors.toList());

        return doctor.getAvailableTimes().stream()
                .filter(time -> !bookedTimes.contains(time))
                .collect(Collectors.toList());
    }

    // 2. Save a new doctor
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1;
            }

            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // 3. Update an existing doctor
    public int updateDoctor(Doctor doctor) {
        try {
            if (doctor.getId() == null
                    || !doctorRepository.existsById(doctor.getId())) {
                return -1;
            }

            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // 4. Retrieve all doctors with their available times
    public List<Doctor> getDoctors() {
        return doctorRepository.findAllWithAvailableTimes();
    }

    // 5. Delete a doctor and associated appointments
    public int deleteDoctor(long id) {
        try {
            if (!doctorRepository.existsById(id)) {
                return -1;
            }

            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // 6. Validate doctor login credentials
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();

        try {
            Doctor doctor = doctorRepository.findByEmail(login.getEmail());

            if (doctor == null
                    || !doctor.getPassword().equals(login.getPassword())) {
                response.put("error", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(response);
            }

            String token = tokenService.generateToken(login.getEmail());
            response.put("token", token);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Unable to validate doctor login");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // 7. Find doctors by name
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();

        List<Doctor> doctors = (name == null || name.isBlank())
                ? doctorRepository.findAll()
                : doctorRepository.findByNameLike("%" + name + "%");

        response.put("doctors", doctors);
        return response;
    }

    // 8. Filter by name, specialty, and time
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(
            String name, String specialty, String amOrPm) {

        List<Doctor> doctors =
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                        name, specialty);

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    // 9. Filter by name and time
    public Map<String, Object> filterDoctorByNameAndTime(
            String name, String amOrPm) {

        List<Doctor> doctors =
                doctorRepository.findByNameLike("%" + name + "%");

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    // 10. Filter by name and specialty
    public Map<String, Object> filterDoctorByNameAndSpecility(
            String name, String specilty) {

        List<Doctor> doctors =
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                        name, specilty);

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return response;
    }

    // 11. Filter by specialty and time
    public Map<String, Object> filterDoctorByTimeAndSpecility(
            String specilty, String amOrPm) {

        List<Doctor> doctors =
                doctorRepository.findBySpecialtyIgnoreCase(specilty);

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    // 12. Filter by specialty
    public Map<String, Object> filterDoctorBySpecility(String specilty) {
        List<Doctor> doctors =
                doctorRepository.findBySpecialtyIgnoreCase(specilty);

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return response;
    }

    // 13. Filter by time
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> doctors = doctorRepository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    // 14. Helper method: filter doctors by AM or PM availability
    private List<Doctor> filterDoctorByTime(
            List<Doctor> doctors, String amOrPm) {

        if (amOrPm == null || amOrPm.isBlank()) {
            return doctors;
        }

        String period = amOrPm.trim().toUpperCase();

        if (!period.equals("AM") && !period.equals("PM")) {
            return List.of();
        }

        return doctors.stream()
                .filter(doctor -> doctor.getAvailableTimes() != null)
                .filter(doctor -> doctor.getAvailableTimes().stream()
                        .anyMatch(time -> isInPeriod(time, period)))
                .collect(Collectors.toList());
    }

    private boolean isInPeriod(String time, String period) {
        try {
            LocalTime parsedTime = LocalTime.parse(time);
            int hour = parsedTime.getHour();

            return period.equals("AM") ? hour < 12 : hour >= 12;
        } catch (Exception e) {
            try {
                LocalDateTime parsedDateTime = LocalDateTime.parse(time);
                int hour = parsedDateTime.getHour();

                return period.equals("AM") ? hour < 12 : hour >= 12;
            } catch (Exception ignored) {
                return false;
            }
        }
    }
}
