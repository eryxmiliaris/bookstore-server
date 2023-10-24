package com.vb.bookstore.payloads.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserInfoRequest {
    @NotBlank
    @Size(min = 5, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9_-]{5,20}$", message = "Username must consist of 5 to 20 characters and can only contain letters, numbers, hyphens, and underscores.")
    private String username;
    @NotBlank
    @Email
    private String email;
    @NotNull
    @DateTimeFormat
    private String birthDate;
}
