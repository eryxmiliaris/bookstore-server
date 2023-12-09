package com.vb.bookstore.services.impl;

import com.vb.bookstore.entities.Subscription;
import com.vb.bookstore.entities.SubscriptionOrder;
import com.vb.bookstore.entities.User;
import com.vb.bookstore.exceptions.ApiRequestException;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.payment.PaymentDTO;
import com.vb.bookstore.repositories.SubscriptionOrderRepository;
import com.vb.bookstore.repositories.SubscriptionRepository;
import com.vb.bookstore.repositories.UserRepository;
import com.vb.bookstore.services.PaymentService;
import com.vb.bookstore.services.SubscriptionService;
import com.vb.bookstore.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final UserService userService;
    private final PaymentService paymentService;

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionOrderRepository subscriptionOrderRepository;
    private final UserRepository userRepository;

    public List<Subscription> getSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public MessageResponse orderSubscription(Long id) {
        User user = userService.getCurrentUser();
        if (user.getHasActiveSubscription()) {
            throw new ApiRequestException("User already has a subscription", HttpStatus.CONFLICT);
        }

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", "id", id));

        SubscriptionOrder subscriptionOrder = subscriptionOrderRepository.findByUserAndPaymentStatus(user, "processing");

        if (subscriptionOrder == null) {
            PaymentDTO payment = paymentService.createPayment(subscription.getPrice(), "http://localhost:5173/subscriptions?orderState=paymentSuccess", "http://localhost:5173/subscriptions");

            subscriptionOrder = new SubscriptionOrder();
            subscriptionOrder.setUser(user);
            subscriptionOrder.setSubscription(subscription);
            subscriptionOrder.setPaymentId(payment.getPaymentId());
            subscriptionOrder.setPaymentStatus("processing");
            subscriptionOrder.setPaymentRedirectUrl(payment.getRedirectUrl());

            subscriptionOrderRepository.save(subscriptionOrder);

            return new MessageResponse(true, payment.getRedirectUrl());
        } else {
            MessageResponse paymentCheckResponse = paymentService.checkPaymentStatus(subscriptionOrder.getPaymentId());
            if (paymentCheckResponse.isSuccess()) {
                Subscription orderedSubscription = subscriptionOrder.getSubscription();

                LocalDate startDate = LocalDate.now();
                LocalDate endDate = startDate.plusDays(orderedSubscription.getDurationDays());

                subscriptionOrder.setStartDate(startDate);
                subscriptionOrder.setEndDate(endDate);
                subscriptionOrder.setPaymentStatus("done");

                user.setHasActiveSubscription(true);
                user.setActiveSubscriptionEndDate(endDate);

                userRepository.save(user);
                subscriptionOrderRepository.save(subscriptionOrder);

                return new MessageResponse(true, "subscriptions page redirect");
            }

            if (subscriptionOrder.getSubscription() == subscription) {
                return new MessageResponse(true, subscriptionOrder.getPaymentRedirectUrl());
            } else {
                PaymentDTO payment = paymentService.createPayment(subscription.getPrice(), "http://localhost:5173/subscriptions?orderState=paymentSuccess", "http://localhost:5173/subscriptions");
                subscriptionOrder.setSubscription(subscription);
                subscriptionOrder.setPaymentId(payment.getPaymentId());
                subscriptionOrder.setPaymentStatus("processing");
                subscriptionOrder.setPaymentRedirectUrl(payment.getRedirectUrl());

                subscriptionOrderRepository.save(subscriptionOrder);

                return new MessageResponse(true, payment.getRedirectUrl());
            }
        }
    }

    public MessageResponse confirmSubscriptionPayment() {
        User user = userService.getCurrentUser();
        SubscriptionOrder subscriptionOrder = subscriptionOrderRepository.findByUserAndPaymentStatus(user, "processing");
        if (subscriptionOrder == null) {
            throw new ApiRequestException("No processing orders were found", HttpStatus.BAD_REQUEST);
        }
        MessageResponse response = paymentService.captureOrder(subscriptionOrder.getPaymentId());
        if (!response.isSuccess()) {
            return response;
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(subscriptionOrder.getSubscription().getDurationDays());

        subscriptionOrder.setStartDate(startDate);
        subscriptionOrder.setEndDate(endDate);
        subscriptionOrder.setPaymentStatus("done");

        user.setHasActiveSubscription(true);
        user.setActiveSubscriptionEndDate(endDate);

        userRepository.save(user);
        subscriptionOrderRepository.save(subscriptionOrder);

        return new MessageResponse(true, "Subscription has been applied successfully");
    }
}
