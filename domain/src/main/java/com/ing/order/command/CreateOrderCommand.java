package com.ing.order.command;

import com.ing.common.model.Command;
import com.ing.order.model.Order;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CreateOrderCommand implements Command {
    String customerId;
    String assetName;
    Order.OrderSide side;
    BigDecimal size;
    BigDecimal price;
}