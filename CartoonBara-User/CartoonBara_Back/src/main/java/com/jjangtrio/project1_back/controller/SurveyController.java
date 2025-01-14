package com.jjangtrio.project1_back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jjangtrio.project1_back.service.SurveyService;
import com.jjangtrio.project1_back.vo.UpdateSurveyRequest;

@RestController
@RequestMapping("/api/survey")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    // 사용자가 설문 항목에 투표하면, 항목이 카운터되는
    @PostMapping("/updateCount")
    public ResponseEntity<?> updateSurveyCount(@RequestBody UpdateSurveyRequest request) {
        try {
            surveyService.updateSurveyCount(request.getSubcode(), request.getSurveytype());
            return ResponseEntity.ok("Survey count 수정 성공");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("update survey count 실패!");
        }
    }

}
