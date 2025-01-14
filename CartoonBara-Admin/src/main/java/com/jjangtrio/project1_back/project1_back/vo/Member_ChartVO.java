package com.jjangtrio.project1_back.project1_back.vo;

import org.apache.ibatis.type.Alias;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Alias("memberChartVO") 
@Getter
@Setter
@ToString
public class Member_ChartVO {
    private int totalUsers;      // 전체 사용자 수
    private int todaysSignups;   // 오늘 가입한 사용자 수
    private String ageGroup;     // 연령대 그룹 (예: 0-12, 13-18 등)
    private String userGender;   // 성별 (예: 남, 여)
    private double percentage;   // 해당 그룹의 비율
}
