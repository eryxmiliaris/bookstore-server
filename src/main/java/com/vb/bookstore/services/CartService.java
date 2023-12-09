package com.vb.bookstore.services;

import com.vb.bookstore.entities.Cart;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.cart.AddToCartRequest;
import com.vb.bookstore.payloads.cart.CartDTO;
import com.vb.bookstore.payloads.cart.MoveToWishlistRequest;
import com.vb.bookstore.payloads.cart.ShippingMethodDTO;

import java.util.List;

public interface CartService {
    CartDTO getCart();

    MessageResponse addBookToCart(AddToCartRequest request);

    MessageResponse deleteBookFromCart(Long id);

    MessageResponse updatePaperBookQuantity(Long id, Integer newQuantity);

    MessageResponse moveToWishlist(MoveToWishlistRequest request);

    void checkCartPaymentStatus(Cart cart);

    List<ShippingMethodDTO> getShippingMethods();

    MessageResponse updateAddress(Long addressId);

    MessageResponse updateShippingMethod(Long shippingMethodId);
}
