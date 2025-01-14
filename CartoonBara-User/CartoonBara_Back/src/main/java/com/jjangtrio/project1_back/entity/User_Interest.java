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
@Table(name = "user_interest")
public class User_Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_interest_seq")
    @SequenceGenerator(name = "user_interest_seq", sequenceName = "user_interest_seq", allocationSize = 1)
    @Column(name = "user_interest_num")
    private Long userInterestNum; // 관심사 번호, 기본 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_interest_user_num", referencedColumnName = "user_num", nullable = false)
    private User user; // "USER" 테이블의 user_num을 참조하는 외래 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_webtoon_tag_list_num", referencedColumnName = "webtoon_tag_list_num", nullable = false)
    private Webtoon_Tag_List userWebtoonTagList; // "webtoon_tag_list" 테이블의 webtoon_tag_list_num을 참조하는 외래 키

}
