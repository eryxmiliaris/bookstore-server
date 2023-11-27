package com.vb.bookstore.payloads.books;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publicationDate;
}
