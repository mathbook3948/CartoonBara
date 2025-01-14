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

@Entity
@Table(name = "community_comm")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Community_Comm {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "community_comm_seq")
    @SequenceGenerator(name = "community_comm_seq", sequenceName = "community_comm_seq", allocationSize = 1)
    @Column(name = "community_comm_num")
    private Long communityCommNum;

    @ManyToOne
    @JoinColumn(name = "user_num", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "community_num", nullable = false)
    private Community community;

    @Column(name = "community_comm_content", nullable = false)
    private String communityCommContent;

    @ColumnDefault(value = "sysdate")
    @Column(name = "community_comm_date", nullable = false)
    private Date communityCommDate;

    @Column(name = "community_comm_ip", nullable = false)
    private String communityCommIp;

    @Column(name = "community_comm_singo_flag")
    private Long communityCommSingoFlag;
}
