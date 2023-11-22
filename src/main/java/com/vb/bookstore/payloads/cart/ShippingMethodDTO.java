package com.vb.bookstore.payloads.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingMethodDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer durationDays;
}
