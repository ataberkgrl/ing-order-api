package com.ing.adapters.order.jpa;

import com.ing.adapters.order.jpa.entity.OrderEntity;
import com.ing.adapters.order.jpa.repository.OrderJpaRepository;
import com.ing.order.model.Order;
import com.ing.order.port.OrderPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OrderAdapter implements OrderPort {

    private final OrderJpaRepository orderJpaRepository;

    public OrderAdapter(OrderJpaRepository orderJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity orderEntity;

        if (order.getId() != null) {
            orderEntity = orderJpaRepository.findById(Long.valueOf(order.getId()))
                    .orElseGet(() -> toEntity(order));

            updateEntity(orderEntity, order);
        } else {
            orderEntity = toEntity(order);
        }

        OrderEntity savedEntity = orderJpaRepository.save(orderEntity);

        return toModel(savedEntity);
    }

    @Override
    public Optional<Order> findById(String id) {
        return orderJpaRepository.findById(Long.valueOf(id))
                .map(this::toModel);
    }

    @Override
    public List<Order> findByCustomerIdAndDateRange(String customerId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderJpaRepository.findByCustomerIdAndCreateDateBetween(customerId, startDate, endDate).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        orderJpaRepository.deleteById(Long.valueOf(id));
    }

    private OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        updateEntity(entity, order);

        return entity;
    }

    private void updateEntity(OrderEntity entity, Order order) {

        if (order.getId() != null) {
            entity.setId(Long.valueOf(order.getId()));
        }

        entity.setCustomerId(order.getCustomerId());
        entity.setAssetName(order.getAssetName());
        entity.setSide(order.getSide());
        entity.setSize(order.getSize());
        entity.setPrice(order.getPrice());
        entity.setStatus(order.getStatus());
        entity.setCreateDate(order.getCreateDate());
    }

    private Order toModel(OrderEntity entity) {

        Order order = new Order(
                entity.getCustomerId(),
                entity.getAssetName(),
                entity.getSide(),
                entity.getSize(),
                entity.getPrice()
        );

        order.setId(entity.getId().toString());
        order.setStatus(entity.getStatus());

        return order;
    }
}