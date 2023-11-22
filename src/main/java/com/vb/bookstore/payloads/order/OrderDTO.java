package com.vb.bookstore.payloads.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private String username;
    private List<OrderItemDTO> orderItems;
    private LocalDate orderDate;
    private BigDecimal cartPrice;
    private Boolean hasPaperBooks;
    private LocalDate shippingDate;
    private BigDecimal shippingPrice;
    private BigDecimal totalPrice;
    private String orderStatus;
    private String addressUserFullName;
    private String addressLocation;
    private String addressPhoneNumber;
}
