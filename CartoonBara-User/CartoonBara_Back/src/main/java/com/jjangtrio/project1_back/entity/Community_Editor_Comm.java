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
import lombok.NoArgsConstructor;;

@Entity
@Table(name = "community_editor_comm")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Community_Editor_Comm {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "community_editor_comm_seq")
    @SequenceGenerator(name = "community_editor_comm_seq", sequenceName = "community_editor_comm_seq", allocationSize = 1)
    @Column(name = "community_editor_comm_num")
    private Long communityEditorCommNum;

    @ManyToOne
    @JoinColumn(name = "user_num", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "community_editor_num", nullable = false)
    private Community_Editor communityEditor;

    @Column(name = "community_editor_comm_content", nullable = false)
    private String communityEditorCommContent;

    @Column(name = "community_editor_comm_date", nullable = false)
    @ColumnDefault(value = "sysdate")
    private Date communityEditorCommDate;
}
