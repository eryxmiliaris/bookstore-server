package com.vb.bookstore.payloads.auth;

import com.vb.bookstore.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotBlank
    @Size(min = 5, max = 45)
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @ValidPassword
    private String password;
}