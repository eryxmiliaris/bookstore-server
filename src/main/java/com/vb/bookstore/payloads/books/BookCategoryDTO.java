package com.vb.bookstore.payloads.books;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookCategoryDTO {
    private Long id;
    private String categoryName;
}
