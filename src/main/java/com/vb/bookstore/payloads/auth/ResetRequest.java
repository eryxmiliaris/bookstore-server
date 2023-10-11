package com.vb.bookstore.payloads.auth;

import com.vb.bookstore.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetRequest {
    @NotBlank
    private String token;
    @NotBlank
    @ValidPassword
    private String password;
}
