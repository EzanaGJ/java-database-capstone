package com.project.back_end.repo;

public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    public List<Prescription> findByAppointmentId(Long appointmentId)
}

