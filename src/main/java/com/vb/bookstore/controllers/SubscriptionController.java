package com.vb.bookstore.controllers;

import com.vb.bookstore.entities.Subscription;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @GetMapping("/subscriptions")
    public ResponseEntity<List<Subscription>> getSubscriptions() {
        List<Subscription> subscriptions = subscriptionService.getSubscriptions();
        return ResponseEntity.ok(subscriptions);
    }

    @PostMapping("/subscriptions/order/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> orderSubscription(@PathVariable Long id) {
        MessageResponse redirectResponse = subscriptionService.orderSubscription(id);
        return ResponseEntity.ok(redirectResponse);
    }

    @PostMapping("/subscriptions/confirmPayment")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> confirmPayment() {
        MessageResponse messageResponse = subscriptionService.confirmSubscriptionPayment();
        return ResponseEntity.ok(messageResponse);
    }
}
