package com.project.back_end.repo;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    public Admin findbyUsername(String username);
}
