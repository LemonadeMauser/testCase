package com.example.testproject.service;

import com.example.testproject.dto.RegistrationUser;
import com.example.testproject.dto.UserDto;
import com.example.testproject.entity.User;
import com.example.testproject.exception.UserExistEx;
import com.example.testproject.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo repo;
    private final ModelMapper mapper;


    public ResponseEntity<UserDto> createUser(RegistrationUser dto) {
        Optional<User> checker = repo.findUserByUsername(dto.getUsername());
        if (checker.isPresent()) {
            throw new UserExistEx("User " + dto.getUsername() + " already exist.");
        }
        User user = repo.save(registrationDtoToEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(entityToDto(user));
    }

    protected User getIfExistOrThrow(Long userId) {
        return repo.findById(userId).orElseThrow(() -> new UserExistEx("User with userId="
                + userId + " not found."));
    }

    private UserDto entityToDto(User user) {
        return mapper.map(user, UserDto.class);
    }

    private User registrationDtoToEntity(RegistrationUser dto) {
        return mapper.map(dto, User.class);
    }
}
