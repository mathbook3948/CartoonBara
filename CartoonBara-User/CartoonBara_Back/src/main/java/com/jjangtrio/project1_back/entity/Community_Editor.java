package com.jjangtrio.project1_back.entity;

import java.util.Date;

import org.hibernate.annotations.ColumnDefault;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "community_editor")
public class Community_Editor {

    @Id
    @Column(name = "community_editor_num")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "community_editor_seq")
    @SequenceGenerator(name = "community_editor_seq", sequenceName = "community_editor_seq", allocationSize = 1)
    private Long communityEditorNum;

    @ManyToOne
    @JoinColumn(name = "user_num", nullable = false)
    private User user;

    @Column(name = "community_editor_category", nullable = false)
    private Long communityEditorCategory;

    @Column(name = "community_editor_title", nullable = false, length = 100)
    private String communityEditorTitle;

    @Column(name = "community_editor_date", nullable = false)
    @ColumnDefault(value = "sysdate")
    private Date communityEditorDate;

    @Column(name = "community_editor_hit", nullable = false)
    private Long communityEditorHit;

    @Column(name = "community_editor_content", nullable = false, length = 255)
    private String communityEditorContent;

    @Column(name = "community_editor_ip", nullable = false, length = 255)
    private String communityEditorIp;

    @Column(name = "community_editor_image", length = 255)
    private String communityEditorImage;
}
