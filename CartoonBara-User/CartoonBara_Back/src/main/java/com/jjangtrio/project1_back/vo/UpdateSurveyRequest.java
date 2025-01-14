package com.jjangtrio.project1_back.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateSurveyRequest {
    private Long subcode;
    private String surveytype;
}