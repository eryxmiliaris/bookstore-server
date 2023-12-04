package com.vb.bookstore.payloads.admin;

import com.vb.bookstore.entities.PaperBookCoverTypes;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewPaperBookDTO {
    private Long id;

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

    @NotBlank
    @Size(min = 5, max = 100)
    private String publisher;

    @NotNull
    private PaperBookCoverTypes coverType;

    @NotBlank
    @Size(min = 13, max = 13)
    private String isbn;

    @NotNull
    private String isAvailable;

    @NotNull
    @Min(value = 1)
    @Max(value = 10000)
    private Integer numOfPages;
}
