package com.jjangtrio.project1_back.project1_back.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jjangtrio.project1_back.project1_back.dao.SurveyDAO;
import com.jjangtrio.project1_back.project1_back.vo.SurveyContentVO;
import com.jjangtrio.project1_back.project1_back.vo.SurveyResultVO;
import com.jjangtrio.project1_back.project1_back.vo.SurveyVO;



@Service
public class SurveyService {
    
    @Autowired
    private SurveyDAO surveyDAO;

    public Long maxSurveyNum() {
        return surveyDAO.maxSurveyNum();
    }

    public void saveSurvey(SurveyVO vo) {
        try {
            char stype = 'A';
            surveyDAO.saveSurvey(vo);
            for (int i = 0; i < vo.getContents().size(); i++) {
                // System.out.println(vo.getContents().get(i).getSurveytitle());
                SurveyContentVO contentVO = new SurveyContentVO();
                contentVO.setSurveytitle(vo.getContents().get(i).getSurveytitle());
                contentVO.setSurveytype(String.valueOf(stype));
                contentVO.setSurveycnt(0L);
                stype++; // A, B, C... 순서로 증가
                surveyDAO.saveSurveyContent(contentVO);
            }
            System.out.println(vo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // public void saveSurveyContent(List<SurveyContentVO> list) {
    //     surveyDAO.saveSurveyContent(list);
    // }
    
    public List<SurveyVO> getSurveyList() {
        List<SurveyVO> surveyList = new ArrayList<>();
        long maxNum = surveyDAO.maxSurveyNum().longValue();

        for (int i = 0; i < maxNum; i++) {
            List<SurveyResultVO> result = surveyDAO.findBySNUM((long)i+1);

            if (result.isEmpty()) {
                continue; // 데이터가 없는 경우 처리
            }else{

                // SurveyVO 및 SurveyContentVO 매핑
                SurveyVO surveyVO = new SurveyVO();
                List<SurveyContentVO> contentVOList = new ArrayList<>();

                // 첫 번째 행을 기준으로 Survey 정보를 설정
                SurveyResultVO resultVO = result.get(0);
                surveyVO.setNum(resultVO.getSurveyNum().longValue());
                surveyVO.setSub(resultVO.getSurveySub());
                surveyVO.setCode(resultVO.getSurveyCode().longValue());
                surveyVO.setSdate(resultVO.getSurveyDate()); // survey.code

                // SurveyContentVO 설정
                for (SurveyResultVO vo : result) {
                    SurveyContentVO contentVO = new SurveyContentVO();
                    contentVO.setSurveytype(vo.getSurveytype());
                    contentVO.setSurveytitle(vo.getSurveytitle());
                    contentVO.setSurveycnt(vo.getSurveycnt().longValue());
                    contentVOList.add(contentVO);
                }

                surveyVO.setContents(contentVOList);
                surveyList.add(surveyVO);
            }
        }
        return surveyList;
    }

    public SurveyVO findBySNUM(Long num) {

        List<SurveyResultVO> result = surveyDAO.findBySNUM(num);

        if (result == null) {
            return null; // 데이터가 없는 경우 처리
        }
        SurveyVO surveyVO = new SurveyVO();
        List<SurveyContentVO> contentVOList = new ArrayList<>();

        SurveyResultVO resultVO = result.get(0);
        surveyVO.setNum(resultVO.getSurveyNum());
        surveyVO.setSub(resultVO.getSurveySub());
        surveyVO.setCode(resultVO.getSurveyCode());
        surveyVO.setSdate(resultVO.getSurveyDate());
        
        for (SurveyResultVO vo : result) {
            SurveyContentVO contentVO = new SurveyContentVO();
            contentVO.setSurveytype(vo.getSurveytype());
            contentVO.setSurveytitle(vo.getSurveytitle());
            contentVO.setSurveycnt(vo.getSurveycnt());
            contentVOList.add(contentVO);
        }
        surveyVO.setContents(contentVOList);
        return surveyVO;
    }

}
