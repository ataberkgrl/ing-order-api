package com.ing.order.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class CancelOrderCommand {
    String orderId;
    String customerId;
}