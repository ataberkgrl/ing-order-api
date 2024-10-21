package com.ing.asset;

import com.ing.asset.command.DepositMoneyCommand;
import com.ing.asset.handler.DepositMoneyHandler;
import com.ing.asset.model.Asset;
import com.ing.asset.port.AssetPort;
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
class DepositMoneyTest {

    @Mock
    private AssetPort assetPort;

    private DepositMoneyHandler depositMoneyHandler;

    @BeforeEach
    void setUp() {
        depositMoneyHandler = new DepositMoneyHandler(assetPort);
    }

    @Test
    void handle_NewAsset_Success() {
        // Arrange
        String customerId = "customer1";
        BigDecimal amount = BigDecimal.valueOf(1000);
        DepositMoneyCommand command = new DepositMoneyCommand(customerId, amount);

        when(assetPort.findByCustomerIdAndAssetName(customerId, "TRY")).thenReturn(Optional.empty());

        // Act
        depositMoneyHandler.handle(command);

        // Assert
        verify(assetPort).save(argThat(asset ->
                asset.getCustomerId().equals(customerId) &&
                        asset.getAssetName().equals("TRY") &&
                        asset.getSize().compareTo(amount) == 0 &&
                        asset.getUsableSize().compareTo(amount) == 0
        ));
    }

    @Test
    void handle_ExistingAsset_Success() {
        // Arrange
        String customerId = "customer1";
        BigDecimal existingAmount = BigDecimal.valueOf(500);
        BigDecimal depositAmount = BigDecimal.valueOf(1000);
        DepositMoneyCommand command = new DepositMoneyCommand(customerId, depositAmount);

        Asset existingAsset = new Asset(customerId, "TRY", existingAmount, existingAmount);
        when(assetPort.findByCustomerIdAndAssetName(customerId, "TRY")).thenReturn(Optional.of(existingAsset));

        // Act
        depositMoneyHandler.handle(command);

        // Assert
        verify(assetPort).save(argThat(asset ->
                asset.getCustomerId().equals(customerId) &&
                        asset.getAssetName().equals("TRY") &&
                        asset.getSize().compareTo(existingAmount.add(depositAmount)) == 0 &&
                        asset.getUsableSize().compareTo(existingAmount.add(depositAmount)) == 0
        ));
    }

    @Test
    void handle_NegativeAmount_ThrowsIllegalArgumentException() {
        // Arrange
        String customerId = "customer1";
        BigDecimal negativeAmount = BigDecimal.valueOf(-100);
        DepositMoneyCommand command = new DepositMoneyCommand(customerId, negativeAmount);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> depositMoneyHandler.handle(command));
        assertEquals("Deposit amount must be positive", exception.getMessage());
        verify(assetPort, never()).save(any());
    }

    @Test
    void handle_ZeroAmount_ThrowsIllegalArgumentException() {
        // Arrange
        String customerId = "customer1";
        BigDecimal zeroAmount = BigDecimal.ZERO;
        DepositMoneyCommand command = new DepositMoneyCommand(customerId, zeroAmount);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> depositMoneyHandler.handle(command));
        assertEquals("Deposit amount must be positive", exception.getMessage());
        verify(assetPort, never()).save(any());
    }

    @Test
    void handle_LargeAmount_Success() {
        // Arrange
        String customerId = "customer1";
        BigDecimal largeAmount = BigDecimal.valueOf(1_000_000_000); // 1 billion
        DepositMoneyCommand command = new DepositMoneyCommand(customerId, largeAmount);

        when(assetPort.findByCustomerIdAndAssetName(customerId, "TRY")).thenReturn(Optional.empty());

        // Act
        depositMoneyHandler.handle(command);

        // Assert
        verify(assetPort).save(argThat(asset ->
                asset.getCustomerId().equals(customerId) &&
                        asset.getAssetName().equals("TRY") &&
                        asset.getSize().compareTo(largeAmount) == 0 &&
                        asset.getUsableSize().compareTo(largeAmount) == 0
        ));
    }

    @Test
    void handle_MultipleDeposits_Success() {
        // Arrange
        String customerId = "customer1";
        BigDecimal initialAmount = BigDecimal.valueOf(1000);
        BigDecimal firstDeposit = BigDecimal.valueOf(500);
        BigDecimal secondDeposit = BigDecimal.valueOf(750);

        Asset initialAsset = new Asset(customerId, "TRY", initialAmount, initialAmount);
        Asset afterFirstDeposit = new Asset(customerId, "TRY", initialAmount.add(firstDeposit), initialAmount.add(firstDeposit));

        when(assetPort.findByCustomerIdAndAssetName(customerId, "TRY"))
                .thenReturn(Optional.of(initialAsset))
                .thenReturn(Optional.of(afterFirstDeposit));

        // Act
        depositMoneyHandler.handle(new DepositMoneyCommand(customerId, firstDeposit));
        depositMoneyHandler.handle(new DepositMoneyCommand(customerId, secondDeposit));

        // Assert
        verify(assetPort, times(2)).save(any());

        verify(assetPort).save(argThat(asset ->
                asset.getCustomerId().equals(customerId) &&
                        asset.getAssetName().equals("TRY") &&
                        asset.getSize().compareTo(initialAmount.add(firstDeposit)) == 0 &&
                        asset.getUsableSize().compareTo(initialAmount.add(firstDeposit)) == 0
        ));

        verify(assetPort).save(argThat(asset ->
                asset.getCustomerId().equals(customerId) &&
                        asset.getAssetName().equals("TRY") &&
                        asset.getSize().compareTo(initialAmount.add(firstDeposit).add(secondDeposit)) == 0 &&
                        asset.getUsableSize().compareTo(initialAmount.add(firstDeposit).add(secondDeposit)) == 0
        ));
    }
}