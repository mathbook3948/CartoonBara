package com.jjangtrio.project1_back.project1_back.vo;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Alias("surveyContentvo")
public class SurveyContentVO {
    private String surveytype;
    private String surveytitle;
    private Long surveycnt;
}
