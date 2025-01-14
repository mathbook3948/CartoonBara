package com.jjangtrio.project1_back.entity;

import java.util.Date;

import org.hibernate.annotations.ColumnDefault;

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
@Table(name = "community")
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "community_seq")
    @SequenceGenerator(name = "community_seq", sequenceName = "community_seq", allocationSize = 1)
    @Column(name = "community_num")
    private Long communityNum; // 커뮤니티 번호, 기본 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_num", referencedColumnName = "user_num", nullable = false)
    private User user; // "USER" 테이블의 user_num을 참조하는 외래 키

    @Column(name = "community_category", nullable = false)
    private Long communityCategory;

    @Column(name = "community_title", nullable = false, length = 100)
    private String communityTitle; // 커뮤니티 제목, NULL을 허용하지 않음

    @Column(name = "community_date", nullable = false)
    @ColumnDefault(value = "sysdate")
    private Date communityDate; // 커뮤니티 작성일

    @Column(name = "community_hit", nullable = false)
    private Long communityHit; // 조회수

    @Column(name = "community_image", length = 255)
    private String communityImage; // 이미지 이름 (optional)

    @Column(name = "community_content", nullable = false, length = 255)
    private String communityContent; // 커뮤니티 내용

    @Column(name = "community_ip", nullable = false, length = 255)
    private String communityIp; // 커뮤니티 IP
    
    @Column(name = "community_singo_flag")
    private Long communitySingoFlag; // 신고글 유무
}
