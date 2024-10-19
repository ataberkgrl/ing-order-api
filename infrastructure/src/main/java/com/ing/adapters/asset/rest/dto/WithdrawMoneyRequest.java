package com.ing.adapters.asset.rest.dto;

import java.math.BigDecimal;

import com.ing.asset.command.WithdrawMoneyCommand;

public record WithdrawMoneyRequest(
    String customerId,
    BigDecimal amount,
    String iban
) {

    public WithdrawMoneyCommand toModel() {
        return WithdrawMoneyCommand.builder()
            .customerId(customerId)
            .amount(amount)
            .iban(iban)
            .build();
    }

}