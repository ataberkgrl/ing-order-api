package com.ing.asset.command;

import lombok.Value;
import lombok.Builder;

@Value
@Builder
public class ListAssetsCommand {
    String customerId;
}