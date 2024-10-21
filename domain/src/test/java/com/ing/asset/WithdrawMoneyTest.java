package com.ing.asset;

import com.ing.asset.command.WithdrawMoneyCommand;
import com.ing.asset.handler.WithdrawMoneyHandler;
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
class WithdrawMoneyTest {

    @Mock
    private AssetPort assetPort;

    private WithdrawMoneyHandler withdrawMoneyHandler;

    @BeforeEach
    void setUp() {
        withdrawMoneyHandler = new WithdrawMoneyHandler(assetPort);
    }

    @Test
    void handle_SufficientFunds_Success() {
        // Arrange
        String customerId = "customer1";
        BigDecimal initialBalance = BigDecimal.valueOf(1000);
        BigDecimal withdrawAmount = BigDecimal.valueOf(500);
        String iban = "NL91ABNA0417164300";
        WithdrawMoneyCommand command = new WithdrawMoneyCommand(customerId, withdrawAmount, iban);

        Asset existingAsset = new Asset(customerId, "TRY", initialBalance, initialBalance);
        when(assetPort.findByCustomerIdAndAssetName(customerId, "TRY")).thenReturn(Optional.of(existingAsset));

        // Act
        withdrawMoneyHandler.handle(command);

        // Assert
        verify(assetPort).save(argThat(asset ->
                asset.getCustomerId().equals(customerId) &&
                        asset.getAssetName().equals("TRY") &&
                        asset.getSize().compareTo(initialBalance.subtract(withdrawAmount)) == 0 &&
                        asset.getUsableSize().compareTo(initialBalance.subtract(withdrawAmount)) == 0
        ));
    }

    @Test
    void handle_InsufficientFunds_ThrowsException() {
        // Arrange
        String customerId = "customer1";
        BigDecimal initialBalance = BigDecimal.valueOf(400);
        BigDecimal withdrawAmount = BigDecimal.valueOf(500);
        String iban = "NL91ABNA0417164300";
        WithdrawMoneyCommand command = new WithdrawMoneyCommand(customerId, withdrawAmount, iban);

        Asset existingAsset = new Asset(customerId, "TRY", initialBalance, initialBalance);
        when(assetPort.findByCustomerIdAndAssetName(customerId, "TRY")).thenReturn(Optional.of(existingAsset));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> withdrawMoneyHandler.handle(command));
        assertEquals("Insufficient funds", exception.getMessage());
        verify(assetPort, never()).save(any());
    }

    @Test
    void handle_AssetNotFound_ThrowsException() {
        // Arrange
        String customerId = "customer1";
        BigDecimal withdrawAmount = BigDecimal.valueOf(500);
        String iban = "NL91ABNA0417164300";
        WithdrawMoneyCommand command = new WithdrawMoneyCommand(customerId, withdrawAmount, iban);

        when(assetPort.findByCustomerIdAndAssetName(customerId, "TRY")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> withdrawMoneyHandler.handle(command));
        assertEquals("No TRY asset found for customer", exception.getMessage());
        verify(assetPort, never()).save(any());
    }

    @Test
    void handle_NegativeAmount_ThrowsException() {
        // Arrange
        String customerId = "customer1";
        BigDecimal withdrawAmount = BigDecimal.valueOf(-500);
        String iban = "NL91ABNA0417164300";
        WithdrawMoneyCommand command = new WithdrawMoneyCommand(customerId, withdrawAmount, iban);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> withdrawMoneyHandler.handle(command));
        assertEquals("Withdraw amount must be positive", exception.getMessage());
        verify(assetPort, never()).findByCustomerIdAndAssetName(any(), any());
        verify(assetPort, never()).save(any());
    }

    @Test
    void handle_ZeroAmount_ThrowsException() {
        // Arrange
        String customerId = "customer1";
        BigDecimal withdrawAmount = BigDecimal.ZERO;
        String iban = "NL91ABNA0417164300";
        WithdrawMoneyCommand command = new WithdrawMoneyCommand(customerId, withdrawAmount, iban);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> withdrawMoneyHandler.handle(command));
        assertEquals("Withdraw amount must be positive", exception.getMessage());
        verify(assetPort, never()).findByCustomerIdAndAssetName(any(), any());
        verify(assetPort, never()).save(any());
    }

    @Test
    void handle_InvalidIBAN_ThrowsException() {
        // Arrange
        String customerId = "customer1";
        BigDecimal withdrawAmount = BigDecimal.valueOf(500);
        String invalidIban = "invalid_iban";
        WithdrawMoneyCommand command = new WithdrawMoneyCommand(customerId, withdrawAmount, invalidIban);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> withdrawMoneyHandler.handle(command));
        assertEquals("Invalid IBAN", exception.getMessage());
        verify(assetPort, never()).findByCustomerIdAndAssetName(any(), any());
        verify(assetPort, never()).save(any());
    }

    @Test
    void handle_WithdrawEntireBalance_Success() {
        // Arrange
        String customerId = "customer1";
        BigDecimal initialBalance = BigDecimal.valueOf(1000);
        BigDecimal withdrawAmount = BigDecimal.valueOf(1000);
        String iban = "NL91ABNA0417164300";
        WithdrawMoneyCommand command = new WithdrawMoneyCommand(customerId, withdrawAmount, iban);

        Asset existingAsset = new Asset(customerId, "TRY", initialBalance, initialBalance);
        when(assetPort.findByCustomerIdAndAssetName(customerId, "TRY")).thenReturn(Optional.of(existingAsset));

        // Act
        withdrawMoneyHandler.handle(command);

        // Assert
        verify(assetPort).save(argThat(asset ->
                asset.getCustomerId().equals(customerId) &&
                        asset.getAssetName().equals("TRY") &&
                        asset.getSize().compareTo(BigDecimal.ZERO) == 0 &&
                        asset.getUsableSize().compareTo(BigDecimal.ZERO) == 0
        ));
    }
}