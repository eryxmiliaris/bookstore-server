package com.vb.bookstore.payloads.order;

import com.vb.bookstore.payloads.books.BookMainInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private Long id;
    private BookMainInfoDTO book;
    private String bookType;
    private Long paperBookId;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalPrice;
}
