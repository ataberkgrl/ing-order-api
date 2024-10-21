package com.ing.asset.handler;

import com.ing.asset.model.Asset;
import com.ing.asset.port.AssetPort;
import com.ing.asset.command.DepositMoneyCommand;
import com.ing.user.port.UserPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
public class DepositMoneyHandler {

    private final AssetPort assetRepository;
    private final UserPort userRepository;

    public void handle(DepositMoneyCommand command) {
        validateDepositAmount(command.getAmount());
        validateUserExists(command.getCustomerId());

        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(command.getCustomerId(), "TRY")
                .orElse(new Asset(command.getCustomerId(), "TRY", BigDecimal.ZERO, BigDecimal.ZERO));

        tryAsset.incrementSize(command.getAmount());
        assetRepository.save(tryAsset);

        log.info("Deposited {} TRY for customer {}", command.getAmount(), command.getCustomerId());
    }

    private void validateDepositAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
    }

    private void validateUserExists(String customerId) {
        if (userRepository.findById(customerId).isEmpty()) {
            throw new IllegalArgumentException("User does not exist");
        }
    }
}