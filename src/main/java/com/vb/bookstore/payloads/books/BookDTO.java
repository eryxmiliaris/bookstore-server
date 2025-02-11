package com.vb.bookstore.payloads.books;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private String description;
    private BigDecimal rating;
    private List<ReviewDTO> reviews;
    private Integer numOfReviews;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate publicationDate;
    private List<String> categories;
    private List<PaperBookDTO> paperBooks;
    private AudiobookDTO audiobook;
    private EbookDTO ebook;
}
