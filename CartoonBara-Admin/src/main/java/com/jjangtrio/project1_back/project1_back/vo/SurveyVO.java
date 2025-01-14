package com.jjangtrio.project1_back.project1_back.vo;


import java.util.List;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Alias("surveyvo")
public class SurveyVO {
    private Long num;
    private PermissionVO permissionNum;
    private String sub;
    private Long code;
    private String sdate;
    private List<SurveyContentVO> contents;
}
