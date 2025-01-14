package com.jjangtrio.project1_back.project1_back.vo;

import org.apache.ibatis.type.Alias;
import lombok.Getter;
import lombok.Setter;

@Alias("reportManagementList") // MyBatis에서 사용할 별칭
@Getter
@Setter
public class Report_Management_List {
    private int num;
    private int userNum;        // 신고한 사용자 번호
    private String title;       // 신고 대상 제목
    private String singoDate;   // 신고 날짜 ('YYYY.MM.DD' 형식)
    private int singoCount;     // 신고 횟수
    private String siteUrl;     // 관련 URL
}
