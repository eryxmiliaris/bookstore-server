package com.vb.bookstore.controllers;


import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.cart.AddToCartRequest;
import com.vb.bookstore.payloads.cart.CartDTO;
import com.vb.bookstore.payloads.cart.MoveToWishlistRequest;
import com.vb.bookstore.payloads.cart.ShippingMethodDTO;
import com.vb.bookstore.services.CartService;
import com.vb.bookstore.services.PromoCodeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final PromoCodeService promoCodeService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartDTO> getCart() {
        CartDTO cart = cartService.getCart();
        return ResponseEntity.ok(cart);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> addBookToCart(
            @Valid
            @RequestBody
            AddToCartRequest request
    ) {
        MessageResponse messageResponse = cartService.addBookToCart(request);
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> deleteBookFromCart(@PathVariable Long id) {
        MessageResponse messageResponse = cartService.deleteBookFromCart(id);
        return ResponseEntity.ok(messageResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> updatePaperBookQuantity(@PathVariable Long id, @RequestParam Integer newQuantity) {
        MessageResponse messageResponse = cartService.updatePaperBookQuantity(id, newQuantity);
        return ResponseEntity.ok(messageResponse);
    }

    @PostMapping("/moveToWishlist")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> moveToWishlist(@Valid @RequestBody MoveToWishlistRequest request) {
        MessageResponse messageResponse = cartService.moveToWishlist(request);
        return ResponseEntity.ok(messageResponse);
    }

    @PostMapping("/promoCode")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> applyPromoCode(@RequestParam @NotBlank String promoCode) {
        MessageResponse messageResponse = promoCodeService.applyPromoCode(promoCode);
        return ResponseEntity.ok(messageResponse);
    }

    @DeleteMapping("/promoCode")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> removePromoCode() {
        MessageResponse messageResponse = promoCodeService.removePromoCode();
        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/shippingMethod")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ShippingMethodDTO>> getShippingMethods() {
        List<ShippingMethodDTO> shippingMethods = cartService.getShippingMethods();
        return ResponseEntity.ok(shippingMethods);
    }

    @PutMapping("/shippingMethod")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> updateShippingMethod(@RequestParam Long shippingMethodId) {
        MessageResponse messageResponse = cartService.updateShippingMethod(shippingMethodId);
        return ResponseEntity.ok(messageResponse);
    }

    @PutMapping("/address")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> updateAddress(@RequestParam Long addressId) {
        MessageResponse messageResponse = cartService.updateAddress(addressId);
        return ResponseEntity.ok(messageResponse);
    }
}
