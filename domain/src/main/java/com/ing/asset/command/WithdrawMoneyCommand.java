package com.ing.asset.command;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class WithdrawMoneyCommand {
    String customerId;
    BigDecimal amount;
    String iban;
}