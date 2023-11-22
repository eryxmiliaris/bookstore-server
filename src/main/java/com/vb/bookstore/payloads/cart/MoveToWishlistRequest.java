package com.vb.bookstore.payloads.cart;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoveToWishlistRequest {
    @NotNull
    private Long bookId;
    @NotBlank
    private String bookType;
    private Long paperBookId;
    @NotNull
    private Long cartItemId;
}
