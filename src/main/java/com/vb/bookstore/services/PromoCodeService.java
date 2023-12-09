package com.vb.bookstore.services;

import com.vb.bookstore.payloads.MessageResponse;

public interface PromoCodeService {
    MessageResponse applyPromoCode(String code);

    MessageResponse removePromoCode();
}
