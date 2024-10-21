package com.ing.asset;

import com.ing.asset.command.ListAssetsCommand;
import com.ing.asset.handler.ListAssetsHandler;
import com.ing.asset.model.Asset;
import com.ing.asset.port.AssetPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListAssetsTest {

    @Mock
    private AssetPort assetPort;

    private ListAssetsHandler listAssetsHandler;

    @BeforeEach
    void setUp() {
        listAssetsHandler = new ListAssetsHandler(assetPort);
    }

    @Test
    void handle_AssetsExist_ReturnsAssetList() {
        // Arrange
        String customerId = "customer1";
        ListAssetsCommand command = new ListAssetsCommand(customerId);

        Asset asset1 = new Asset(customerId, "TRY", BigDecimal.valueOf(1000), BigDecimal.valueOf(1000));
        Asset asset2 = new Asset(customerId, "TUPRS", BigDecimal.valueOf(0.5), BigDecimal.valueOf(0.5));
        List<Asset> expectedAssets = Arrays.asList(asset1, asset2);

        when(assetPort.findAllByCustomerId(customerId)).thenReturn(expectedAssets);

        // Act
        List<Asset> result = listAssetsHandler.handle(command);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedAssets, result);
        verify(assetPort).findAllByCustomerId(customerId);
    }

    @Test
    void handle_NoAssets_ReturnsEmptyList() {
        // Arrange
        String customerId = "customer1";
        ListAssetsCommand command = new ListAssetsCommand(customerId);

        when(assetPort.findAllByCustomerId(customerId)).thenReturn(Collections.emptyList());

        // Act
        List<Asset> result = listAssetsHandler.handle(command);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(assetPort).findAllByCustomerId(customerId);
    }

    @Test
    void handle_NullCustomerId_ThrowsIllegalArgumentException() {
        // Arrange
        ListAssetsCommand command = new ListAssetsCommand(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> listAssetsHandler.handle(command));
        assertEquals("Customer ID cannot be null", exception.getMessage());
        verify(assetPort, never()).findAllByCustomerId(any());
    }

    @Test
    void handle_EmptyCustomerId_ThrowsIllegalArgumentException() {
        // Arrange
        ListAssetsCommand command = new ListAssetsCommand("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> listAssetsHandler.handle(command));
        assertEquals("Customer ID cannot be empty", exception.getMessage());
        verify(assetPort, never()).findAllByCustomerId(any());
    }

    @Test
    void handle_LargeNumberOfAssets_ReturnsAllAssets() {
        // Arrange
        String customerId = "customer1";
        ListAssetsCommand command = new ListAssetsCommand(customerId);

        List<Asset> expectedAssets = createLargeAssetList(customerId, 1000);

        when(assetPort.findAllByCustomerId(customerId)).thenReturn(expectedAssets);

        // Act
        List<Asset> result = listAssetsHandler.handle(command);

        // Assert
        assertNotNull(result);
        assertEquals(1000, result.size());
        assertEquals(expectedAssets, result);
        verify(assetPort).findAllByCustomerId(customerId);
    }

    private List<Asset> createLargeAssetList(String customerId, int count) {
        List<Asset> assets = new java.util.ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            assets.add(new Asset(customerId, "ASSET" + i, BigDecimal.valueOf(i), BigDecimal.valueOf(i)));
        }
        return assets;
    }
}