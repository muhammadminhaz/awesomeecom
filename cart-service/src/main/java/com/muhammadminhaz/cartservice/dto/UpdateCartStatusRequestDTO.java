package com.muhammadminhaz.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCartStatusRequestDTO {
    private String cartId;
    private String status;
}
