package com.example.tradingengine;

public record RiskEvaluation(
        String orderId,
        RiskStatus status,
        String reason
) {
    public enum RiskStatus {
        APPROVED, REJECTED
    }
}