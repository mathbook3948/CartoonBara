package com.jjangtrio.project1_back.project1_back.vo;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Alias("surveyresultvo")
public class SurveyResultVO {

    // list 출력용
    private Long surveyNum;
    private String surveySub;
    private Long surveyCode;
    private String surveyDate;
    private Long subCode;
    private String surveytype;
    private String surveytitle;
    private Long surveycnt;
}

