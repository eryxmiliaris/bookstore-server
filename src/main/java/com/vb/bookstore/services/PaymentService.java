package com.vb.bookstore.services;

import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.payment.PaymentDTO;

import java.math.BigDecimal;

public interface PaymentService {
    PaymentDTO createPayment(BigDecimal fee, String returnUrl, String cancelUrl);

    MessageResponse checkPaymentStatus(String paymentId);

    MessageResponse captureOrder(String paymentId);
}
