package com.muhammadminhaz.customerservice.dto;

import com.muhammadminhaz.customerservice.validator.PasswordMatches;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@PasswordMatches
public class RegisterRequestDTO {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._]+$", message = "Username can only contain letters, numbers, dots, and underscores")
    private String username;

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    private String address;

    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 4 characters")
    private String password;

    @NotBlank(message = "Please confirm your password")
    @Size(min = 4, message = "Confirm password must be at least 4 characters long")
    private String confirmPassword;
}
