package com.project.back_end.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.project.back_end.models.Doctor;
import java.util.List;
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    public Doctor findByEmail(String email);

    @Query("SELECT d FROM Doctor d " +
           "WHERE d.name LIKE CONCAT('%', :name, '%')")
    public List<Doctor> findByNameLike(@Param("name") String name);

    @Query("SELECT d FROM Doctor d " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND LOWER(d.specialty) = LOWER(:specialty)")
    public List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
            @Param("name") String name,
            @Param("specialty") String specialty);

    public List<Doctor> findBySpecialtyIgnoreCase(String specialty);

    @Query("SELECT DISTINCT d FROM Doctor d LEFT JOIN FETCH d.availableTimes")
List<Doctor> findAllWithAvailableTimes();
}