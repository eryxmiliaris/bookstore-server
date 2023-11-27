package com.vb.bookstore.payloads.books;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AudioBookDTO {
    private Long id;
    private BigDecimal price;
    private Boolean hasDiscount;
    private BigDecimal priceWithDiscount;
    private Integer discountPercentage;
    private BigDecimal discountAmount;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime discountEndDate;
    private String publisher;
    private String narrator;
    private Integer durationSeconds;
    private Boolean isHidden;
}
