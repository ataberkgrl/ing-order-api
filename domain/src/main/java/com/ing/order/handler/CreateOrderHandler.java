package com.ing.order.handler;

import com.ing.asset.model.Asset;
import com.ing.asset.port.AssetPort;
import com.ing.order.command.CreateOrderCommand;
import com.ing.order.model.Order;
import com.ing.order.port.OrderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
public class CreateOrderHandler {

    private final AssetPort assetRepository;

    private final OrderPort orderRepository;

    public Order handle(CreateOrderCommand useCase) {

        Asset asset;

        if (useCase.getSide() == Order.OrderSide.SELL) {
            asset = assetRepository.findByCustomerIdAndAssetName(useCase.getCustomerId(), useCase.getAssetName())
                .orElseThrow(() -> new IllegalArgumentException("Insufficient asset balance"));
        } else {
            asset = assetRepository.findByCustomerIdAndAssetName(useCase.getCustomerId(), "TRY")
                .orElseThrow(() -> new IllegalArgumentException("Insufficient TRY balance"));
        }

        BigDecimal requiredAmount = useCase.getSide() == Order.OrderSide.SELL ?
            useCase.getSize() :
            useCase.getSize().multiply(useCase.getPrice());

        if (asset.getUsableSize().compareTo(requiredAmount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        asset.decrementSize(requiredAmount);
        assetRepository.save(asset);

        Order order = new Order(useCase.getCustomerId(),
            useCase.getAssetName(),
            useCase.getSide(),
            useCase.getSize(),
            useCase.getPrice()
        );

        Order savedOrder = orderRepository.save(order);

        log.info(
            "Order created: {} {} {} at {} for customer {}",
            useCase.getSide(),
            useCase.getSize(),
            useCase.getAssetName(),
            useCase.getPrice(),
            useCase.getCustomerId()
        );

        return savedOrder;
    }

}