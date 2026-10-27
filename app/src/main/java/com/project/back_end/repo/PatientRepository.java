package com.project.back_end.repo;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    public Patient findByEmail(String email);
     
    public Patient findByEmailOrPhone(String email, String phone);

}

