package com.ing.order.handler;

import com.ing.asset.model.Asset;
import com.ing.asset.port.AssetPort;
import com.ing.order.command.CancelOrderCommand;
import com.ing.order.model.Order;
import com.ing.order.port.OrderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class CancelOrderHandler {

    private final OrderPort orderRepository;
    private final AssetPort assetRepository;

    public void handle(CancelOrderCommand command) {
        Order order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be canceled");
        }

        if (!order.getCustomerId().equals(command.getCustomerId())) {
            throw new IllegalArgumentException("Order does not belong to the specified customer");
        }

        // Find the asset before canceling the order
        Optional<Asset> assetOptional = order.getSide() == Order.OrderSide.SELL ?
                assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName()) :
                assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), "TRY");

        if (assetOptional.isEmpty()) {
            throw new IllegalStateException("Asset not found for the order");
        }

        Asset asset = assetOptional.get();

        // Now we can safely cancel the order
        order.setStatus(Order.OrderStatus.CANCELED);
        orderRepository.save(order);

        // Return the reserved amount to the customer's asset
        asset.incrementUsableSize(
                order.getSide() == Order.OrderSide.SELL ? order.getSize() : order.getSize().multiply(order.getPrice())
        );

        assetRepository.save(asset);

        log.info("Order {} canceled for customer {}", command.getOrderId(), command.getCustomerId());
    }
}