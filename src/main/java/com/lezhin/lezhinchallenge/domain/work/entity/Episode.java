package com.lezhin.lezhinchallenge.domain.work.entity;

import com.lezhin.lezhinchallenge.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 작품의 에피소드 정보를 관리하는 엔티티
 */
@Entity
@Table(name = "episodes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Episode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer episodeNumber;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Boolean free;

    @Column(nullable = false)
    private Integer viewCount;

    @Builder
    public Episode(Work work, String title, Integer episodeNumber, String content, BigDecimal price, Boolean free) {
        this.work = work;
        this.title = title;
        this.episodeNumber = episodeNumber;
        this.content = content;
        this.price = price;
        this.free = free;
        this.viewCount = 0;
    }

    /**
     * 조회수 증가
     */
    public void increaseViewCount() {
        this.viewCount++;
    }

    /**
     * 에피소드 정보 업데이트
     */
    public void update(String title, String content, BigDecimal price, Boolean free) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.free = free;
    }
}
