package com.lezhin.lezhinchallenge.domain.purchase.entity;

import com.lezhin.lezhinchallenge.common.entity.BaseEntity;
import com.lezhin.lezhinchallenge.domain.user.entity.User;
import com.lezhin.lezhinchallenge.domain.work.entity.Work;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 작품 구매 내역을 관리하는 엔티티
 */
@Entity
@Table(name = "purchases")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Purchase extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;

    @Column(nullable = false)
    private BigDecimal price;  // 구매 당시 가격

    @Column(nullable = false)
    private LocalDateTime purchasedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseType type;

    @Builder
    public Purchase(User user, Work work, BigDecimal price, PurchaseType type) {
        this.user = user;
        this.work = work;
        this.price = price;
        this.type = type;
        this.purchasedAt = LocalDateTime.now();
    }
}