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
import jakarta.persistence.criteria.Fetch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "webtoon_tag")
public class Webtoon_Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "webtoon_tag_seq")
    @SequenceGenerator(name = "webtoon_tag_seq", sequenceName = "webtoon_tag_seq", allocationSize = 1)
    @Column(name = "webtoon_tag_num")
    private Long webtoonTagNum; // 태그 번호, 기본 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webtoon_id", referencedColumnName = "webtoon_id", nullable = false)
    private Webtoon webtoon; // "webtoon" 테이블의 webtoon_id를 참조하는 외래 키

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_num", referencedColumnName= "user_num", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webtoon_tag_list_num", referencedColumnName = "webtoon_tag_list_num", nullable = false)
    private Webtoon_Tag_List webtoonTagListNum; // "webtoon_tag_list" 테이블의 webtoon_tag_list_num을 참조하는 외래 키

}
