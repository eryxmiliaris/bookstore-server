package com.vb.bookstore.payloads.books;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookMainInfoDTO {
    private Long id;
    private String title;
    private String author;
    private String description;
    private BigDecimal rating;
    private List<String> bookTypes;
}
