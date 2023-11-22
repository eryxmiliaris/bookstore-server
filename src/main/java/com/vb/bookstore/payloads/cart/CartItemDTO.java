package com.vb.bookstore.payloads.cart;

import com.vb.bookstore.payloads.books.BookMainInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Long id;
    private Long bookId;
    private String bookType;
    private Long paperBookId;
    private BigDecimal price;
    private Integer quantity;
    private Boolean hasDiscount;
    private BigDecimal priceWithDiscount;
    private BigDecimal totalPrice;
}
