package com.vb.bookstore.payloads.books;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EBookDTO {
    private Long id;
    private BigDecimal price;
    private Boolean hasDiscount;
    private BigDecimal priceWithDiscount;
    private Integer discountPercentage;
    private BigDecimal discountAmount;
    private LocalDateTime discountEndDate;
    private String publisher;
    private Integer numOfPages;
    private Boolean isHidden;
}
