package com.vb.bookstore.payloads.wishlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistDTO {
    private Long id;
    @NotNull
    private Long bookId;
    @NotBlank
    private String bookType;
    private Long paperBookId;
}
