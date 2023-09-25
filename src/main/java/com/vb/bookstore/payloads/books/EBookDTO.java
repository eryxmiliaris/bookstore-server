package com.vb.bookstore.payloads.books;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EBookDTO {
    private Long id;
    private String coverImageUrl;
    private BigDecimal price;
    private String publisher;
    private Integer numOfPages;
}
