package com.jjangtrio.project1_back.project1_back.vo;

import java.util.Date;

import org.apache.ibatis.type.Alias;


import lombok.Getter;
import lombok.Setter;

@Alias("registeredmembervo")
@Getter
@Setter
public class Registered_MemberVO {
    private UserVO user;
    private PermissionVO permission;  // 신고 누적 횟수 1.활동회원 2. 가입대기 3. 활동정지 5회 되면 스테이트가 4번으로 바뀐다. 4. 탈퇴회원
    private CommunityVO community;
    private Webtoon_ReviewVO webtoonReview;
    private Date reportDate; // 신고날짜 
    private Long reportState; // 신고상태 1. 처리중 2. 처리 완료
    private Long reportReason; // 신고 사유 1.욕설 및 비방 내용 포함 2. 광고성 게시글 3. 동일 내용 반복 업로드 4.부정확한 정보 공유

}
