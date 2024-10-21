package com.ing.adapters.asset.jpa;

import com.ing.adapters.asset.jpa.entity.AssetEntity;
import com.ing.adapters.asset.jpa.repository.AssetJpaRepository;
import com.ing.asset.model.Asset;
import com.ing.asset.port.AssetPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AssetAdapter implements AssetPort {

    private final AssetJpaRepository assetJpaRepository;

    public AssetAdapter(AssetJpaRepository assetJpaRepository) {
        this.assetJpaRepository = assetJpaRepository;
    }

    @Override
    public Asset save(Asset asset) {
        AssetEntity assetEntity = toEntity(asset);
        AssetEntity savedEntity = assetJpaRepository.save(assetEntity);
        return toModel(savedEntity);
    }

    @Override
    public Optional<Asset> findByCustomerIdAndAssetName(String customerId, String assetName) {
        return assetJpaRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .map(this::toModel);
    }

    @Override
    public List<Asset> findAllByCustomerId(String customerId) {
        return assetJpaRepository.findAllByCustomerId(customerId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    private AssetEntity toEntity(Asset asset) {
        AssetEntity entity = new AssetEntity();
        entity.setCustomerId(asset.getCustomerId());
        entity.setAssetName(asset.getAssetName());
        entity.setSize(asset.getSize());
        entity.setUsableSize(asset.getUsableSize());
        return entity;
    }

    private Asset toModel(AssetEntity entity) {
        return new Asset(
                entity.getCustomerId(),
                entity.getAssetName(),
                entity.getSize(),
                entity.getUsableSize()
        );
    }
}