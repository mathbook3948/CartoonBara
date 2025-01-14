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
@Table(name = "singo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Singo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "singo_seq")
    @SequenceGenerator(name = "singo_seq", sequenceName = "singo_seq", allocationSize = 1)
    @Column(name = "singo_num")
    private Long singoNum;

    @ManyToOne
    @JoinColumn(name = "user_num")
    private User user;

    @ColumnDefault(value = "sysdate")
    @Column(name = "singo_date", nullable = false)
    private Date singoDate;

    @Column(name = "singo_category")
    private Long singoCategory;

    @Column(name = "primarynumber")
    private Long primarynumber;

    @Column(name = "singo_current_url")
    private String singoCurrentUrl;
}
