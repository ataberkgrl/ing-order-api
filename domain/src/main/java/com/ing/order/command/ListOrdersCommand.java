package com.ing.order.command;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ListOrdersCommand {
    String customerId;
    LocalDateTime startDate;
    LocalDateTime endDate;
}