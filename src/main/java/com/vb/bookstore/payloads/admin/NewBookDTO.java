package com.vb.bookstore.payloads.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private String title;
    @NotBlank
    private String author;
    @NotBlank
    private String description;
    @NotBlank
    private String publisher;
    @NotNull
    private LocalDate publicationDate;
    @NotNull
    private BigDecimal price;
    @NotNull
    private Boolean hasDiscount;
    private Integer discountPercentage;
    private LocalDateTime discountEndDate;
    @NotNull
    private Boolean isHidden;
    @NotNull
    private List<Long> categories;
    private String coverType;
    private String isbn;
    private Boolean isAvailable;
    private Integer numOfPages;
    private String narrator;
    private Integer durationSeconds;
}
