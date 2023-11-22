package com.vb.bookstore.payloads.admin;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewPromoCodeDTO {
    @NotBlank
    private String code;
    @NotNull
    @Min(value = 1)
    @Max(value = 100)
    private Integer percentage;
    @NotNull
    private LocalDateTime endDate;
}
