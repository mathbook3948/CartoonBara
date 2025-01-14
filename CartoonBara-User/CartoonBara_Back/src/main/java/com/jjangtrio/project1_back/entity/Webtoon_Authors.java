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

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "webtoon_authors")
public class Webtoon_Authors {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "webtoon_authors_seq")
    @SequenceGenerator(name = "webtoon_authors_seq", sequenceName = "webtoon_authors_seq", allocationSize = 1)
    @Column(name = "webtoon_authors_num")
    private Long webtoonAuthorsNum;

    @ManyToOne
    @JoinColumn(name = "webtoon_id", nullable = false)
    private Webtoon webtoon;

    @ManyToOne
    @JoinColumn(name = "webtoon_authors_list_num", nullable = false)
    private Webtoon_Authors_List webtoonAuthorsListNum;

}
