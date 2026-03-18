package com.levelonejava.car_show.services;

import com.levelonejava.car_show.dtos.UserRequest;
import com.levelonejava.car_show.entities.UserCredential;
import com.levelonejava.car_show.repository.UserCredentialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCredentialServiceTest {

    @Mock
    private UserCredentialRepository userCredentialRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserCredentialService userCredentialService;

    @Test
    void createUser_savesAndReturnsMessage() {
        UserRequest req = new UserRequest("test@example.com", "password123");
        when(passwordEncoder.encode("password123")).thenReturn("encodedPass");
        when(userCredentialRepository.save(any(UserCredential.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String res = userCredentialService.createUser(req);

        assertEquals("User has been created", res);
    }
}

