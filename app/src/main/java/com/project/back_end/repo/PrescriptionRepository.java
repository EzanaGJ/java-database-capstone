package com.project.back_end.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.project.back_end.models.Prescription;
import java.util.List;

public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    public List<Prescription> findByAppointmentId(Long appointmentId);
}

