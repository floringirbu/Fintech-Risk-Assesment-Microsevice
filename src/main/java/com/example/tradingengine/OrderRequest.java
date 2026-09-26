package com.example.tradingengine;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderRequest(
        String orderId,
        String userId,
        String symbol,
        int quantity,
        BigDecimal price,
        Instant timestamp
) {}

