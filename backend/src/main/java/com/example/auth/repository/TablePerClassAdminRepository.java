package com.example.auth.repository;

import com.example.auth.entity.inheritance.strategy.TablePerClassAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TablePerClassAdminRepository extends JpaRepository<TablePerClassAdmin, Long> {
    // Additional query methods can be added here if needed
}