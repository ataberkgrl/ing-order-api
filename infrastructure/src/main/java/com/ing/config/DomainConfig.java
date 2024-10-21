package com.ing.config;

import com.ing.asset.handler.ListAssetsHandler;
import com.ing.asset.port.AssetPort;
import com.ing.order.handler.CancelOrderHandler;
import com.ing.order.handler.CreateOrderHandler;
import com.ing.asset.handler.DepositMoneyHandler;
import com.ing.order.handler.ListOrdersHandler;
import com.ing.asset.handler.WithdrawMoneyHandler;
import com.ing.order.port.OrderPort;
import com.ing.user.port.UserPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public CreateOrderHandler createOrderHandler(OrderPort orderPort, AssetPort assetPort) {
        return new CreateOrderHandler(assetPort, orderPort);
    }

    @Bean
    public ListOrdersHandler listOrdersHandler(OrderPort orderPort) {
        return new ListOrdersHandler(orderPort);
    }

    @Bean
    public CancelOrderHandler cancelOrderHandler(OrderPort orderPort, AssetPort assetPort) {
        return new CancelOrderHandler(orderPort, assetPort);
    }

    @Bean
    public DepositMoneyHandler depositMoneyHandler(AssetPort assetPort, UserPort userPort) {
        return new DepositMoneyHandler(assetPort, userPort);
    }

    @Bean
    public WithdrawMoneyHandler withdrawMoneyHandler(AssetPort assetPort) {
        return new WithdrawMoneyHandler(assetPort);
    }

    @Bean
    public ListAssetsHandler listAssetsHandler(AssetPort assetPort) {
        return new ListAssetsHandler(assetPort);
    }
}