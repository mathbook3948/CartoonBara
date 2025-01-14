package com.jjangtrio.project1_back.project1_back.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jjangtrio.project1_back.project1_back.vo.SurveyContentVO;
import com.jjangtrio.project1_back.project1_back.vo.SurveyResultVO;
import com.jjangtrio.project1_back.project1_back.vo.SurveyVO;



@Mapper
public interface SurveyDAO {
    // <select id="maxSurveyNum" resultType="Long">
    Long maxSurveyNum();

    // <select id="findallList" parameterType="Long" resultType="Object[]">
    List<SurveyResultVO> findBySNUM(Long num);

    // <insert id="saveSurvey" parameterType="surveyvo">
    void saveSurvey(SurveyVO vo);

    // <insert id="saveSurveyContent" parameterType="java.util.List">
    void saveSurveyContent(SurveyContentVO vo);

}
