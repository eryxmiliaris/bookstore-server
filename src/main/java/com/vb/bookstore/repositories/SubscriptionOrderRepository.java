package com.vb.bookstore.repositories;

import com.vb.bookstore.entities.SubscriptionOrder;
import com.vb.bookstore.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionOrderRepository extends JpaRepository<SubscriptionOrder, Long> {
    SubscriptionOrder findByUserAndPaymentStatus(User user, String paymentStatus);
}
