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
public class PaperBookDTO {
    private Long id;
    private Long bookId;
    private BigDecimal price;
    private Boolean hasDiscount;
    private BigDecimal priceWithDiscount;
    private Integer discountPercentage;
    private BigDecimal discountAmount;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime discountEndDate;
    private String publisher;
    private String coverType;
    private String isbn;
    private String isAvailable;
    private Integer numOfPages;
    private Boolean isHidden;
}
