package com.example.auth.repository;

import com.example.auth.entity.inheritance.strategy.TablePerClassStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TablePerClassStudentRepository extends JpaRepository<TablePerClassStudent, Long> {
    // Additional query methods can be added here if needed
}