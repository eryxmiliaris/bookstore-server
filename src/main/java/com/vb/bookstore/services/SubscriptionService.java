package com.vb.bookstore.services;

import com.vb.bookstore.entities.Subscription;
import com.vb.bookstore.payloads.MessageResponse;

import java.util.List;

public interface SubscriptionService {
    List<Subscription> getSubscriptions();

    MessageResponse orderSubscription(Long id);

    MessageResponse confirmSubscriptionPayment();
}
