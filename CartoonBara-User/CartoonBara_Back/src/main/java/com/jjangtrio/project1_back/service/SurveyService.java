package com.jjangtrio.project1_back.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jjangtrio.project1_back.entity.Survey;
import com.jjangtrio.project1_back.repository.SurveyRepository;
import com.jjangtrio.project1_back.vo.SurveyContentVO;
import com.jjangtrio.project1_back.vo.SurveyVO;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SurveyService {

    @Autowired
    private SurveyRepository surveyRepository;

    // public Survey saveSurvey(Survey survey) {
    // return surveyRepository.save(survey);
    // }

    // // 가장 최신의 설문조사만 조회
    // public SurveyVO getLatestSurvey() {

    // long longValue = surveyRepository.maxSurveyNum().longValue();

    // List<Object[]> result = surveyRepository.findBySNUM(longValue);

    // if (result.isEmpty()) {
    // return null; // 데이터가 없는 경우 처리
    // }

    // // SurveyVO 및 SurveyContentVO 매핑
    // SurveyVO surveyVO = new SurveyVO();
    // List<SurveyContentVO> contentVOList = new ArrayList<>();

    // // 첫 번째 행을 기준으로 Survey 정보를 설정
    // Object[] firstRow = result.get(0);
    // surveyVO.setNum(((Number) firstRow[0]).longValue()); // survey.num (Number로
    // 캐스팅 후 longValue() 호출)
    // surveyVO.setSub((String) firstRow[1]); // survey.sub
    // surveyVO.setCode(((Number) firstRow[2]).longValue());// survey.code

    // // SurveyContentVO 설정
    // for (Object[] row : result) {
    // SurveyContentVO contentVO = new SurveyContentVO();
    // contentVO.setSurveytype((String) row[4]); // surveycontent.surveytype
    // contentVO.setSurveytitle((String) row[6]); // surveycontent.surveytitle
    // contentVO.setSurveycnt(((Number) row[7]).longValue()); //
    // surveycontent.surveycnt
    // contentVOList.add(contentVO);
    // }

    // surveyVO.setContents(contentVOList);
    // return surveyVO;
    // }

    // // 모든 설문 조사 조회
    // public List<SurveyVO> getSurveyList() {

    // List<SurveyVO> surveyList = new ArrayList<>();
    // long maxNum = surveyRepository.maxSurveyNum().longValue();

    // for (int i = 0; i < maxNum; i++) {
    // List<Object[]> result = surveyRepository.findBySNUM((long)i+1);

    // if (result.isEmpty()) {
    // continue; // 데이터가 없는 경우 처리
    // }else{

    // // SurveyVO 및 SurveyContentVO 매핑
    // SurveyVO surveyVO = new SurveyVO();
    // List<SurveyContentVO> contentVOList = new ArrayList<>();

    // // 첫 번째 행을 기준으로 Survey 정보를 설정
    // Object[] firstRow = result.get(0);
    // surveyVO.setNum(((Number) firstRow[0]).longValue()); // survey.num (Number로
    // 캐스팅 후 longValue() 호출)
    // surveyVO.setSub((String) firstRow[1]); // survey.sub
    // surveyVO.setCode(((Number) firstRow[2]).longValue());// survey.code

    // // SurveyContentVO 설정
    // for (Object[] row : result) {
    // SurveyContentVO contentVO = new SurveyContentVO();
    // contentVO.setSurveytype((String) row[4]); // surveycontent.surveytype
    // contentVO.setSurveytitle((String) row[6]); // surveycontent.surveytitle
    // contentVO.setSurveycnt(((Number) row[7]).longValue()); //
    // surveycontent.surveycnt
    // contentVOList.add(contentVO);
    // }

    // surveyVO.setContents(contentVOList);
    // surveyList.add(surveyVO);
    // }
    // }
    // return surveyList;
    // }

    public void updateSurveyCount(Long subcode, String surveytype) {
        surveyRepository.incrementSurveyCount(subcode, surveytype);
    }

}