package com.jjangtrio.project1_back.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SurveyVO {
    private Long num;
    private String sub;
    private Long code;
    private List<SurveyContentVO> contents;
}
