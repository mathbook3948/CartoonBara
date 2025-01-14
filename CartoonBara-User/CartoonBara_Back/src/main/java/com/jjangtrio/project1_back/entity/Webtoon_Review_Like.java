package com.jjangtrio.project1_back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "webtoon_review_like")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Webtoon_Review_Like {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "webtoon_review_like_seq")
    @SequenceGenerator(name = "webtoon_review_like_seq", sequenceName = "webtoon_review_like_seq", allocationSize = 1)
    @Column(name = "webtoon_review_like_num")
    private Long webtoonReviewLikeNum;

    @ManyToOne
    @JoinColumn(name = "user_num", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "webtoon_review_num", nullable = false)
    private Webtoon_Review webtoonReview;

    @Column(name = "webtoon_review_like_islike", nullable = false)
    private Long webtoonReviewLikeIslike;

}
