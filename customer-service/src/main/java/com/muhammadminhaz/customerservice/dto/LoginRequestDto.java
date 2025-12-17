package com.muhammadminhaz.customerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {
    @NotBlank(message = "Please provide a username")
    private String username;
    @NotBlank(message = "Please provide a password")
    @Size(min = 4, message = "Password must be at least 4 characters long")
    private String password;

    private String role = "user";
}
