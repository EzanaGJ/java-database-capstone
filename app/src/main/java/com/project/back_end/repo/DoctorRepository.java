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
}