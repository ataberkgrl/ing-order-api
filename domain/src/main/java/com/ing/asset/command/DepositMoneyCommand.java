package com.ing.asset.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
@AllArgsConstructor
public class DepositMoneyCommand {
    String customerId;
    BigDecimal amount;
}