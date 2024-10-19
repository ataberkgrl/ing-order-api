package com.ing.adapters.order.jpa.repository;

import com.ing.adapters.order.jpa.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByCustomerIdAndCreateDateBetween(String customerId, LocalDateTime startDate,
        LocalDateTime endDate);

}