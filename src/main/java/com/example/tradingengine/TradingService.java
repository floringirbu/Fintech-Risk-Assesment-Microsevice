package com.example.tradingengine;

import com.example.tradingengine.entity.OrderEntity;
import com.example.tradingengine.repository.OrderRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TradingService {

    private final ConcurrentHashMap<String, BigDecimal> userExposureMap = new ConcurrentHashMap<>();
    private final OrderRepository orderRepository;
    private static final BigDecimal MAX_EXPOSURE_LIMIT = new BigDecimal("10000.00");

    public TradingService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Async
    @Transactional
    public CompletableFuture<RiskEvaluation> processOrder(OrderRequest order) {
        BigDecimal orderValue = order.price().multiply(BigDecimal.valueOf(order.quantity()));

        BigDecimal currentExposure = userExposureMap.getOrDefault(order.userId(), BigDecimal.ZERO);
        BigDecimal projectedExposure = currentExposure.add(orderValue);

        RiskEvaluation evaluation;
        OrderEntity.Status entityStatus;
        String reason;

        if (projectedExposure.compareTo(MAX_EXPOSURE_LIMIT) > 0) {
            entityStatus = OrderEntity.Status.REJECTED;
            reason = "Exposure limit exceeded.";
            evaluation = new RiskEvaluation(order.orderId(), RiskEvaluation.RiskStatus.REJECTED, reason);
        } else {
            userExposureMap.put(order.userId(), projectedExposure);
            entityStatus = OrderEntity.Status.APPROVED;
            reason = "Passed risk checks.";
            evaluation = new RiskEvaluation(order.orderId(), RiskEvaluation.RiskStatus.APPROVED, reason);
        }

        OrderEntity auditRecord = OrderEntity.builder()
                .orderId(order.orderId())
                .userId(order.userId())
                .symbol(order.symbol())
                .quantity(order.quantity())
                .price(order.price())
                .status(entityStatus)
                .reason(reason)
                .timestamp(order.timestamp())
                .build();

        orderRepository.save(auditRecord);

        return CompletableFuture.completedFuture(evaluation);
    }
}