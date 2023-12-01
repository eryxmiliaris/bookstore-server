package com.vb.bookstore.services.impl;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import com.vb.bookstore.exceptions.ApiRequestException;
import com.vb.bookstore.exceptions.ResourceNotFoundException;
import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.payloads.PaymentDTO;
import com.vb.bookstore.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PayPalHttpClient payPalHttpClient;

    public PaymentDTO createPayment(BigDecimal fee, String returnUrl, String cancelUrl) {
        if (!(fee.longValue() > 0)) {
            throw new ApiRequestException("Fee must be greater than zero", HttpStatus.BAD_REQUEST);
        }

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");
        AmountWithBreakdown amountBreakdown = new AmountWithBreakdown().currencyCode("PLN").value(fee.toString());
        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest().amountWithBreakdown(amountBreakdown);
        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));
        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl);
        orderRequest.applicationContext(applicationContext);
        OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest().requestBody(orderRequest);

        try {
            HttpResponse<Order> orderHttpResponse = payPalHttpClient.execute(ordersCreateRequest);
            com.paypal.orders.Order order = orderHttpResponse.result();

            String redirectUrl = order.links().stream()
                    .filter(link -> "approve".equals(link.rel()))
                    .findFirst()
                    .orElseThrow(ResourceNotFoundException::new)
                    .href();

            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setPaymentId(order.id());
            paymentDTO.setRedirectUrl(redirectUrl);
            return paymentDTO;
        } catch (IOException e) {
            throw new ApiRequestException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public MessageResponse checkPaymentStatus(String paymentId) {
        OrdersGetRequest request = new OrdersGetRequest(paymentId);
        if (paymentId == null) {
            return new MessageResponse(false, "Payment status is null");
        }

        try {
            HttpResponse<com.paypal.orders.Order> response = payPalHttpClient.execute(request);

            if (response.statusCode() == 200) {
                com.paypal.orders.Order order = response.result();
                String paymentStatus = order.status();
                if (paymentStatus.equals("APPROVED")) {
                    return new MessageResponse(true, "Payment is approved");
                }
                return new MessageResponse(false, paymentStatus);
            } else {
                return new MessageResponse(false, response.result().status());
            }
        } catch (IOException e) {
            throw new ApiRequestException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public MessageResponse captureOrder(String paymentId) {
        OrdersCaptureRequest ordersCaptureRequest = new OrdersCaptureRequest(paymentId);
        try {
            HttpResponse<com.paypal.orders.Order> httpResponse = payPalHttpClient.execute(ordersCaptureRequest);
            if (httpResponse.result().status() != null) {
                return new MessageResponse(true, "Payment was completed");
            } else {
                throw new ApiRequestException("Payment wasn't completed", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e) {
            String jsonString = e.getMessage();
            String issue = "";
            int issueIndex = jsonString.indexOf("\"issue\":\"");
            if (issueIndex != -1) {
                String subString = jsonString.substring(issueIndex + 9);

                int endIndex = subString.indexOf("\"");
                if (endIndex != -1) {
                    issue = subString.substring(0, endIndex);
                }
            }
            if (issue.equals("ORDER_NOT_APPROVED")) {
                return new MessageResponse(false, "Order was not approved");
            }
            throw new ApiRequestException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
