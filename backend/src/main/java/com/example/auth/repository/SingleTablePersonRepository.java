package com.example.auth.repository;

import com.example.auth.entity.inheritance.strategy.SingleTablePerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SingleTablePersonRepository extends JpaRepository<SingleTablePerson, Long> {
    // Additional query methods can be added here if needed
}