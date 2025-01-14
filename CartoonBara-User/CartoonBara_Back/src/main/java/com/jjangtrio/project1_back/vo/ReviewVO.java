package com.jjangtrio.project1_back.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewVO {

    private Long reviewNum;
    private UserVO user;
    private PermissionVO permission; // 신고 누적 횟수 1.활동회원 2. 가입대기 3. 활동정지 5회 되면 스테이트가 4번으로 바뀐다. 4. 탈퇴회원
    private Long reviewControll;
    private Date reportDate; // 신고날짜
    private Long reportReason; // 신고 사유 1.욕설 및 비방 내용 포함 2. 광고성 게시글 3. 동일 내용 반복 업로드 4.부정확한 정보 공유
    private String reportContent; // 신고 내용

}