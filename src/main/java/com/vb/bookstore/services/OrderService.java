package com.vb.bookstore.services;


import com.vb.bookstore.entities.*;
import com.vb.bookstore.exceptions.ApiRequestException;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.PaymentDTO;
import com.vb.bookstore.payloads.order.OrderDTO;
import com.vb.bookstore.repositories.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final ModelMapper modelMapper;

    private final UserService userService;
    private final PaymentService paymentService;

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final LibraryItemRepository libraryItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final BookRepository bookRepository;

    public List<OrderDTO> getOrders() {
        User user = userService.getCurrentUser();
        List<Order> orders = orderRepository.findByUserOrderByIdDesc(user);

        return orders.stream().map((element) -> modelMapper.map(element, OrderDTO.class)).toList();
    }

    public MessageResponse order() {
        User user = userService.getCurrentUser();
        Cart cart = user.getCart();
        if (!(cart.getTotalPrice().longValue() > 0)) {
            throw new ApiRequestException("Cart can't be empty!", HttpStatus.BAD_REQUEST);
        }

        String redirectUrl = createOrderPayment();

        MessageResponse messageResponse = paymentService.captureOrder(cart.getPaymentId());
        if (messageResponse.isSuccess()) {
            cart.setPaymentStatus("completed");
        } else {
            messageResponse.setMessage(redirectUrl);
            return messageResponse;
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setCartPrice(cart.getTotalPrice());

        List<LibraryItem> libraryItems = cart.getCartItems().stream()
                .filter((cartItem -> !cartItem.getBookType().equals("Paper book")))
                .map((cartItem) -> {
                    LibraryItem libraryItem = libraryItemRepository.findByUserAndBookAndBookType(user, cartItem.getBook(), cartItem.getBookType())
                            .orElse(new LibraryItem());
                    libraryItem.setBook(cartItem.getBook());
                    libraryItem.setBookType(cartItem.getBookType());
                    libraryItem.setUser(user);
                    libraryItem.setIsSubscriptionItem(false);
                    libraryItem.setAddedDate(LocalDate.now());
                    libraryItem.setLastPosition("0");
                    return libraryItem;
                }).collect(Collectors.toList());

        order.setOrderItems(cart.getCartItems().stream().map((cartItem) -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cartItem.getBook());
            orderItem.setBookType(cartItem.getBookType());
            orderItem.setPaperBookId(cartItem.getPaperBookId());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getTotalPrice());
            return orderItem;
        }).toList());

        if (cart.getHasPaperBooks()) {
            Address address = cart.getAddress();
            if (address == null) {
                throw new ApiRequestException("You must provide delivery address!", HttpStatus.CONFLICT);
            }

            ShippingMethod shippingMethod = cart.getShippingMethod();
            if (shippingMethod == null) {
                throw new ApiRequestException("You must provide shipping method!", HttpStatus.CONFLICT);
            }

            order.setHasPaperBooks(true);
            order.setShippingPrice(shippingMethod.getPrice());
            LocalDate today = LocalDate.now();
            LocalDate shippingDate = today.plusDays(shippingMethod.getDurationDays());
            order.setShippingDate(shippingDate);
            order.setTotalPrice(order.getCartPrice().add(order.getShippingPrice()));
            order.setOrderStatus("Processing");

            order.setAddressUserFullName(address.getUserName() + " " + address.getUserSurname());
            order.setAddressLocation(address.getCity() + ", " + address.getStreet() + ", " + address.getPostalCode());
            order.setAddressPhoneNumber(address.getPhoneNumber());
        } else {
            order.setHasPaperBooks(false);
            order.setTotalPrice(order.getCartPrice());
            order.setOrderStatus("Done");
        }

        cart.clear();
        if (cart.getHasPromoCode()) {
            user.getUsedPromoCodes().add(cart.getPromoCode());
        }
        cart.setPromoCode(null);
        cart.setPaymentStatus(null);
        cart.setPaymentId(null);
        cart.setPaymentRedirectUrl(null);
        cart.setAddress(null);
        cart.setShippingMethod(null);
        orderRepository.save(order);
        cartRepository.save(cart);
        userRepository.save(user);
        libraryItemRepository.saveAll(libraryItems);

        return new MessageResponse(true, "Order created");
    }

    public String createOrderPayment() {
        Cart cart = userService.getCurrentUser().getCart();
        BigDecimal fee;
        if (cart.getHasPromoCode()) {
            fee = cart.getTotalPriceWithPromoCode();
        } else {
            fee = cart.getTotalPrice();
        }
        if (!(fee.longValue() > 0)) {
            throw new ApiRequestException("Cart can't be empty", HttpStatus.BAD_REQUEST);
        }
        if (cart.getPaymentId() != null) {
            return cart.getPaymentRedirectUrl();
        }
        if (cart.getHasPaperBooks()) {
            fee = fee.add(cart.getShippingMethod().getPrice());
        }

        PaymentDTO payment = paymentService.createPayment(fee,
                "http://localhost:5173/profile/cart?orderState=paymentSuccess",
                "http://localhost:5173/profile/cart");

        cart.setPaymentId(payment.getPaymentId());
        cart.setPaymentRedirectUrl(payment.getRedirectUrl());
        cart.setPaymentStatus("processing");
        cartRepository.save(cart);

        return payment.getRedirectUrl();
    }

    public MessageResponse checkOrderPaymentStatus() {
        User user = userService.getCurrentUser();
        String paymentId = user.getCart().getPaymentId();

        MessageResponse messageResponse = paymentService.checkPaymentStatus(paymentId);

        if (messageResponse.isSuccess()) {
            return order();
        } else {
            return messageResponse;
        }
    }

    public Boolean userOwnsBook(Long id) {
        User user = userService.getCurrentUser();
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        Optional<List<OrderItem>> orderItem = orderItemRepository.findByOrder_UserAndBook(user, book);
        return orderItem.isPresent() && !orderItem.get().isEmpty();
    }
}
