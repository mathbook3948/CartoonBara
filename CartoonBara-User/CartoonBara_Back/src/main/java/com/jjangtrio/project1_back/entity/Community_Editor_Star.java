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
@Table(name = "community_editor_star")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Community_Editor_Star {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "community_editor_star_seq")
    @SequenceGenerator(name = "community_editor_star_seq", sequenceName = "community_editor_star_seq", allocationSize = 1)
    @Column(name = "community_editor_star_num")
    private Long communityEditorStarNum;

    @ManyToOne
    @JoinColumn(name = "community_editor_num", nullable = false) // 외래 키로 매핑
    private Community_Editor communityEditor;

    @Column(name = "community_editor_star", nullable = false)
    private Long communityEditorStar;

    @ManyToOne
    @JoinColumn(name = "user_num") // 외래 키로 매핑
    private User user;
}
