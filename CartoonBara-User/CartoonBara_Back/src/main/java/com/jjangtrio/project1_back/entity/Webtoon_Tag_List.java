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
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "webtoon_tag_list")
public class Webtoon_Tag_List {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "webtoon_tag_list_seq")
    @SequenceGenerator(name = "webtoon_tag_list_seq", sequenceName = "webtoon_tag_list_seq", allocationSize = 1)
    @Column(name = "webtoon_tag_list_num")
    private Long webtoonTagListNum; // 변수명 camelCase로 변경

    @Column(name = "webtoon_tag_list_tagn", nullable = false, length = 50)
    private String webtoonTagListTagn; // 변수명 camelCase로 변경
}
