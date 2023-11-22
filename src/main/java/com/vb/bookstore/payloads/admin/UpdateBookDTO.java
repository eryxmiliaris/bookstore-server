package com.vb.bookstore.payloads.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookDTO {
    @NotBlank
    private String title;
    @NotBlank
    private String author;
    @NotBlank
    private String description;
    @NotNull
    private LocalDate publicationDate;
    @NotNull
    private List<Long> categories;
}
