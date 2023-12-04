package com.vb.bookstore.payloads.admin;

import com.vb.bookstore.entities.PaperBookCoverTypes;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewBookDTO {
    @NotBlank
    private String bookType;

    @NotBlank
    @Size(min = 5, max = 100)
    private String title;

    @NotBlank
    @Size(min = 5, max = 100)
    private String author;

    @NotBlank
    @Size(min = 20)
    private String description;

    @NotBlank
    @Size(min = 5, max = 100)
    private String publisher;

    @NotNull
    private LocalDate publicationDate;

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
    private Boolean isHidden;

    @NotNull
    private List<Long> categories;

    private PaperBookCoverTypes coverType;

    @Size(min = 13, max = 13)
    private String isbn;

    private Boolean isAvailable;

    @Min(value = 1)
    @Max(value = 10000)
    private Integer numOfPages;

    @Size(min = 5, max = 100)
    private String narrator;

    @Min(value = 60)
    @Max(value = 360000)
    private Integer durationSeconds;
}
