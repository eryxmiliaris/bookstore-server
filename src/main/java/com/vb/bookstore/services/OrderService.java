package com.vb.bookstore.services;


import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.order.OrderDTO;

import java.util.List;

public interface OrderService {
    List<OrderDTO> getOrders();

    MessageResponse order();

    String createOrderPayment();

    MessageResponse checkOrderPaymentStatus();

    Boolean userOwnsBook(Long id);
}
