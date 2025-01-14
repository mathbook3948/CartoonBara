package com.jjangtrio.project1_back.entity;

import java.util.Date;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "webtoon_review")
@Builder
public class Webtoon_Review {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "webtoon_review_seq")
    @SequenceGenerator(name = "webtoon_review_seq", sequenceName = "webtoon_review_seq", allocationSize = 1)
    @Column(name = "webtoon_review_num")
    private Long webtoonReviewNum; // 변수명 camelCase로 변경

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webtoon_id", referencedColumnName = "webtoon_id")
    private Webtoon webtoon; // 변수명 camelCase로 변경

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_num", referencedColumnName = "user_num")
    private User user; // 변수명 camelCase로 변경

    @Column(name = "user_nickname")
    private String userNickname;

    @Column(name = "webtoon_review_review", nullable = false, length = 255)
    private String webtoonReviewReview; // 변수명 camelCase로 변경

    @ColumnDefault(value = "sysdate")
    @Column(name = "webtoon_review_date", nullable = false)
    private Date webtoonReviewDate; // 변수명 camelCase로 변경

    @Column(name = "webtoon_review_ip", nullable = false, length = 255)
    private String webtoonReviewIp; // 변수명 camelCase로 변경

    @Column(name = "webtoon_review_singo_flag")
    private Long webtoonReviewSingoFlag;
}
