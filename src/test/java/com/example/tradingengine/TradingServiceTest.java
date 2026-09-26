package com.example.tradingengine;

import com.example.tradingengine.entity.OrderEntity;
import com.example.tradingengine.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TradingServiceTest {

    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private OrderRepository orderRepository;
    private TradingService tradingService;

    @BeforeEach
    void setUp() {
        redisTemplate = Mockito.mock(StringRedisTemplate.class);
        valueOperations = Mockito.mock(ValueOperations.class);
        orderRepository = Mockito.mock(OrderRepository.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        tradingService = new TradingService(redisTemplate, orderRepository);
    }

    @Test
    void testProcessOrder_Approved() throws ExecutionException, InterruptedException {
        OrderRequest request = new OrderRequest("ORD-1", "user_1", "AAPL", 10, new BigDecimal("100.00"), Instant.now());

        when(valueOperations.get("user:exposure:user_1")).thenReturn("1000.00");

        CompletableFuture<RiskEvaluation> future = tradingService.processOrder(request);
        RiskEvaluation result = future.get();

        assertEquals(RiskEvaluation.RiskStatus.APPROVED, result.status());
    }
}