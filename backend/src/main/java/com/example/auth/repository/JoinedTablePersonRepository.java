package com.example.auth.repository;

import com.example.auth.entity.inheritance.strategy.JoinedTablePerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JoinedTablePersonRepository extends JpaRepository<JoinedTablePerson, Long> {
    // Additional query methods can be added here if needed
}