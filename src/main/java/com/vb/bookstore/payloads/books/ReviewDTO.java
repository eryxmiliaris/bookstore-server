package com.vb.bookstore.payloads.books;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private Long id;
    @NotNull
    @DecimalMin(value = "0.00")
    @DecimalMax(value = "10.00")
    private BigDecimal rating;
    @NotBlank
    @Size(min = 20)
    private String text;
    private String userName;
    private LocalDateTime publicationDate;
}
