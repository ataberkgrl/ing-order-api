package com.ing.asset.port;

import java.util.List;
import java.util.Optional;

import com.ing.asset.model.Asset;

public interface AssetPort {

    Asset save(Asset asset);

    Optional<Asset> findByCustomerIdAndAssetName(String customerId, String assetName);

    List<Asset> findAllByCustomerId(String customerId);

}