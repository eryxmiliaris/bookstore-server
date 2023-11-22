package com.vb.bookstore.payloads.library;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LibraryItemDTO {
    private Long id;
    private Long bookId;
    private Long collectionId;
    private Boolean isSubscriptionItem;
    private String bookType;
    private String title;
    private String author;
    private String description;
    private Integer numOfPages;
    private String narrator;
    private Integer durationSeconds;
}
