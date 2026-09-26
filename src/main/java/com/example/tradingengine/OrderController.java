package com.example.tradingengine;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final TradingService tradingService;

    public OrderController(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<RiskEvaluation>> evaluateOrder(@RequestBody OrderRequest order) {
        return tradingService.processOrder(order)
                .thenApply(evaluation -> {
                    if (evaluation.status() == RiskEvaluation.RiskStatus.APPROVED) {
                        return ResponseEntity.ok(evaluation);
                    } else {
                        return ResponseEntity.badRequest().body(evaluation);
                    }
                });
    }
}