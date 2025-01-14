package com.jjangtrio.project1_back.project1_back.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjangtrio.project1_back.project1_back.service.SurveyService;
import com.jjangtrio.project1_back.project1_back.vo.SurveyVO;


@RestController
@RequestMapping("/api/survey")
public class SurveyController {
    
    @Autowired
    private SurveyService surveyService;

    @PostMapping("/addsurvey") 
    public ResponseEntity<String> saveSurvey(@RequestBody SurveyVO vo) {
        // {
        //     "sub" : "배가 고프세요?",
        //     "code" : 2,
        //     "contents" : [
        //         {
        //             "surveyTitle" : "네"
        //         },
        //         {
        //             "surveyTitle" : "아니요"
        //         }
        //     ] 
        //  }  -> 이렇게 들어옴
        surveyService.saveSurvey(vo);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/latest") 
    public ResponseEntity<SurveyVO> getLatestSurvey() {
        SurveyVO surveyVO = surveyService.findBySNUM(surveyService.maxSurveyNum());
        if (surveyVO != null) {
            return ResponseEntity.ok(surveyVO);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/allList") 
    public ResponseEntity<List<SurveyVO>> getAllSurvey() {
        List<SurveyVO> surveyList = surveyService.getSurveyList();
        if (surveyList != null) {
            return ResponseEntity.ok(surveyList);
        } else {
            return ResponseEntity.noContent().build();
        }
    }


}
