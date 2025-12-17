package com.muhammadminhaz.customerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerProfileResponseDTO {
    private String username;
    private String name;
    private String email;
    private String address;
    private String phone;

}
