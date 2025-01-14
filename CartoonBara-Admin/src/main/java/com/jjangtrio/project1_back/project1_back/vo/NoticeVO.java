package com.jjangtrio.project1_back.project1_back.vo;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;

@Alias("noticevo")
@Getter
@Setter
public class NoticeVO {

    private Long noticeNum; // 공지사항 번호
    private Long permissionNum; // 권한 번호
    private String noticeTitle; // 제목
    private String noticeContent; // 내용
    private String noticeImage; // 이미지 경로 (파일 이름만 저장)
    private String noticeDate; // 작성 날짜

    
}
