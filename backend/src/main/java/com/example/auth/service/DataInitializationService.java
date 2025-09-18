package com.example.auth.service;

import com.example.auth.entity.Role;
import com.example.auth.entity.RoleName;
import com.example.auth.entity.User;
import com.example.auth.entity.Paper;
import com.example.auth.entity.Tag;
import com.example.auth.repository.RoleRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.repository.PaperRepository;
import com.example.auth.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaperRepository paperRepository;

    @Autowired
    private TagRepository tagRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        initializeDatabase();
    }

    private void initializeDatabase() {
        // Check if data already exists
        if (roleRepository.count() > 0) {
            System.out.println("Database already initialized. Skipping data initialization.");
            return;
        }

        System.out.println("Initializing database with sample data...");

        // Create Roles
        Role studentRole = createRoleIfNotExists(RoleName.STUDENT);
        Role authorRole = createRoleIfNotExists(RoleName.AUTHOR);
        Role committeeRole = createRoleIfNotExists(RoleName.COMMITTEE);

        // Create Sample Users
        User student1 = createUser("student1", "student1@example.com", "password123", studentRole);
        User author1 = createUser("author1", "author1@example.com", "password123", authorRole);
        User author2 = createUser("author2", "author2@example.com", "password123", authorRole);
        User committee1 = createUser("committee1", "committee1@example.com", "password123", committeeRole);

        // Create Sample Tags
        Tag aiTag = createTag("Artificial Intelligence", "Research related to AI and machine learning");
        Tag dbTag = createTag("Database Systems", "Database management and design");
        Tag webTag = createTag("Web Development", "Full-stack web application development");
        Tag securityTag = createTag("Cybersecurity", "Information security and data protection");

        // Create Sample Papers
        Paper paper1 = createPaper(
            "Machine Learning in Healthcare",
            "This paper explores the applications of machine learning algorithms in healthcare diagnostics and treatment planning.",
            "Machine learning has revolutionized healthcare by providing intelligent solutions for diagnosis, treatment, and patient care. This comprehensive study examines various ML algorithms including supervised learning techniques like Random Forest and SVM for medical image analysis, and unsupervised learning methods for pattern recognition in patient data. We present case studies from radiology, pathology, and personalized medicine, demonstrating improved accuracy and efficiency. The paper also addresses ethical considerations, data privacy concerns, and regulatory compliance in healthcare AI. Our findings suggest that ML can reduce diagnostic errors by up to 23% while improving treatment outcomes. Future research directions include federated learning for multi-institutional collaboration and explainable AI for clinical decision support.",
            author1
        );
        paper1.getTags().add(aiTag);

        Paper paper2 = createPaper(
            "Distributed Database Design Patterns",
            "A comprehensive study of modern distributed database architectures and their implementation strategies.",
            "This paper presents a detailed analysis of distributed database design patterns used in modern scalable applications. We explore various consistency models including eventual consistency, strong consistency, and causal consistency, examining their trade-offs in terms of performance and data integrity. The study covers popular distributed database systems like MongoDB, Cassandra, and CockroachDB, analyzing their architectural decisions and use cases. We present performance benchmarks comparing ACID transactions versus BASE properties across different workload patterns. The paper includes practical implementation guidelines for choosing appropriate sharding strategies, replication methods, and consistency levels based on application requirements. Case studies from e-commerce, social media, and financial services demonstrate real-world applications of these patterns.",
            author2
        );
        paper2.getTags().add(dbTag);

        Paper paper3 = createPaper(
            "Modern Web Security Vulnerabilities",
            "An analysis of contemporary web application security threats and mitigation strategies.",
            "Web application security remains a critical concern as applications become more complex and interconnected. This paper examines emerging security vulnerabilities in modern web applications, including advanced XSS attacks, CSRF exploits, and supply chain vulnerabilities through third-party dependencies. We analyze the OWASP Top 10 2023 updates and discuss new attack vectors such as client-side prototype pollution and server-side template injection. The study includes practical mitigation strategies including Content Security Policy (CSP) implementation, secure coding practices, and automated security testing integration. We present a comprehensive security framework for full-stack applications covering authentication, authorization, input validation, and secure communication protocols. Real-world case studies demonstrate the financial and reputational impact of security breaches and the ROI of proactive security measures.",
            author1
        );
        paper3.getTags().add(webTag);
        paper3.getTags().add(securityTag);

        // Save papers
        paperRepository.save(paper1);
        paperRepository.save(paper2);
        paperRepository.save(paper3);

        // Publish some papers (simulate committee member publishing)
        paper1.setPublishedByCommittee(committee1);
        paper1.setPublicationDate(LocalDateTime.now().minusDays(5));
        paperRepository.save(paper1);

        paper3.setPublishedByCommittee(committee1);
        paper3.setPublicationDate(LocalDateTime.now().minusDays(2));
        paperRepository.save(paper3);

        System.out.println("Database initialization completed successfully!");
        System.out.println("Sample users created:");
        System.out.println("- student1 / password123 (STUDENT role)");
        System.out.println("- author1 / password123 (AUTHOR role)");
        System.out.println("- author2 / password123 (AUTHOR role)");
        System.out.println("- committee1 / password123 (COMMITTEE role)");
        System.out.println("Sample papers and relationships created successfully.");
    }

    private Role createRoleIfNotExists(RoleName roleName) {
        return roleRepository.findByName(roleName)
            .orElseGet(() -> {
                Role role = new Role(roleName);
                return roleRepository.save(role);
            });
    }

    private User createUser(String username, String email, String password, Role role) {
        User user = new User(
            username,
            email,
            passwordEncoder.encode(password),
            role
        );
        return userRepository.save(user);
    }

    private Tag createTag(String name, String description) {
        Tag tag = new Tag();
        tag.setName(name);
        tag.setDescription(description);
        return tagRepository.save(tag);
    }

    private Paper createPaper(String title, String abstractText, String content, User author) {
        Paper paper = new Paper();
        paper.setTitle(title);
        paper.setAbstractText(abstractText);
        paper.setContent(content);
        paper.setAuthor(author);
        paper.setCreatedAt(LocalDateTime.now().minusDays((long) (Math.random() * 30)));
        paper.setUpdatedAt(paper.getCreatedAt());
        paper.setTags(new HashSet<>());
        return paper;
    }
}
