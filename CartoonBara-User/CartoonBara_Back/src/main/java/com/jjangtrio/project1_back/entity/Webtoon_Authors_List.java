package com.jjangtrio.project1_back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "webtoon_authors_list")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Webtoon_Authors_List {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "webtoon_authors_list_seq")
    @SequenceGenerator(name = "webtoon_authors_list_seq", sequenceName = "webtoon_authors_list_seq", allocationSize = 1)
    @Column(name = "webtoon_authors_list_num")
    private Long webtoonAuthorsListNum;

    @Column(name = "webtoon_author", nullable = false)
    private String webtoonAuthor;

}
