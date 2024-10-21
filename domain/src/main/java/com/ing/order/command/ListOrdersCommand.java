package com.ing.order.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
@AllArgsConstructor
public class ListOrdersCommand {
    String customerId;
    LocalDateTime startDate;
    LocalDateTime endDate;
}