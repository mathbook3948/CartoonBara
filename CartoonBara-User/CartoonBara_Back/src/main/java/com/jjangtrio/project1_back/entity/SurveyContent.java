package com.jjangtrio.project1_back.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

//@Entity
//@Entity와 @Embeddable 어노테이션을 동시에 사용할 수 없다.
@Setter
@Getter
@Embeddable
public class SurveyContent {
    @Column(name = "surveytype")
    private String surveytype;
    
    @Column(name = "surveytitle")
    private String surveytitle;
    
    @Column(name = "surveycnt")
    private Long surveycnt;
}