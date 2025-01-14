package com.jjangtrio.project1_back.project1_back.vo;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;

@Alias("reportmemberVO")
@Getter
@Setter
public class Report_MemberVO {
    private Long userNum;           // 유저 번호 (primary key 역할)
    private String userId;          // 유저 아이디
    private String nickname;        // 유저 닉네임
    private int permissionState;    // 퍼미션 상태 (1: 사용자, 2: 가입대기, 3: 신고, 4: 활동정지, 5: 탈퇴)
}