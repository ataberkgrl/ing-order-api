package com.ing.adapters.order.rest;

import com.ing.adapters.order.rest.dto.CancelOrderRequest;
import com.ing.adapters.order.rest.dto.CreateOrderRequest;
import com.ing.adapters.order.rest.dto.OrderResponse;
import com.ing.order.command.ListOrdersCommand;
import com.ing.order.handler.CancelOrderHandler;
import com.ing.order.handler.CreateOrderHandler;
import com.ing.order.handler.ListOrdersHandler;
import com.ing.order.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderHandler createOrderHandler;
    private final ListOrdersHandler listOrdersHandler;
    private final CancelOrderHandler cancelOrderHandler;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or #request.customerId == authentication.principal.id")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        Order order = createOrderHandler.handle(request.toModel());
        return new ResponseEntity<>(OrderResponse.fromModel(order), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or #customerId == authentication.principal.id")
    public ResponseEntity<List<OrderResponse>> listOrders(
            @RequestParam String customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        ListOrdersCommand command = new ListOrdersCommand(customerId, startDate, endDate);
        List<Order> orders = listOrdersHandler.handle(command);

        List<OrderResponse> orderResponses = orders.stream()
                .map(OrderResponse::fromModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(orderResponses);
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or #customerId == authentication.principal.id")
    public ResponseEntity<Void> cancelOrder(@PathVariable String orderId, @RequestParam String customerId) {
        CancelOrderRequest request = new CancelOrderRequest(orderId, customerId);
        cancelOrderHandler.handle(request.toModel());
        return ResponseEntity.noContent().build();
    }
}