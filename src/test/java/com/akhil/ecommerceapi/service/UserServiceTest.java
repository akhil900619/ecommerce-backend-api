package com.akhil.ecommerceapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.akhil.ecommerceapi.entity.*;
import com.akhil.ecommerceapi.exception.DuplicateEmailException;
import com.akhil.ecommerceapi.repository.UserRepository;

import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_throwsException_whenEmailAlreadyExists() {
        User newUser = new User();
        newUser.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(DuplicateEmailException.class, () -> userService.registerUser(newUser));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_hashesPassword_beforeSaving() {
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setPassword("plainTextPassword");

        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainTextPassword")).thenReturn("hashedPasswordValue");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.registerUser(newUser);

        assertEquals("hashedPasswordValue", result.getPassword());
        verify(passwordEncoder, times(1)).encode("plainTextPassword");
    }
}