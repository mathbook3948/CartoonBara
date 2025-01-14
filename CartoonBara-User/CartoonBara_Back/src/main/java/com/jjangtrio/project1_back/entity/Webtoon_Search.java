package com.jjangtrio.project1_back.entity;

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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "webtoon_search")
public class Webtoon_Search {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "webtoon_search_seq")
    @SequenceGenerator(name = "webtoon_search_seq", sequenceName = "webtoon_search_seq", allocationSize = 1)
    @Column(name = "webtoon_search_id")
    private Long webtoonSearchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webtoon_id", referencedColumnName = "webtoon_id", nullable = false)
    private Webtoon webtoonId; // 외래 키로 연결된 Webtoon 엔티티

    @Column(name = "webtoon_title_choseong", nullable = false, length = 100)
    private String webtoonTitleChoseong; // 웹툰 제목 (초성)
}
