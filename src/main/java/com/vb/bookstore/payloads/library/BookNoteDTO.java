package com.vb.bookstore.payloads.library;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookNoteDTO {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String text;
    @NotBlank
    private String position;
}
