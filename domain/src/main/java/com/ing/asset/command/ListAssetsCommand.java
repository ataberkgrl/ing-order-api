package com.ing.asset.command;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.Builder;

@Value
@Builder
@AllArgsConstructor
public class ListAssetsCommand {
    String customerId;
}