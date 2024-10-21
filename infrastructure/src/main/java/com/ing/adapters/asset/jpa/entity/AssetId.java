package com.ing.adapters.asset.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetId implements Serializable {
    private String customerId;
    private String assetName;
}