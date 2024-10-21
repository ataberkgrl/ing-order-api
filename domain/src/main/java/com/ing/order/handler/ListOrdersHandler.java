package com.ing.order.handler;

import com.ing.order.command.ListOrdersCommand;
import com.ing.order.model.Order;
import com.ing.order.port.OrderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ListOrdersHandler {

    private final OrderPort orderRepository;

    public List<Order> handle(ListOrdersCommand command) {
        LocalDateTime startDate = command.getStartDate() != null ? command.getStartDate() : LocalDateTime.now().minusMonths(1);
        LocalDateTime endDate = command.getEndDate() != null ? command.getEndDate() : LocalDateTime.now();

        validateDateRange(startDate, endDate);

        List<Order> orders = orderRepository.findByCustomerIdAndDateRange(
                command.getCustomerId(), startDate, endDate);

        log.info("Retrieved {} orders for customer {} between {} and {}",
                orders.size(), command.getCustomerId(), startDate, endDate
        );

        return orders;
    }

    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        if (ChronoUnit.DAYS.between(startDate, endDate) > 365) {
            throw new IllegalArgumentException("Date range must not exceed 1 year");
        }
    }
}