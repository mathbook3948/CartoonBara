package com.jjangtrio.project1_back.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SurveyContentVO {
    @JsonProperty("surveytype")
    private String surveytype;

    @JsonProperty("surveytitle") // JSON 필드 이름 매핑
    private String surveytitle;

    @JsonProperty("surveycnt")
    private Long surveycnt;
}
