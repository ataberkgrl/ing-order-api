package com.ing.asset.handler;

import com.ing.asset.command.ListAssetsCommand;
import com.ing.asset.model.Asset;
import com.ing.asset.port.AssetPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ListAssetsHandler {

    private final AssetPort assetRepository;

    public List<Asset> handle(ListAssetsCommand command) {
        List<Asset> assets = assetRepository.findAllByCustomerId(command.getCustomerId());

        log.info("Retrieved {} assets for customer {}", assets.size(), command.getCustomerId());

        return assets;
    }
}