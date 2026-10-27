
package com.project.back_end.repo;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.doctor d " +
           "WHERE d.id = :doctorId " +
           "AND a.appointmentTime BETWEEN :start AND :end")
    public List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
            Long doctorId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.doctor d " +
           "LEFT JOIN FETCH a.patient p " +
           "WHERE d.id = :doctorId " +
           "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%')) " +
           "AND a.appointmentTime BETWEEN :start AND :end")
    public List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            Long doctorId, String patientName,
            LocalDateTime start, LocalDateTime end);

    @Modifying
    @Transactional
    public void deleteAllByDoctorId(Long doctorId);

    public List<Appointment> findByPatientId(Long patientId);

    public List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(
            Long patientId, int status);

    @Query("SELECT a FROM Appointment a " +
           "WHERE LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId")
    public List<Appointment> filterByDoctorNameAndPatientId(
            String doctorName, Long patientId);

    @Query("SELECT a FROM Appointment a " +
           "WHERE LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId " +
           "AND a.status = :status")
    public List<Appointment> filterByDoctorNameAndPatientIdAndStatus(
            String doctorName, Long patientId, int status);
}
