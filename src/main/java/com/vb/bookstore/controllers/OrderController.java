package com.vb.bookstore.controllers;

import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.order.OrderDTO;
import com.vb.bookstore.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OrderDTO>> getOrders() {
        List<OrderDTO> orderDTOS = orderService.getOrders();
        return ResponseEntity.ok(orderDTOS);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> order() {
        MessageResponse messageResponse = orderService.order();
        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/payment/check")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> checkPayment() {
        MessageResponse messageResponse = orderService.checkOrderPaymentStatus();
        return ResponseEntity.ok(messageResponse);
    }

    @GetMapping("/book/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Boolean> userOwnsBook(@PathVariable Long id) {
        Boolean status = orderService.userOwnsBook(id);
        return ResponseEntity.ok(status);
    }
}
