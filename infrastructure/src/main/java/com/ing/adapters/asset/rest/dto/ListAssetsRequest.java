package com.ing.adapters.asset.rest.dto;

import com.ing.asset.command.ListAssetsCommand;

public record ListAssetsRequest(
    String customerId
) {

    public ListAssetsCommand toModel() {
        return ListAssetsCommand.builder()
            .customerId(customerId)
            .build();
    }

}