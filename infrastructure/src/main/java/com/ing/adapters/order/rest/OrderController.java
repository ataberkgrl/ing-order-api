package com.ing.adapters.order.rest;

import com.ing.adapters.order.rest.dto.CancelOrderRequest;
import com.ing.adapters.order.rest.dto.CreateOrderRequest;
import com.ing.adapters.order.rest.dto.ListOrdersRequest;
import com.ing.adapters.order.rest.dto.OrderResponse;
import com.ing.order.handler.CancelOrderHandler;
import com.ing.order.handler.CreateOrderHandler;
import com.ing.order.handler.ListOrdersHandler;
import com.ing.order.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        Order order = createOrderHandler.handle(request.toModel());
        return new ResponseEntity<>(OrderResponse.fromModel(order), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> listOrders(ListOrdersRequest request) {

        List<Order> orders = listOrdersHandler.handle(request.toModel());

        List<OrderResponse> orderResponses = orders.stream()
            .map(OrderResponse::fromModel)
            .collect(Collectors.toList());

        return ResponseEntity.ok(orderResponses);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable String orderId, @RequestParam String customerId) {
        CancelOrderRequest request = new CancelOrderRequest(orderId, customerId);

        cancelOrderHandler.handle(request.toModel());

        return ResponseEntity.noContent().build();
    }
}