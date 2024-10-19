package com.ing.adapters.asset.rest.dto;

import java.math.BigDecimal;

import com.ing.asset.model.Asset;

public record AssetResponse(
    String customerId,
    String assetName,
    BigDecimal size,
    BigDecimal usableSize
) {

    public static AssetResponse fromModel(Asset asset) {
        return new AssetResponse(
            asset.getCustomerId(),
            asset.getAssetName(),
            asset.getSize(),
            asset.getUsableSize()
        );
    }

}