package com.ing.order;

import com.ing.asset.model.Asset;
import com.ing.asset.port.AssetPort;
import com.ing.order.command.CancelOrderCommand;
import com.ing.order.handler.CancelOrderHandler;
import com.ing.order.model.Order;
import com.ing.order.port.OrderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelOrderTest {

    @Mock
    private OrderPort orderPort;

    @Mock
    private AssetPort assetPort;

    private CancelOrderHandler cancelOrderHandler;

    @BeforeEach
    void setUp() {
        cancelOrderHandler = new CancelOrderHandler(orderPort, assetPort);
    }

    @Test
    void handle_PendingBuyOrder_Success() {
        // Arrange
        String orderId = "order1";
        String customerId = "customer1";
        CancelOrderCommand command = new CancelOrderCommand(orderId, customerId);

        Order pendingOrder = new Order(customerId, "TUPRS", Order.OrderSide.BUY, BigDecimal.ONE, BigDecimal.valueOf(50000));
        pendingOrder.setId(orderId);
        when(orderPort.findById(orderId)).thenReturn(Optional.of(pendingOrder));

        Asset tryAsset = new Asset(customerId, "TRY", BigDecimal.valueOf(50000), BigDecimal.valueOf(0));
        when(assetPort.findByCustomerIdAndAssetName(customerId, "TRY")).thenReturn(Optional.of(tryAsset));

        // Act
        cancelOrderHandler.handle(command);

        // Assert
        verify(orderPort).save(argThat(order ->
                order.getId().equals(orderId) &&
                        order.getStatus() == Order.OrderStatus.CANCELED
        ));

        verify(assetPort).save(argThat(asset ->
                asset.getCustomerId().equals(customerId) &&
                        asset.getAssetName().equals("TRY") &&
                        asset.getSize().compareTo(BigDecimal.valueOf(100000)) == 0 &&
                        asset.getUsableSize().compareTo(BigDecimal.valueOf(50000)) == 0
        ));
    }

    @Test
    void handle_PendingSellOrder_Success() {
        // Arrange
        String orderId = "order1";
        String customerId = "customer1";
        CancelOrderCommand command = new CancelOrderCommand(orderId, customerId);

        Order pendingOrder = new Order(customerId, "TUPRS", Order.OrderSide.SELL, BigDecimal.ONE, BigDecimal.valueOf(50000));
        pendingOrder.setId(orderId);
        when(orderPort.findById(orderId)).thenReturn(Optional.of(pendingOrder));

        Asset tuprsAsset = new Asset(customerId, "TUPRS", BigDecimal.ZERO, BigDecimal.ZERO);
        when(assetPort.findByCustomerIdAndAssetName(customerId, "TUPRS")).thenReturn(Optional.of(tuprsAsset));

        // Act
        cancelOrderHandler.handle(command);

        // Assert
        verify(orderPort).save(argThat(order ->
                order.getId().equals(orderId) &&
                        order.getStatus() == Order.OrderStatus.CANCELED
        ));

        verify(assetPort).save(argThat(asset ->
                asset.getCustomerId().equals(customerId) &&
                        asset.getAssetName().equals("TUPRS") &&
                        asset.getSize().compareTo(BigDecimal.ONE) == 0 &&
                        asset.getUsableSize().compareTo(BigDecimal.ONE) == 0
        ));
    }

    @Test
    void handle_NonPendingOrder_ThrowsException() {
        // Arrange
        String orderId = "order1";
        String customerId = "customer1";
        CancelOrderCommand command = new CancelOrderCommand(orderId, customerId);

        Order matchedOrder = new Order(customerId, "TUPRS", Order.OrderSide.BUY, BigDecimal.ONE, BigDecimal.valueOf(50000));
        matchedOrder.setId(orderId);
        matchedOrder.setStatus(Order.OrderStatus.MATCHED);
        when(orderPort.findById(orderId)).thenReturn(Optional.of(matchedOrder));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> cancelOrderHandler.handle(command));
        assertEquals("Only pending orders can be canceled", exception.getMessage());

        verify(orderPort, never()).save(any());
        verify(assetPort, never()).save(any());
    }

    @Test
    void handle_OrderNotFound_ThrowsException() {
        // Arrange
        String orderId = "order1";
        String customerId = "customer1";
        CancelOrderCommand command = new CancelOrderCommand(orderId, customerId);

        when(orderPort.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> cancelOrderHandler.handle(command));
        assertEquals("Order not found", exception.getMessage());

        verify(orderPort, never()).save(any());
        verify(assetPort, never()).save(any());
    }

    @Test
    void handle_OrderBelongsToAnotherCustomer_ThrowsException() {
        // Arrange
        String orderId = "order1";
        String customerId = "customer1";
        String anotherCustomerId = "customer2";
        CancelOrderCommand command = new CancelOrderCommand(orderId, customerId);

        Order pendingOrder = new Order(anotherCustomerId, "TUPRS", Order.OrderSide.BUY, BigDecimal.ONE, BigDecimal.valueOf(50000));
        pendingOrder.setId(orderId);
        when(orderPort.findById(orderId)).thenReturn(Optional.of(pendingOrder));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> cancelOrderHandler.handle(command));
        assertEquals("Order does not belong to the specified customer", exception.getMessage());

        verify(orderPort, never()).save(any());
        verify(assetPort, never()).save(any());
    }

    @Test
    void handle_AssetNotFound_ThrowsException() {
        // Arrange
        String orderId = "order1";
        String customerId = "customer1";
        CancelOrderCommand command = new CancelOrderCommand(orderId, customerId);

        Order pendingOrder = new Order(customerId, "TUPRS", Order.OrderSide.BUY, BigDecimal.ONE, BigDecimal.valueOf(50000));
        pendingOrder.setId(orderId);
        when(orderPort.findById(orderId)).thenReturn(Optional.of(pendingOrder));

        when(assetPort.findByCustomerIdAndAssetName(customerId, "TRY")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> cancelOrderHandler.handle(command));

        verify(orderPort, never()).save(any());
        verify(assetPort, never()).save(any());
    }
}