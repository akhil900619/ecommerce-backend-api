package com.akhil.ecommerceapi.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.akhil.ecommerceapi.entity.User;
import com.akhil.ecommerceapi.exception.DuplicateEmailException;
import com.akhil.ecommerceapi.exception.InvalidCredentialsException;
import com.akhil.ecommerceapi.exception.UserNotFoundException;
import com.akhil.ecommerceapi.repository.UserRepository;
import com.akhil.ecommerceapi.security.JwtUtil;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email already registered: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
    
    public String login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return jwtUtil.generateToken(user.getEmail());
    }

}