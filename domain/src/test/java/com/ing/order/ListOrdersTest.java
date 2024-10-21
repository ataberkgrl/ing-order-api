package com.ing.order;

import com.ing.order.command.ListOrdersCommand;
import com.ing.order.handler.ListOrdersHandler;
import com.ing.order.model.Order;
import com.ing.order.port.OrderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListOrdersTest {

    @Mock
    private OrderPort orderPort;

    private ListOrdersHandler listOrdersHandler;

    @BeforeEach
    void setUp() {
        listOrdersHandler = new ListOrdersHandler(orderPort);
    }

    @Test
    void handle_OrdersExist_ReturnsOrderList() {
        // Arrange
        String customerId = "customer1";
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        ListOrdersCommand command = new ListOrdersCommand(customerId, startDate, endDate);

        Order order1 = new Order(customerId, "TUPRS", Order.OrderSide.BUY, BigDecimal.ONE, BigDecimal.valueOf(50000));
        Order order2 = new Order(customerId, "ETH", Order.OrderSide.SELL, BigDecimal.valueOf(2), BigDecimal.valueOf(3000));
        List<Order> expectedOrders = Arrays.asList(order1, order2);

        when(orderPort.findByCustomerIdAndDateRange(customerId, startDate, endDate)).thenReturn(expectedOrders);

        // Act
        List<Order> result = listOrdersHandler.handle(command);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedOrders, result);
        verify(orderPort).findByCustomerIdAndDateRange(customerId, startDate, endDate);
    }

    @Test
    void handle_NoOrders_ReturnsEmptyList() {
        // Arrange
        String customerId = "customer1";
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        ListOrdersCommand command = new ListOrdersCommand(customerId, startDate, endDate);

        when(orderPort.findByCustomerIdAndDateRange(customerId, startDate, endDate)).thenReturn(Collections.emptyList());

        // Act
        List<Order> result = listOrdersHandler.handle(command);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderPort).findByCustomerIdAndDateRange(customerId, startDate, endDate);
    }

    @Test
    void handle_NullDateRange_UsesDefaultDateRange() {
        // Arrange
        String customerId = "customer1";
        ListOrdersCommand command = new ListOrdersCommand(customerId, null, null);

        Order order = new Order(customerId, "TUPRS", Order.OrderSide.BUY, BigDecimal.ONE, BigDecimal.valueOf(50000));
        List<Order> expectedOrders = Collections.singletonList(order);

        // We expect the handler to use a default date range when null is provided
        when(orderPort.findByCustomerIdAndDateRange(eq(customerId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedOrders);

        // Act
        List<Order> result = listOrdersHandler.handle(command);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedOrders, result);
        verify(orderPort).findByCustomerIdAndDateRange(eq(customerId), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void handle_InvalidDateRange_ThrowsIllegalArgumentException() {
        // Arrange
        String customerId = "customer1";
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().minusDays(7); // End date before start date
        ListOrdersCommand command = new ListOrdersCommand(customerId, startDate, endDate);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> listOrdersHandler.handle(command));
        assertEquals("End date must be after start date", exception.getMessage());
        verify(orderPort, never()).findByCustomerIdAndDateRange(any(), any(), any());
    }

    @Test
    void handle_LargeDateRange_ThrowsIllegalArgumentException() {
        // Arrange
        String customerId = "customer1";
        LocalDateTime startDate = LocalDateTime.now().minusYears(2);
        LocalDateTime endDate = LocalDateTime.now();
        ListOrdersCommand command = new ListOrdersCommand(customerId, startDate, endDate);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> listOrdersHandler.handle(command));
        assertEquals("Date range must not exceed 1 year", exception.getMessage());
        verify(orderPort, never()).findByCustomerIdAndDateRange(any(), any(), any());
    }
}