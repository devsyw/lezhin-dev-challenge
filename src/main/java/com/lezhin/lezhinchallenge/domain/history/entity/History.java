package com.lezhin.lezhinchallenge.domain.history.entity;

import com.lezhin.lezhinchallenge.common.entity.BaseEntity;
import com.lezhin.lezhinchallenge.domain.user.entity.User;
import com.lezhin.lezhinchallenge.domain.work.entity.Work;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 작품 조회 이력을 관리하는 엔티티
 */
@Entity
@Table(name = "histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class History extends BaseEntity {

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
    private LocalDateTime viewedAt;

    @Builder
    public History(User user, Work work) {
        this.user = user;
        this.work = work;
        this.viewedAt = LocalDateTime.now();
    }
}
