package com.jjangtrio.project1_back.project1_back.vo;


import org.apache.ibatis.type.Alias;
import lombok.Getter;
import lombok.Setter;

@Alias("reportMembershipManagementVO")
@Getter
@Setter
public class Report_Membership_ManagementVO {
    private Long userNum;             // 회원 번호 (USER_NUM)
    private String nickname;          // 신고받은 사람 별명 (USER_NICKNAME)
    private String name;              // 신고받은 사람 이름 (USER_NAME)
    private Long singoAccumulated;    // 신고 누적 횟수 (USER_SINGO_ACCUMULATED)private Date reportDate; // 신고 날짜 (최소한 LocalDateTime으로 관리)
    private Long communityPostCount;  // 신고당한 커뮤니티 글 수 (SINGO_CATEGORY = 3)
    private Long communityCommentCount;// 신고당한 커뮤니티 댓글 수 (SINGO_CATEGORY = 4)
    private Long reviewPostCount;     // 신고당한 웹툰 리뷰 글 수 (SINGO_CATEGORY = 1)
    private Long reviewCommentCount;  // 신고당한 웹툰 리뷰 댓글 수 (SINGO_CATEGORY = 2)
    private String approvalStatus;    // 신고 승인 여부 (PERMISSION_STATE에 3,1 따라 '신고 계정' 또는 '일반 사용자'로 분류)
}

