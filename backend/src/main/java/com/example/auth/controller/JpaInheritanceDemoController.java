package com.example.auth.controller;

import com.example.auth.entity.inheritance.strategy.*;
import com.example.auth.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller demonstrating JPA Inheritance Strategies:
 * 1. Single Table Inheritance (SINGLE_TABLE)
 * 2. Joined Table Inheritance (JOINED)  
 * 3. Table Per Class Inheritance (TABLE_PER_CLASS)
 * 
 * Each strategy has different database schema implications and performance characteristics.
 */
@RestController
@RequestMapping("/api/inheritance/jpa-strategies")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class JpaInheritanceDemoController {

    @Autowired
    private SingleTablePersonRepository singleTablePersonRepository;
    
    @Autowired
    private JoinedTablePersonRepository joinedTablePersonRepository;
    
    @Autowired
    private TablePerClassStudentRepository tpcStudentRepository;
    
    @Autowired
    private TablePerClassAuthorRepository tpcAuthorRepository;
    
    @Autowired
    private TablePerClassAdminRepository tpcAdminRepository;

    /**
     * GET /api/inheritance/jpa-strategies/overview
     * Comprehensive overview of all JPA inheritance strategies
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getJpaInheritanceOverview() {
        return ResponseEntity.ok(Map.of(
            "title", "JPA Inheritance Strategies Complete Guide",
            "description", "Comprehensive comparison of database-level inheritance mapping",
            "strategies", Map.of(
                "SINGLE_TABLE", Map.of(
                    "concept", "All entities in ONE table with discriminator column",
                    "pros", List.of(
                        "Fastest queries (no JOINs needed)",
                        "Simple schema - only one table",
                        "Polymorphic queries are efficient",
                        "Good for simple hierarchies"
                    ),
                    "cons", List.of(
                        "Nullable columns (child-specific fields)",
                        "Table can become very wide",
                        "Wasted space for null values",
                        "All fields must be nullable"
                    ),
                    "useCase", "Small hierarchies with similar field sets",
                    "tableStructure", "single_table_persons (id, person_type, name, email, student_id, major, gpa, author_id, affiliation, research_field, admin_id, department, admin_level)"
                ),
                "JOINED", Map.of(
                    "concept", "Parent table + separate child tables with FK relationships",
                    "pros", List.of(
                        "Normalized database structure",
                        "No nullable columns",
                        "Efficient storage (no wasted space)",
                        "Easy to add new entity types"
                    ),
                    "cons", List.of(
                        "Requires JOINs for polymorphic queries",
                        "More complex queries",
                        "Multiple tables to maintain",
                        "Slower performance for deep hierarchies"
                    ),
                    "useCase", "Complex hierarchies with different field sets",
                    "tableStructure", "joined_persons + joined_students + joined_authors + joined_admins (linked by FKs)"
                ),
                "TABLE_PER_CLASS", Map.of(
                    "concept", "Complete separate table for each concrete class",
                    "pros", List.of(
                        "No JOINs for type-specific queries",
                        "All fields can be NOT NULL",
                        "Independent table optimization",
                        "Best performance for single-type queries"
                    ),
                    "cons", List.of(
                        "Data duplication (parent fields repeated)",
                        "Complex polymorphic queries (UNIONs)",
                        "Schema changes affect multiple tables",
                        "Most complex for cross-type operations"
                    ),
                    "useCase", "Independent entity types with minimal polymorphic queries",
                    "tableStructure", "table_per_class_students + table_per_class_authors + table_per_class_admins (all contain complete fields)"
                )
            ),
            "examples", Map.of(
                "endpoints", List.of(
                    "GET /single-table - Single Table strategy demo",
                    "GET /joined-table - Joined Table strategy demo", 
                    "GET /table-per-class - Table Per Class strategy demo",
                    "GET /comparison - Side-by-side comparison",
                    "GET /query-patterns - Database query examples"
                )
            )
        ));
    }

    /**
     * GET /api/inheritance/jpa-strategies/single-table
     * Demonstrates Single Table Inheritance Strategy
     */
    @GetMapping("/single-table")
    public ResponseEntity<Map<String, Object>> demonstrateSingleTableInheritance() {
        // Create sample entities
        SingleTableStudent student = new SingleTableStudent("Alice", "Johnson", "alice.johnson@university.edu", "ST001", "Computer Science");
        student.setGpa(3.8);
        student.enrollInCourse("CS101");
        student.enrollInCourse("MATH201");
        student.enrollInCourse("PHYS101");

        SingleTableAuthor author = new SingleTableAuthor("Dr. Bob", "Smith", "bob.smith@research.com", "AU001", "MIT", "Artificial Intelligence");
        author.addPublication("Deep Learning in Computer Vision");
        author.addResearchArea("Machine Learning");
        author.addResearchArea("Computer Vision");
        author.updateCitations(150);

        SingleTableAdmin admin = new SingleTableAdmin("Carol", "Williams", "carol.williams@admin.com", "ADM001", "IT", "SYSTEM");
        admin.recordLogin();
        admin.addManagedArea("User Management");

        // 🚀 SAVE TO DATABASE
        student = singleTablePersonRepository.save(student);
        author = singleTablePersonRepository.save(author);
        admin = singleTablePersonRepository.save(admin);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "strategy", "SINGLE_TABLE Inheritance",
            "description", "All inheritance hierarchy entities stored in a single database table",
            "databaseSchema", Map.of(
                "tableName", "single_table_persons",
                "discriminatorColumn", "person_type (STUDENT/EMPLOYEE)",
                "allColumns", List.of(
                    "id (PK)", "person_type (discriminator)", "first_name", "last_name", 
                    "email", "phone_number", "created_at",
                    "student_id (nullable)", "major (nullable)", "gpa (nullable)", 
                    "year_of_study (nullable)", "credits_completed (nullable)",
                    "author_id (nullable)", "affiliation (nullable)", "research_field (nullable)",
                    "h_index (nullable)", "total_citations (nullable)", "first_publication_date (nullable)",
                    "admin_id (nullable)", "department (nullable)", "admin_level (nullable)",
                    "last_login (nullable)", "login_count (nullable)", "is_super_admin (nullable)",
                    "can_manage_users (nullable)", "can_manage_content (nullable)", "can_view_reports (nullable)"
                ),
                "note", "All child-specific columns must be nullable since they're in the same table"
            ),
            "sampleData", Map.of(
                "message", "✅ Successfully created and saved entities to PostgreSQL database",
                "student", Map.of(
                    "id", student.getId(),
                    "entity", student.getSingleTableInfo(),
                    "discrimination", "person_type = 'STUDENT'"
                ),
                "author", Map.of(
                    "id", author.getId(),
                    "entity", author.getSingleTableInfo(),
                    "discrimination", "person_type = 'AUTHOR'"
                ),
                "admin", Map.of(
                    "id", admin.getId(),
                    "entity", admin.getSingleTableInfo(),
                    "discrimination", "person_type = 'ADMIN'"
                )
            ),
            "queryExamples", Map.of(
                "allPersons", "SELECT * FROM single_table_persons",
                "studentsOnly", "SELECT * FROM single_table_persons WHERE person_type = 'STUDENT'",
                "authorsOnly", "SELECT * FROM single_table_persons WHERE person_type = 'AUTHOR'",
                "adminsOnly", "SELECT * FROM single_table_persons WHERE person_type = 'ADMIN'",
                "polymorphicQuery", "SELECT id, first_name, last_name, person_type FROM single_table_persons WHERE email LIKE '%@%.%'",
                "complexFilter", "SELECT * FROM single_table_persons WHERE (person_type = 'STUDENT' AND gpa > 3.5) OR (person_type = 'AUTHOR' AND h_index > 10) OR (person_type = 'ADMIN' AND is_super_admin = true)"
            ),
            "performanceCharacteristics", Map.of(
                "strengths", List.of("No JOINs required", "Fast polymorphic queries", "Simple table structure"),
                "weaknesses", List.of("Many nullable columns", "Potential for wide tables", "Wasted storage space")
            )
        ));
    }

    /**
     * GET /api/inheritance/jpa-strategies/joined-table
     * Demonstrates Joined Table Inheritance Strategy
     */
    @GetMapping("/joined-table")
    public ResponseEntity<Map<String, Object>> demonstrateJoinedTableInheritance() {
        // Create sample entities
        JoinedTableStudent student = new JoinedTableStudent("Carol", "Davis", "carol.davis@university.edu", "JT001", "Biology");
        student.setGpa(3.9);
        student.enrollInCourse("BIO101");
        student.enrollInCourse("CHEM201");

        JoinedTableAuthor author = new JoinedTableAuthor("Dr. David", "Wilson", "david.wilson@research.com", "AU002", "Stanford University", "Machine Learning");
        author.addPublication("Neural Networks for Pattern Recognition");
        author.addResearchArea("Artificial Intelligence");
        author.addCollaborator("Prof. Sarah Johnson");
        author.updateCitations(200);

        JoinedTableAdmin admin = new JoinedTableAdmin("Emma", "Brown", "emma.brown@admin.com", "ADM002", "Computer Science", "DEPARTMENT");
        admin.recordLogin();
        admin.addManagedArea("Department Users");

        // 🚀 SAVE TO DATABASE
        student = joinedTablePersonRepository.save(student);
        author = joinedTablePersonRepository.save(author);
        admin = joinedTablePersonRepository.save(admin);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "strategy", "JOINED Table Inheritance",
            "description", "Parent-child relationship with separate tables joined by foreign keys",
            "databaseSchema", Map.of(
                "parentTable", Map.of(
                    "name", "joined_persons",
                    "columns", List.of("id (PK)", "first_name", "last_name", "email", "phone_number", "created_at"),
                    "role", "Contains common fields for all person types"
                ),
                "childTables", List.of(
                    Map.of(
                        "name", "joined_students",
                        "columns", List.of("id (PK, FK to joined_persons)", "student_id", "major", "gpa", "year_of_study", "credits_completed"),
                        "relationship", "1:1 with joined_persons"
                    ),
                    Map.of(
                        "name", "joined_authors", 
                        "columns", List.of("id (PK, FK to joined_persons)", "author_id", "affiliation", "research_field", "h_index", "total_citations", "first_publication_date"),
                        "relationship", "1:1 with joined_persons"
                    ),
                    Map.of(
                        "name", "joined_admins", 
                        "columns", List.of("id (PK, FK to joined_persons)", "admin_id", "department", "admin_level", "last_login", "login_count", "is_super_admin", "permissions"),
                        "relationship", "1:1 with joined_persons"
                    )
                ),
                "joinStrategy", "Child table PK is also FK to parent table"
            ),
            "sampleData", Map.of(
                "student", Map.of(
                    "entity", student.getJoinedTableInfo(),
                    "dataDistribution", "Common fields in joined_persons, student fields in joined_students"
                ),
                "author", Map.of(
                    "entity", author.getJoinedTableInfo(),
                    "dataDistribution", "Common fields in joined_persons, author fields in joined_authors"
                ),
                "admin", Map.of(
                    "entity", admin.getJoinedTableInfo(),
                    "dataDistribution", "Common fields in joined_persons, admin fields in joined_admins"
                )
            ),
            "queryExamples", Map.of(
                "allPersons", "SELECT * FROM joined_persons",
                "studentsWithDetails", """
                    SELECT p.*, s.student_id, s.major, s.gpa 
                    FROM joined_persons p 
                    JOIN joined_students s ON p.id = s.id""",
                "authorsWithDetails", """
                    SELECT p.*, a.author_id, a.affiliation, a.research_field, a.h_index 
                    FROM joined_persons p 
                    JOIN joined_authors a ON p.id = a.id""",
                "adminsWithDetails", """
                    SELECT p.*, ad.admin_id, ad.department, ad.admin_level, ad.is_super_admin 
                    FROM joined_persons p 
                    JOIN joined_admins ad ON p.id = ad.id""",
                "polymorphicQuery", """
                    SELECT p.id, p.first_name, p.last_name, 'STUDENT' as type 
                    FROM joined_persons p 
                    JOIN joined_students s ON p.id = s.id
                    UNION ALL
                    SELECT p.id, p.first_name, p.last_name, 'AUTHOR' as type 
                    FROM joined_persons p 
                    JOIN joined_authors a ON p.id = a.id
                    UNION ALL
                    SELECT p.id, p.first_name, p.last_name, 'ADMIN' as type 
                    FROM joined_persons p 
                    JOIN joined_admins ad ON p.id = ad.id""",
                "complexFilter", """
                    SELECT p.*, s.gpa 
                    FROM joined_persons p 
                    JOIN joined_students s ON p.id = s.id 
                    WHERE s.gpa > 3.5 AND p.email LIKE '%@university.edu'"""
            ),
            "performanceCharacteristics", Map.of(
                "strengths", List.of("Normalized structure", "No nullable columns", "Efficient storage", "Clear separation of concerns"),
                "weaknesses", List.of("Requires JOINs", "More complex queries", "Multiple table maintenance")
            )
        ));
    }

    /**
     * GET /api/inheritance/jpa-strategies/table-per-class
     * Demonstrates Table Per Class Inheritance Strategy
     */
    @GetMapping("/table-per-class")
    public ResponseEntity<Map<String, Object>> demonstrateTablePerClassInheritance() {
        // Create sample entities
        TablePerClassStudent student = new TablePerClassStudent("Emma", "Brown", "emma.brown@university.edu", "TPC001", "Mathematics");
        student.setGpa(4.0);
        student.enrollInCourse("MATH301");
        student.enrollInCourse("MATH302");
        student.enrollInCourse("STAT201");
        student.addCredits(90);

        TablePerClassAuthor author = new TablePerClassAuthor("Dr. Frank", "Taylor", "frank.taylor@research.com", "AU003", "Harvard University", "Quantum Computing");
        author.addPublication("Quantum Algorithms for Machine Learning", "Nature");
        author.addResearchArea("Quantum Computing");
        author.addResearchArea("Theoretical Physics");
        author.updateCitations(300);
        author.attendConference("Quantum Computing Symposium");

        TablePerClassAdmin admin = new TablePerClassAdmin("Grace", "Miller", "grace.miller@admin.com", "ADM003", "Research", "CONTENT");
        admin.attemptLogin("192.168.1.100");
        admin.addManagedArea("Content Review");
        admin.enableTwoFactor();

        // 🚀 SAVE TO DATABASE
        student = tpcStudentRepository.save(student);
        author = tpcAuthorRepository.save(author);
        admin = tpcAdminRepository.save(admin);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "strategy", "TABLE_PER_CLASS Inheritance",
            "description", "Complete separate table for each concrete class with all fields duplicated",
            "databaseSchema", Map.of(
                "studentTable", Map.of(
                    "name", "table_per_class_students",
                    "columns", List.of(
                        "id (PK)", "first_name", "last_name", "email", "phone_number", "created_at",
                        "student_id", "major", "gpa", "year_of_study", "credits_completed"
                    ),
                    "note", "Contains ALL parent fields + student-specific fields"
                ),
                "authorTable", Map.of(
                    "name", "table_per_class_authors",
                    "columns", List.of(
                        "id (PK)", "first_name", "last_name", "email", "phone_number", "created_at",
                        "author_id", "affiliation", "research_field", "h_index", "total_citations", "first_publication_date"
                    ),
                    "note", "Contains ALL parent fields + author-specific fields"
                ),
                "adminTable", Map.of(
                    "name", "table_per_class_admins",
                    "columns", List.of(
                        "id (PK)", "first_name", "last_name", "email", "phone_number", "created_at",
                        "admin_id", "department", "admin_level", "last_login", "login_count", "is_super_admin", "permissions"
                    ),
                    "note", "Contains ALL parent fields + admin-specific fields"
                ),
                "duplication", "Parent fields (id, first_name, last_name, email, etc.) are duplicated in every table"
            ),
            "sampleData", Map.of(
                "student", Map.of(
                    "entity", student.getTablePerClassInfo(),
                    "storagePattern", "All data in single table_per_class_students table"
                ),
                "author", Map.of(
                    "entity", author.getTablePerClassInfo(),
                    "storagePattern", "All data in single table_per_class_authors table"
                ),
                "admin", Map.of(
                    "entity", admin.getTablePerClassInfo(),
                    "storagePattern", "All data in single table_per_class_admins table"
                )
            ),
            "queryExamples", Map.of(
                "studentsOnly", "SELECT * FROM table_per_class_students WHERE major = 'Mathematics'",
                "authorsOnly", "SELECT * FROM table_per_class_authors WHERE research_field = 'Quantum Computing'",
                "adminsOnly", "SELECT * FROM table_per_class_admins WHERE admin_level = 'SYSTEM'",
                "polymorphicQuery", """
                    SELECT id, first_name, last_name, email, 'STUDENT' as type 
                    FROM table_per_class_students
                    UNION ALL
                    SELECT id, first_name, last_name, email, 'AUTHOR' as type 
                    FROM table_per_class_authors
                    UNION ALL
                    SELECT id, first_name, last_name, email, 'ADMIN' as type 
                    FROM table_per_class_admins""",
                "countAllPersons", """
                    SELECT COUNT(*) FROM (
                        SELECT id FROM table_per_class_students
                        UNION ALL
                        SELECT id FROM table_per_class_authors
                        UNION ALL
                        SELECT id FROM table_per_class_admins
                    ) all_persons""",
                "complexPolymorphic", """
                    SELECT 'HIGH_ACHIEVER' as category, first_name, last_name, 'GPA: ' || gpa as metric
                    FROM table_per_class_students WHERE gpa >= 3.8
                    UNION ALL
                    SELECT 'INFLUENTIAL_AUTHOR' as category, first_name, last_name, 'H-Index: ' || h_index as metric
                    FROM table_per_class_authors WHERE h_index >= 20
                    UNION ALL
                    SELECT 'SUPER_ADMIN' as category, first_name, last_name, 'Logins: ' || login_count as metric
                    FROM table_per_class_admins WHERE is_super_admin = true"""
            ),
            "performanceCharacteristics", Map.of(
                "strengths", List.of("No JOINs for type-specific queries", "All constraints possible", "Independent optimization", "Best single-type performance"),
                "weaknesses", List.of("Data duplication", "Complex polymorphic queries", "Schema maintenance overhead", "UNION operations required")
            )
        ));
    }

    /**
     * GET /api/inheritance/jpa-strategies/comparison
     * Side-by-side comparison of all three JPA inheritance strategies
     */
    @GetMapping("/comparison")
    public ResponseEntity<Map<String, Object>> compareInheritanceStrategies() {
        return ResponseEntity.ok(Map.of(
            "title", "JPA Inheritance Strategies: Complete Comparison",
            "comparisonMatrix", Map.of(
                "criteria", List.of(
                    "Database Tables", "Query Performance", "Storage Efficiency", 
                    "Schema Complexity", "Polymorphic Queries", "Type-Specific Queries",
                    "Data Integrity", "Maintenance", "Scalability"
                ),
                "SINGLE_TABLE", Map.of(
                    "databaseTables", "1 table for entire hierarchy",
                    "queryPerformance", "⭐⭐⭐⭐⭐ Fastest (no JOINs)",
                    "storageEfficiency", "⭐⭐ Poor (many nulls)",
                    "schemaComplexity", "⭐⭐⭐⭐⭐ Simplest",
                    "polymorphicQueries", "⭐⭐⭐⭐⭐ Excellent",
                    "typeSpecificQueries", "⭐⭐⭐⭐ Good (WHERE clause)",
                    "dataIntegrity", "⭐⭐ Limited (nullable columns)",
                    "maintenance", "⭐⭐⭐⭐ Easy",
                    "scalability", "⭐⭐ Limited (wide tables)"
                ),
                "JOINED", Map.of(
                    "databaseTables", "1 parent + N child tables",
                    "queryPerformance", "⭐⭐⭐ Moderate (JOINs required)",
                    "storageEfficiency", "⭐⭐⭐⭐⭐ Excellent (normalized)",
                    "schemaComplexity", "⭐⭐⭐ Moderate",
                    "polymorphicQueries", "⭐⭐⭐ Good (complex JOINs)",
                    "typeSpecificQueries", "⭐⭐⭐ Good (JOIN required)",
                    "dataIntegrity", "⭐⭐⭐⭐⭐ Excellent (FK constraints)",
                    "maintenance", "⭐⭐⭐ Moderate",
                    "scalability", "⭐⭐⭐⭐ Good"
                ),
                "TABLE_PER_CLASS", Map.of(
                    "databaseTables", "N separate complete tables",
                    "queryPerformance", "⭐⭐⭐⭐ Good for single type",
                    "storageEfficiency", "⭐⭐⭐ Fair (field duplication)",
                    "schemaComplexity", "⭐⭐ Complex (multiple tables)",
                    "polymorphicQueries", "⭐⭐ Poor (UNION required)",
                    "typeSpecificQueries", "⭐⭐⭐⭐⭐ Excellent",
                    "dataIntegrity", "⭐⭐⭐⭐⭐ Excellent (all constraints)",
                    "maintenance", "⭐⭐ Complex (multiple tables)",
                    "scalability", "⭐⭐⭐ Fair"
                )
            ),
            "decisionMatrix", Map.of(
                "chooseSingleTable", List.of(
                    "Small, simple hierarchies",
                    "Frequent polymorphic queries", 
                    "Performance is critical",
                    "Entities have similar field sets",
                    "Read-heavy workloads"
                ),
                "chooseJoined", List.of(
                    "Complex hierarchies with different field sets",
                    "Storage efficiency is important",
                    "Strong data integrity requirements",
                    "Balanced read/write workloads",
                    "Entities have many different fields"
                ),
                "chooseTablePerClass", List.of(
                    "Independent entity types",
                    "Minimal polymorphic queries",
                    "Type-specific performance critical",
                    "Strong constraint requirements",
                    "Entities rarely queried together"
                )
            ),
            "realWorldExamples", Map.of(
                "SINGLE_TABLE", "User roles (Admin, Customer, Vendor) - similar data",
                "JOINED", "Vehicle hierarchy (Car, Truck, Motorcycle) - different specs",
                "TABLE_PER_CLASS", "Document types (Invoice, Report, Contract) - independent processing"
            )
        ));
    }

    /**
     * GET /api/inheritance/jpa-strategies/query-patterns
     * Database query pattern examples for each strategy
     */
    @GetMapping("/query-patterns")
    public ResponseEntity<Map<String, Object>> getQueryPatterns() {
        return ResponseEntity.ok(Map.of(
            "title", "Database Query Patterns by Inheritance Strategy",
            "patterns", Map.of(
                "SINGLE_TABLE", Map.of(
                    "basicSelect", "SELECT * FROM single_table_persons WHERE person_type = 'STUDENT'",
                    "polymorphicQuery", "SELECT id, first_name, last_name, person_type FROM single_table_persons",
                    "conditionalLogic", """
                        SELECT 
                            first_name, 
                            last_name,
                            CASE person_type 
                                WHEN 'STUDENT' THEN CONCAT('Major: ', major)
                                WHEN 'EMPLOYEE' THEN CONCAT('Dept: ', department)
                            END as details
                        FROM single_table_persons""",
                    "performance", "⭐⭐⭐⭐⭐ No JOINs, single table scan"
                ),
                "JOINED", Map.of(
                    "basicSelect", """
                        SELECT p.*, s.major, s.gpa 
                        FROM joined_persons p 
                        JOIN joined_students s ON p.id = s.id""",
                    "polymorphicQuery", """
                        SELECT p.id, p.first_name, p.last_name, 'STUDENT' as type
                        FROM joined_persons p 
                        JOIN joined_students s ON p.id = s.id
                        UNION ALL
                        SELECT p.id, p.first_name, p.last_name, 'EMPLOYEE' as type
                        FROM joined_persons p 
                        JOIN joined_employees e ON p.id = e.id""",
                    "conditionalJoin", """
                        SELECT p.first_name, p.last_name,
                               s.major, e.department
                        FROM joined_persons p
                        LEFT JOIN joined_students s ON p.id = s.id
                        LEFT JOIN joined_employees e ON p.id = e.id""",
                    "performance", "⭐⭐⭐ Requires JOINs, multiple table access"
                ),
                "TABLE_PER_CLASS", Map.of(
                    "basicSelect", "SELECT * FROM table_per_class_students WHERE major = 'Computer Science'",
                    "polymorphicQuery", """
                        SELECT id, first_name, last_name, 'STUDENT' as type, major as extra
                        FROM table_per_class_students
                        UNION ALL
                        SELECT id, first_name, last_name, 'EMPLOYEE' as type, department as extra
                        FROM table_per_class_employees""",
                    "aggregateQuery", """
                        SELECT 'TOTAL' as category, COUNT(*) as count FROM (
                            SELECT id FROM table_per_class_students
                            UNION ALL
                            SELECT id FROM table_per_class_employees
                        ) all_persons""",
                    "performance", "⭐⭐⭐⭐ Single type fast, polymorphic requires UNION"
                )
            ),
            "complexScenarios", Map.of(
                "reporting", Map.of(
                    "scenario", "Generate report of all persons with their specific details",
                    "SINGLE_TABLE", "Single query with CASE statements for different fields",
                    "JOINED", "Multiple JOINs or UNION of different JOIN queries",
                    "TABLE_PER_CLASS", "UNION of separate table queries with different field sets"
                ),
                "searching", Map.of(
                    "scenario", "Search across all persons by name",
                    "SINGLE_TABLE", "Simple WHERE clause on single table",
                    "JOINED", "Query parent table or JOIN all child tables",
                    "TABLE_PER_CLASS", "UNION search across all concrete tables"
                ),
                "counting", Map.of(
                    "scenario", "Count total persons in system",
                    "SINGLE_TABLE", "SELECT COUNT(*) FROM single_table_persons",
                    "JOINED", "SELECT COUNT(*) FROM joined_persons", 
                    "TABLE_PER_CLASS", "COUNT from UNION of all concrete tables"
                )
            ),
            "performanceTips", Map.of(
                "SINGLE_TABLE", List.of(
                    "Index discriminator column",
                    "Consider partitioning for large tables",
                    "Minimize nullable columns where possible"
                ),
                "JOINED", List.of(
                    "Index foreign key columns",
                    "Consider denormalization for read-heavy scenarios",
                    "Use covering indexes for common JOIN patterns"
                ),
                "TABLE_PER_CLASS", List.of(
                    "Index each table independently",
                    "Avoid polymorphic queries when possible",
                    "Consider views for common UNION operations"
                )
            )
        ));
    }
}