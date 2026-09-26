package com.example.tradingengine.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "order_audit_trail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String symbol;

    private int quantity;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(length = 500)
    private String reason;

    private Instant timestamp;

    public enum Status {
        APPROVED, REJECTED
    }
}