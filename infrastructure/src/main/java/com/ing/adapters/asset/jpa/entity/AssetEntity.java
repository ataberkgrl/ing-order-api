package com.ing.adapters.asset.jpa.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "assets")
@Data
@IdClass(AssetId.class)
public class AssetEntity {
    @Id
    @Column(nullable = false)
    private String customerId;

    @Id
    @Column(nullable = false)
    private String assetName;

    @Column(nullable = false)
    private BigDecimal size;

    @Column(nullable = false)
    private BigDecimal usableSize;
}
