package com.vb.bookstore.payloads.books;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AudioBookDTO {
    private Long id;
    private String coverImageUrl;
    private BigDecimal price;
    private String publisher;
    private String narrator;
    private Integer duration_seconds;
}
