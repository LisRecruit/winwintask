package org.example.user;

import jakarta.persistence.EntityNotFoundException;
import org.example.user.dto.UserCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void createUser_shouldEncodePasswordAndSaveUser() {
        UserCreateRequest request = new UserCreateRequest("a@a.com", "password123");

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0, User.class));

        User created = userService.createUser(request);

        assertEquals(request.email(), created.getEmail());
        assertEquals("hashedPassword", created.getPasswordHash());

        verify(userRepository).existsByEmail(request.email());
        verify(passwordEncoder).encode(request.password());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("hashedPassword", captor.getValue().getPasswordHash());
    }

    @Test
    void createUser_shouldThrow_whenEmailExists() {
        UserCreateRequest request = new UserCreateRequest("a@a.com", "password123");

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.createUser(request));
        assertEquals("User with this userName already exists.", ex.getMessage());

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void getUserByEmail_shouldReturnUser() {
        User user = User.builder().email("a@a.com").passwordHash("hash").build();

        when(userRepository.findByEmail("a@a.com")).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail("a@a.com");

        assertEquals(user, result);
        verify(userRepository).findByEmail("a@a.com");
    }

    @Test
    void getUserByEmail_shouldThrow_whenNotFound() {
        when(userRepository.findByEmail("missing@a.com")).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> userService.getUserByEmail("missing@a.com"));

        assertEquals("User not found", ex.getMessage());
        verify(userRepository).findByEmail("missing@a.com");
    }
}