package com.example.testproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationUser {
    @NotNull(message = "The username cannot be empty.")
    String username;
    @NotNull(message = "The password cannot be empty.")
    @Min(value = 7, message = "The password must contain at least 7 characters.")
    String password;
    @Email
    String email;
}
