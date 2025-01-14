package com.jjangtrio.project1_back.entity;

import java.util.Date;
import java.util.List;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
//Survey가 메인 엔티티가 되고, SurveyContent는 @Embeddable 클래스로 구현
//@ElementCollection을 사용하여 Survey와 SurveyContent 간의 일대다 관계를 설정
//@ElementCollection은 별도의 식별자를 가질 수 없고, 항상 부모 엔티티에 종속적이라는 것이다. 
//만약 SurveyContent에 독립적인 식별자가 필요하다면, 대신 @Entity와 @OneToMany 관계를 사용하는 것이 더 적절할 수 있
@Setter
@Getter
@Entity
@Table(name = "survey")
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "survey_seq")
    @SequenceGenerator(name = "survey_seq", sequenceName = "survey_seq", allocationSize = 1)
    private Long num;
    
    private String sub;
    private Long code;
    private Date sdate;

    @ElementCollection
    @CollectionTable(
        name = "surveycontent",
        joinColumns = @JoinColumn(name = "subcode")
    )
    private List<SurveyContent> contents;



}