package com.vb.bookstore.payloads.books;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private String description;
    private Double rating;
    private Date publicationDate;
    private List<String> categories;
    private List<PaperBookDTO> paperBooks;
    private AudioBookDTO audioBook;
    private EBookDTO eBook;
}
