package com.vb.bookstore.payloads.order;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate orderDate;
    private BigDecimal cartPrice;
    private Boolean hasPaperBooks;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate shippingDate;
    private BigDecimal shippingPrice;
    private BigDecimal totalPrice;
    private String orderStatus;
    private String addressUserFullName;
    private String addressLocation;
    private String addressPhoneNumber;
}
