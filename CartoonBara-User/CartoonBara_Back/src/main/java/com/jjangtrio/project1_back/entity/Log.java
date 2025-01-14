package com.jjangtrio.project1_back.entity;

import java.time.LocalDate;
import java.util.Date;

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
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "log")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "log_seq_gen") // ID 자동 생성 설정 (필요 시 수정 가능)
    @SequenceGenerator(name = "log_seq_gen", sequenceName = "log_seq", allocationSize = 1)
    @Column(name = "log_num")
    private Long logNum;

    @ManyToOne
    @JoinColumn(name = "user_num", referencedColumnName = "user_num")
    private User user; // User 엔티티와 연결 (FK)

    @ManyToOne
    @JoinColumn(name = "log_webtoon_id", referencedColumnName = "webtoon_id", nullable = false)
    private Webtoon webtoon; // Webtoon 엔티티와 연결 (FK)

    @Column(name = "log_ip", length = 20)
    private String logIp;

    @Column(name = "log_date", columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private Date logDate; // 기본값 설정

}
