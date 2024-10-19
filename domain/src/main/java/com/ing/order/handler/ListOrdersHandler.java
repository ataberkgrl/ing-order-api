package com.ing.order.handler;

import com.ing.order.command.ListOrdersCommand;
import com.ing.order.model.Order;
import com.ing.order.port.OrderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ListOrdersHandler {

    private final OrderPort orderRepository;

    public List<Order> handle(ListOrdersCommand command) {

        List<Order> orders = orderRepository.findByCustomerIdAndDateRange(
            command.getCustomerId(), command.getStartDate(), command.getEndDate());

        log.info("Retrieved {} orders for customer {} between {} and {}",
            orders.size(), command.getCustomerId(), command.getStartDate(), command.getEndDate()
        );

        return orders;
    }

}