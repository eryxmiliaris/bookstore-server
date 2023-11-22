package com.vb.bookstore.payloads.library;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LibraryCollectionDTO {
    private Long id;
    @NotBlank
    private String name;
}
