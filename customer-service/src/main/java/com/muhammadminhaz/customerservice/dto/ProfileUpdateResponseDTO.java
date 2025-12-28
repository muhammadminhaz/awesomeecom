package com.muhammadminhaz.customerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateResponseDTO {
    private String status;
    private String message;
    private String username;
}
