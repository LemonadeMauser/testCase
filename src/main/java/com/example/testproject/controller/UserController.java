package com.example.testproject.controller;

import com.example.testproject.dto.RegistrationUser;
import com.example.testproject.dto.UserDto;
import com.example.testproject.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody RegistrationUser dto) {
        log.info("A request has been received to add a user.");
        return service.createUser(dto);
    }
}
