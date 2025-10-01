package com.example.auth.repository;

import com.example.auth.entity.inheritance.strategy.TablePerClassAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TablePerClassAuthorRepository extends JpaRepository<TablePerClassAuthor, Long> {
    // Additional query methods can be added here if needed
}