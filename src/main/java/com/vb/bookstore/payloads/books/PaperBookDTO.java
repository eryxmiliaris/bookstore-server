package com.vb.bookstore.payloads.books;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaperBookDTO {
    private Long id;
    private Long bookId;
    private String coverImageUrl;
    private BigDecimal price;
    private String publisher;
    private String coverType;
    private String isbn;
    private String isAvailable;
    private Integer numOfPages;
}
