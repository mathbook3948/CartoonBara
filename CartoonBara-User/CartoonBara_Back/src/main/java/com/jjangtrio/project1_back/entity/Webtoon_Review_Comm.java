package com.jjangtrio.project1_back.entity;

import java.util.Date;

import org.hibernate.annotations.ColumnDefault;

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
@Table(name = "webtoon_review_comm")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Webtoon_Review_Comm {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "webtoon_review_comm_seq")
    @SequenceGenerator(name = "webtoon_review_comm_seq", sequenceName = "webtoon_review_comm_seq", allocationSize = 1)
    @Column(name = "webtoon_review_comm_num")
    private Long webtoonReviewCommNum;

    @ManyToOne
    @JoinColumn(name = "user_num", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "webtoon_review_num", nullable = false)
    private Webtoon_Review webtoonReview;

    @Column(name = "webtoon_review_nickname", nullable = false)
    private String webtoonReviewNickname;

    @Column(name = "webtoon_review_comm_content", nullable = false)
    private String webtoonReviewCommContent;

    @ColumnDefault(value = "sysdate")
    @Column(name = "webtoon_review_comm_date", nullable = false)
    private Date webtoonReviewCommDate;

    @Column(name = "webtoon_review_comm_ip", nullable = false)
    private String webtoonReviewCommIp;

    @Column(name = "webtoon_review_comm_singo_flag")
    private Long webtoonReviewCommSingoFlag;
}
