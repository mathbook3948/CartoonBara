package com.jjangtrio.project1_back.project1_back.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjangtrio.project1_back.project1_back.service.QnaService;
import com.jjangtrio.project1_back.project1_back.vo.QnaVO;

@RestController
@RequestMapping("/api/qna")
public class QnaController {
    
    @Autowired
    private QnaService qnaService;

    @PostMapping("/test")
    public void test() {
        System.out.println("연결 성공");
    }


    // @GetMapping("/{qnaNum}")
    // public void qnaDetail(@PathVariable("qnaNum") Long qnaNum) {
    //   QnaVO vo = qnaService.qnaDetail(qnaNum);
    //   if (vo != null) {
    //     System.out.println(vo);
    // } else {
    //     System.out.println("vo => null");
    // } 
    // }

    @PostMapping("/answer")
    public ResponseEntity<?> qnaAnswer(@RequestParam("qnaNum") Long qnaNum, @RequestParam("qnaAnswer") String qnaAnswer) {
        QnaVO vo = new QnaVO();
        vo = qnaService.qnaDetail(qnaNum);
        if (vo != null) {
            vo.setQnaAnswer(qnaAnswer);
        } else {
            System.out.println("vo => null");
        } 
        qnaService.qnaAnswer(vo);
        return ResponseEntity.ok().body("Success");
    }
}
