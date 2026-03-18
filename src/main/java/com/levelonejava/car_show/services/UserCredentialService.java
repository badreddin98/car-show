package com.levelonejava.car_show.services;

import com.levelonejava.car_show.dtos.UserRequest;
import com.levelonejava.car_show.entities.UserCredential;
import com.levelonejava.car_show.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCredentialService {

    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;


    public String createUser(UserRequest userRequest){
        UserCredential userCredential = UserCredential.builder()
                .email(userRequest.email())
                .password(passwordEncoder.encode(userRequest.password()))
                .role("USER")
                .build();
        userCredential = userCredentialRepository.save(userCredential);

        return "User has been created";

    }

}
