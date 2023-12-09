package com.vb.bookstore.services.impl;

import com.vb.bookstore.entities.Cart;
import com.vb.bookstore.entities.PromoCode;
import com.vb.bookstore.entities.User;
import com.vb.bookstore.exceptions.ApiRequestException;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.repositories.CartRepository;
import com.vb.bookstore.repositories.PromoCodeRepository;
import com.vb.bookstore.services.CartService;
import com.vb.bookstore.services.PromoCodeService;
import com.vb.bookstore.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromoCodeServiceImpl implements PromoCodeService {
    private final UserService userService;
    private final CartService cartService;
    private final PromoCodeRepository promoCodeRepository;
    private final CartRepository cartRepository;

    public MessageResponse applyPromoCode(String code) {
        User user = userService.getCurrentUser();
        Cart cart = user.getCart();

        cartService.checkCartPaymentStatus(cart);

        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code", "code", code));
        if (!promoCode.getIsActive()) {
            throw new ApiRequestException("Promo code is inactive!", HttpStatus.BAD_REQUEST);
        }
        if (user.getUsedPromoCodes().contains(promoCode)) {
            throw new ApiRequestException("You have already used this promo code!", HttpStatus.CONFLICT);
        }
        cart.setPromoCode(promoCode);
        cartRepository.save(cart);
        return new MessageResponse(true, "Promo code has been successfully applied!");
    }

    public MessageResponse removePromoCode() {
        User user = userService.getCurrentUser();
        Cart cart = user.getCart();

        cartService.checkCartPaymentStatus(cart);

        cart.setPromoCode(null);
        cartRepository.save(cart);
        return new MessageResponse(true, "Promo code has been successfully removed");
    }
}
