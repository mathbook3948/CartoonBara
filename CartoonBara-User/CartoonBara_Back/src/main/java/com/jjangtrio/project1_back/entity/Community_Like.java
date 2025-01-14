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
@Table(name = "community_like")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Community_Like {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "community_like_seq")
    @SequenceGenerator(name = "community_like_seq", sequenceName = "community_like_seq", allocationSize = 1)
    @Column(name = "community_like_num")
    private Long communityLikeNum;

    @ManyToOne
    @JoinColumn(name = "user_num", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "community_num", nullable = false)
    private Community community;

    @Column(name = "community_like_islike", nullable = false)
    private Long communityLikeIslike;
}
