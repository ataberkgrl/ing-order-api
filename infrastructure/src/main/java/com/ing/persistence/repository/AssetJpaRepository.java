package com.ing.persistence.repository;

import com.ing.persistence.entity.AssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetJpaRepository extends JpaRepository<AssetEntity, Long> {

    Optional<AssetEntity> findByCustomerIdAndAssetName(String customerId, String assetName);

    List<AssetEntity> findAllByCustomerId(String customerId);

}