package com.vb.bookstore.payloads.books;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AudioBookDTO {
    private Long id;
    private String coverImageUrl;
    private BigDecimal price;
    private Boolean hasDiscount;
    private BigDecimal priceWithDiscount;
    private Integer discountPercentage;
    private BigDecimal discountAmount;
    private Date discountEndDate;
    private String publisher;
    private String narrator;
    private Integer durationSeconds;
}
