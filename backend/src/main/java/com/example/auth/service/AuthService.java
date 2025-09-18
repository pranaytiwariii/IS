package com.example.auth.service;

import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.SignupRequest;
import com.example.auth.entity.Role;
import com.example.auth.entity.RoleName;
import com.example.auth.entity.User;
import com.example.auth.repository.RoleRepository;
import com.example.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean signup(SignupRequest signupRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return false;
        }

        // Check if email already exists
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return false;
        }

        // Get role
        RoleName roleName;
        try {
            roleName = RoleName.valueOf(signupRequest.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            return false; // Invalid role
        }

        Optional<Role> roleOptional = roleRepository.findByName(roleName);
        if (roleOptional.isEmpty()) {
            // Create role if it doesn't exist
            Role role = new Role(roleName);
            roleOptional = Optional.of(roleRepository.save(role));
        }

        // Create new user
        User user = new User(
            signupRequest.getUsername(),
            signupRequest.getEmail(),
            passwordEncoder.encode(signupRequest.getPassword()),
            roleOptional.get()
        );

        userRepository.save(user);
        return true;
    }

    public boolean login(LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        }
        
        return false;
    }

    public User authenticateAndGetUser(LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return user;
            }
        }
        
        return null;
    }

    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public String getUserRole(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent() && userOptional.get().getRole() != null) {
            return userOptional.get().getRole().getName().toString();
        }
        return null;
    }
}
