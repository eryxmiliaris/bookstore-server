package com.vb.bookstore.payloads.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(min = 5, max = 100)
    private String title;

    @NotBlank
    @Size(min = 5, max = 100)
    private String author;

    @NotBlank
    @Size(min = 20)
    private String description;

    @NotNull
    private LocalDate publicationDate;

    @NotNull
    private List<Long> categories;
}
