package com.ing.adapters.asset.rest;

import com.ing.adapters.asset.rest.dto.AssetResponse;
import com.ing.adapters.asset.rest.dto.DepositMoneyRequest;
import com.ing.adapters.asset.rest.dto.ListAssetsRequest;
import com.ing.adapters.asset.rest.dto.WithdrawMoneyRequest;
import com.ing.asset.handler.DepositMoneyHandler;
import com.ing.asset.handler.ListAssetsHandler;
import com.ing.asset.handler.WithdrawMoneyHandler;
import com.ing.asset.model.Asset;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final DepositMoneyHandler depositMoneyHandler;
    private final WithdrawMoneyHandler withdrawMoneyHandler;
    private final ListAssetsHandler listAssetsHandler;

    @PostMapping("/deposit")
    public ResponseEntity<Void> depositMoney(@RequestBody DepositMoneyRequest request) {

        depositMoneyHandler.handle(request.toModel());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdrawMoney(@RequestBody WithdrawMoneyRequest request) {

        withdrawMoneyHandler.handle(request.toModel());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<AssetResponse>> listAssets(@RequestBody ListAssetsRequest request) {

        List<Asset> assets = listAssetsHandler.handle(request.toModel());

        List<AssetResponse> assetResponses = assets.stream()
            .map(AssetResponse::fromModel)
            .collect(Collectors.toList());

        return ResponseEntity.ok(assetResponses);
    }
}