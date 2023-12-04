package com.vb.bookstore.payloads.admin;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewAudiobookDTO {
    @NotNull
    private Boolean isHidden;

    @NotNull
    @DecimalMin(value = "0.01")
    @DecimalMax(value = "9999.99")
    private BigDecimal price;

    @NotNull
    private Boolean hasDiscount;

    @Min(value = 0)
    @Max(value = 100)
    private Integer discountPercentage;

    private LocalDateTime discountEndDate;

    @NotNull
    private String publisher;

    @NotNull
    private String narrator;

    @NotNull
    private String durationSeconds;
}
