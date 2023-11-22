package com.vb.bookstore.payloads.admin;

import jakarta.validation.constraints.NotNull;
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
    private BigDecimal price;
    @NotNull
    private Boolean hasDiscount;
    private Integer discountPercentage;
    private LocalDateTime discountEndDate;
    @NotNull
    private String publisher;
    @NotNull
    private String coverType;
    @NotNull
    private String isbn;
    @NotNull
    private String isAvailable;
    @NotNull
    private Integer numOfPages;
}
