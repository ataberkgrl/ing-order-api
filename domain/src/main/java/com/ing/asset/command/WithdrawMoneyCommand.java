package com.ing.asset.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
@AllArgsConstructor
public class WithdrawMoneyCommand {
    String customerId;
    BigDecimal amount;
    String iban;
}