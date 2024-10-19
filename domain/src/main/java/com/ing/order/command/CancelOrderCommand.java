package com.ing.order.command;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CancelOrderCommand {
    String orderId;
    String customerId;
}