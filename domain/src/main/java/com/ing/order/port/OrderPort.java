package com.ing.order.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.ing.order.model.Order;

public interface OrderPort {

    Order save(Order order);

    Optional<Order> findById(String id);

    List<Order> findByCustomerIdAndDateRange(String customerId, LocalDateTime startDate, LocalDateTime endDate);

    void deleteById(String id);

}