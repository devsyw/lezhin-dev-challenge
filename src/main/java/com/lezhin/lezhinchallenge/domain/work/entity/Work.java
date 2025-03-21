package com.lezhin.lezhinchallenge.domain.work.entity;


import com.lezhin.lezhinchallenge.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 만화 작품 정보를 관리하는 엔티티
 */
@Entity
@Table(name = "works")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Work extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkType type;

    @Column(nullable = false)
    private String thumbnailUrl;

    @Column(nullable = false)
    private int viewCount;  // 조회수

    @Column(nullable = false)
    private int purchaseCount;  // 구매수

    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Episode> episodes = new ArrayList<>();

    @Builder
    public Work(String title, String author, String description, BigDecimal price,
                WorkType type, String thumbnailUrl) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.price = price;
        this.type = type;
        this.thumbnailUrl = thumbnailUrl != null ? thumbnailUrl : "";
        this.viewCount = 0;
        this.purchaseCount = 0;
    }

    /**
     * 조회수 증가
     */
    public void increaseViewCount() {
        this.viewCount++;
    }

    /**
     * 구매수 증가
     */
    public void increasePurchaseCount() {
        this.purchaseCount++;
    }

    /**
     * 작품 정보 업데이트
     */
    public void update(String title, String author, String description,
                       BigDecimal price, WorkType type, String thumbnailUrl) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.price = price;
        this.type = type;
        this.thumbnailUrl = thumbnailUrl != null ? thumbnailUrl : this.thumbnailUrl;
    }

    /**
     * 에피소드 추가
     */
    public void addEpisode(Episode episode) {
        this.episodes.add(episode);
    }
}