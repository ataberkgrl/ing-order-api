package com.ing.order;

import com.ing.asset.model.Asset;
import com.ing.asset.port.AssetPort;
import com.ing.order.command.CreateOrderCommand;
import com.ing.order.handler.CreateOrderHandler;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrderTest {

    @Mock
    private OrderPort orderPort;

    @Mock
    private AssetPort assetPort;

    private CreateOrderHandler createOrderHandler;

    @BeforeEach
    void setUp() {
        createOrderHandler = new CreateOrderHandler(assetPort, orderPort);
    }

    @Test
    void handle_BuyOrder_Success() {
        // Arrange
        CreateOrderCommand command = CreateOrderCommand.builder()
                .customerId("customer1")
                .assetName("TUPRS")
                .side(Order.OrderSide.BUY)
                .size(BigDecimal.ONE)
                .price(BigDecimal.valueOf(50000))
                .build();

        Asset tryAsset = new Asset("customer1", "TRY", BigDecimal.valueOf(100000), BigDecimal.valueOf(100000));
        when(assetPort.findByCustomerIdAndAssetName("customer1", "TRY")).thenReturn(Optional.of(tryAsset));
        when(orderPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order result = createOrderHandler.handle(command);

        // Assert
        assertNotNull(result);
        assertEquals("customer1", result.getCustomerId());
        assertEquals("TUPRS", result.getAssetName());
        assertEquals(Order.OrderSide.BUY, result.getSide());
        assertEquals(BigDecimal.ONE, result.getSize());
        assertEquals(BigDecimal.valueOf(50000), result.getPrice());
        assertEquals(Order.OrderStatus.PENDING, result.getStatus());

        verify(assetPort).save(argThat(asset ->
                asset.getCustomerId().equals("customer1") &&
                        asset.getAssetName().equals("TRY") &&
                        asset.getSize().compareTo(BigDecimal.valueOf(50000)) == 0 &&
                        asset.getUsableSize().compareTo(BigDecimal.valueOf(50000)) == 0
        ));
    }

    @Test
    void handle_SellOrder_Success() {
        // Arrange
        CreateOrderCommand command = CreateOrderCommand.builder()
                .customerId("customer1")
                .assetName("TUPRS")
                .side(Order.OrderSide.SELL)
                .size(BigDecimal.ONE)
                .price(BigDecimal.valueOf(50000))
                .build();

        Asset tuprsAsset = new Asset("customer1", "TUPRS", BigDecimal.valueOf(2), BigDecimal.valueOf(2));
        when(assetPort.findByCustomerIdAndAssetName("customer1", "TUPRS")).thenReturn(Optional.of(tuprsAsset));
        when(orderPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order result = createOrderHandler.handle(command);

        // Assert
        assertNotNull(result);
        assertEquals("customer1", result.getCustomerId());
        assertEquals("TUPRS", result.getAssetName());
        assertEquals(Order.OrderSide.SELL, result.getSide());
        assertEquals(BigDecimal.ONE, result.getSize());
        assertEquals(BigDecimal.valueOf(50000), result.getPrice());
        assertEquals(Order.OrderStatus.PENDING, result.getStatus());

        verify(assetPort).save(argThat(asset ->
                asset.getCustomerId().equals("customer1") &&
                        asset.getAssetName().equals("TUPRS") &&
                        asset.getSize().compareTo(BigDecimal.ONE) == 0 &&
                        asset.getUsableSize().compareTo(BigDecimal.ONE) == 0
        ));
    }

    @Test
    void handle_InsufficientBalance_ThrowsException() {
        // Arrange
        CreateOrderCommand command = CreateOrderCommand.builder()
                .customerId("customer1")
                .assetName("TUPRS")
                .side(Order.OrderSide.BUY)
                .size(BigDecimal.ONE)
                .price(BigDecimal.valueOf(50000))
                .build();

        Asset tryAsset = new Asset("customer1", "TRY", BigDecimal.valueOf(40000), BigDecimal.valueOf(40000));
        when(assetPort.findByCustomerIdAndAssetName("customer1", "TRY")).thenReturn(Optional.of(tryAsset));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> createOrderHandler.handle(command));
    }
}