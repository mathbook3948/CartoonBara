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
@Table(name = "webtoon_like")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Webtoon_Like {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "webtoon_like_seq")
    @SequenceGenerator(name = "webtoon_like_seq", sequenceName = "webtoon_like_seq", allocationSize = 1)
    @Column(name = "webtoon_like_num")
    private Long webtoonLikeNum;

    @ManyToOne
    @JoinColumn(name = "user_num", nullable = false)
    private User user;

    @Column(name = "webtoon_islike", nullable = false)
    private Long webtoonIsLike;

    @ManyToOne
    @JoinColumn(name = "webtoon_id", nullable = false)
    private Webtoon webtoon;

}
