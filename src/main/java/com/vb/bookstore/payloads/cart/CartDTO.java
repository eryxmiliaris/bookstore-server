package com.vb.bookstore.payloads.cart;


import com.vb.bookstore.payloads.user.AddressDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long id;
    private List<CartItemDTO> cartItems;
    private Boolean hasPaperBooks;
    private AddressDTO address;
    private ShippingMethodDTO shippingMethod;
    private BigDecimal totalPrice;
    private Boolean hasPromoCode;
    private String promoCode;
    private BigDecimal totalPriceWithPromoCode;
}
