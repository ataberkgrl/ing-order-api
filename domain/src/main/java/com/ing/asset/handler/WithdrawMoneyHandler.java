package com.ing.asset.handler;

import com.ing.asset.model.Asset;
import com.ing.asset.port.AssetPort;
import com.ing.asset.command.WithdrawMoneyCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
public class WithdrawMoneyHandler {

    private final AssetPort assetRepository;

    public void handle(WithdrawMoneyCommand command) {
        validateWithdrawAmount(command.getAmount());
        validateIBAN(command.getIban());

        Asset tryAsset = assetRepository.findByCustomerIdAndAssetName(command.getCustomerId(), "TRY")
                .orElseThrow(() -> new IllegalArgumentException("No TRY asset found for customer"));

        if (tryAsset.getUsableSize().compareTo(command.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        tryAsset.decrementSize(command.getAmount());
        assetRepository.save(tryAsset);

        log.info(
                "Withdrawn {} TRY for customer {} to IBAN {}",
                command.getAmount(),
                command.getCustomerId(),
                command.getIban()
        );
    }

    private void validateWithdrawAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be positive");
        }
    }

    private void validateIBAN(String iban) {
        if (iban == null || iban.length() < 15 || iban.length() > 34) {
            throw new IllegalArgumentException("Invalid IBAN");
        }
    }
}