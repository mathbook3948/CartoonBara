package com.jjangtrio.project1_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "webtoon")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Webtoon {

    @Id
    @Column(name = "webtoon_id")
    private Long webtoonId;

    @Column(name = "webtoon_title", nullable = false)
    private String webtoonTitle;

    @Column(name = "webtoon_desc", length=1500)
    private String webtoonDesc;

    @Column(name = "webtoon_isend", nullable = false)
    private Long webtoonIsEnd;

    @Column(name = "webtoon_url", nullable = false, unique = true)
    private String webtoonUrl;

}
