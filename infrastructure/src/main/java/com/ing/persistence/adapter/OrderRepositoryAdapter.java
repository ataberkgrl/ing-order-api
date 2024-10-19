package com.ing.persistence.adapter;

import com.ing.order.model.Order;
import com.ing.order.port.OrderPort;
import com.ing.persistence.entity.OrderEntity;
import com.ing.persistence.repository.OrderJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OrderRepositoryAdapter implements OrderPort {

    private final OrderJpaRepository orderJpaRepository;

    public OrderRepositoryAdapter(OrderJpaRepository orderJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity orderEntity = toEntity(order);
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
        entity.setCustomerId(order.getCustomerId());
        entity.setAssetName(order.getAssetName());
        entity.setSide(order.getSide());
        entity.setSize(order.getSize());
        entity.setPrice(order.getPrice());
        entity.setStatus(order.getStatus());
        entity.setCreateDate(order.getCreateDate());
        return entity;
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