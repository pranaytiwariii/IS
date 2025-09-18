package com.example.auth.config;

import com.example.auth.entity.Paper;
import com.example.auth.entity.Role;
import com.example.auth.entity.RoleName;
import com.example.auth.entity.User;
import com.example.auth.repository.PaperRepository;
import com.example.auth.repository.RoleRepository;
import com.example.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PaperRepository paperRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        createRoleIfNotExists(RoleName.STUDENT);
        createRoleIfNotExists(RoleName.AUTHOR);
        createRoleIfNotExists(RoleName.COMMITTEE);

        // Create sample users if they don't exist
        createSampleUsers();
        
        // Create sample papers if none exist
        if (paperRepository.count() == 0) {
            createSamplePapers();
        }
    }

    private void createRoleIfNotExists(RoleName roleName) {
        if (!roleRepository.findByName(roleName).isPresent()) {
            Role role = new Role(roleName);
            roleRepository.save(role);
        }
    }

    private void createSampleUsers() {
        // Create sample author if doesn't exist
        if (!userRepository.findByUsername("author1").isPresent()) {
            Optional<Role> authorRole = roleRepository.findByName(RoleName.AUTHOR);
            if (authorRole.isPresent()) {
                User author = new User("author1", "author@example.com", 
                                    passwordEncoder.encode("password"), authorRole.get());
                userRepository.save(author);
            }
        }

        // Create sample committee member if doesn't exist
        if (!userRepository.findByUsername("committee1").isPresent()) {
            Optional<Role> committeeRole = roleRepository.findByName(RoleName.COMMITTEE);
            if (committeeRole.isPresent()) {
                User committee = new User("committee1", "committee@example.com", 
                                        passwordEncoder.encode("password"), committeeRole.get());
                userRepository.save(committee);
            }
        }
    }

    private void createSamplePapers() {
        Optional<User> authorOptional = userRepository.findByUsername("author1");
        Optional<User> committeeOptional = userRepository.findByUsername("committee1");

        if (authorOptional.isPresent()) {
            User author = authorOptional.get();

            // Create sample published paper
            Paper paper1 = new Paper(
                "Introduction to Machine Learning",
                "This paper provides a comprehensive introduction to machine learning concepts and algorithms.",
                "Machine learning is a subset of artificial intelligence that enables computers to learn and improve from experience without being explicitly programmed. This field has revolutionized various industries...",
                author
            );
            if (committeeOptional.isPresent()) {
                paper1.setPublishedByCommittee(committeeOptional.get());
                paper1.setPublicationDate(LocalDateTime.now().minusDays(10));
            }
            paperRepository.save(paper1);

            // Create sample unpublished paper
            Paper paper2 = new Paper(
                "Deep Learning Applications in Computer Vision",
                "An exploration of deep learning techniques applied to computer vision problems.",
                "Computer vision has significantly benefited from deep learning advances. This paper explores various architectures and their applications in image recognition, object detection, and semantic segmentation...",
                author
            );
            paperRepository.save(paper2);

            // Create another published paper
            Paper paper3 = new Paper(
                "Natural Language Processing with Transformers",
                "A study on transformer architectures for natural language processing tasks.",
                "Transformer models have become the backbone of modern NLP systems. This paper discusses the architecture, training methodologies, and applications of transformer models in various NLP tasks...",
                author
            );
            if (committeeOptional.isPresent()) {
                paper3.setPublishedByCommittee(committeeOptional.get());
                paper3.setPublicationDate(LocalDateTime.now().minusDays(5));
            }
            paperRepository.save(paper3);
        }
    }
}