package com.ing.persistence.repository;

import com.ing.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByCustomerIdAndCreateDateBetween(String customerId, LocalDateTime startDate,
        LocalDateTime endDate);

}