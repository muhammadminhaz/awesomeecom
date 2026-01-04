package com.muhammadminhaz.customerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteResponseDTO {
    private String status;
    private String message;
}
