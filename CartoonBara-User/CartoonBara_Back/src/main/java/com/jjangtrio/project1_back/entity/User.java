package com.jjangtrio.project1_back.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"user\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    @Column(name = "user_num")
    private Long userNum;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "user_pwd", nullable = false)
    private String userPwd;

    @Column(name = "user_security_question", nullable = false)
    private String userSecurityQuestion;

    @Column(name = "user_security_answer", nullable = false)
    private String userSecurityAnswer;

    @Column(name = "user_image")
    private String userImage;

    @Column(name = "user_level", columnDefinition = "NUMBER DEFAULT 1")
    private Long userLevel;

    @Column(name = "user_email", nullable = false, unique = true)
    private String userEmail;

    @Column(name = "user_phone", nullable = false, unique = true)
    private String userPhone;

    @Column(name = "user_birth", nullable = false)
    private Date userBirth;

    @Column(name = "user_nickname", nullable = false, unique = true)
    private String userNickname;

    @Column(name = "user_gender", nullable = false)
    private Long userGender;

    @Column(name = "user_date", nullable = false, columnDefinition = "DATE DEFAULT SYSDATE")
    private Date userDate;

    @Column(name = "user_introduce")
    private String userIntroduce;

    @Column(name = "user_singo_accumulated")
    private Long userSingoAccumulated;

}
