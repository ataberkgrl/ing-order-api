package com.ing.asset.handler;

import com.ing.asset.model.Asset;
import com.ing.asset.port.AssetPort;
import com.ing.asset.command.DepositMoneyCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DepositMoneyHandler {

    private final AssetPort assetRepository;

    public void handle(DepositMoneyCommand command) {

        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(command.getCustomerId(), "TRY")
            .orElse(new Asset(command.getCustomerId(), "TRY", command.getAmount(), command.getAmount()));

        tryAsset.incrementSize(command.getAmount());
        assetRepository.save(tryAsset);

        log.info("Deposited {} TRY for customer {}", command.getAmount(), command.getCustomerId());
    }

}