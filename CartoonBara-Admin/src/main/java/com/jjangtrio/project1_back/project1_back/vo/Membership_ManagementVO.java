package com.jjangtrio.project1_back.project1_back.vo;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;

@Alias("membershipmanagementvo")
@Getter
@Setter
public class Membership_ManagementVO {
    private Long userNum;             // 회원 번호 (USER_NUM)
    private String nickname;          // 회원 별명 (USER_NICKNAME)
    private String name;              // 회원 이름 (USER_NAME)
    private String joinDate;          // 가입 날짜 (USER_DATE -> 'YYYY.MM.DD' 형식)
    private int communityPostCount;   // 작성한 게시글 수 (COMMUNITY_NUM 카운트)
    private int communityCommentCount; // 작성한 게시글 댓글 수 (COMMUNITY_COMM_NUM 카운트)
    private int reviewPostCount;      // 작성한 리뷰 수 (WEBTOON_REVIEW_NUM 카운트)
    private int reviewCommentCount;   // 작성한 리뷰 댓글 수 (WEBTOON_REVIEW_COMMENT_NUM 카운트)
    private String gender;            // 성별 ('남', '여')
    private String approvalStatus;   // 가입 승인 여부 (PERMISSION_STATE에 따라 '가입 완료' 또는 '가입 대기')
}  
