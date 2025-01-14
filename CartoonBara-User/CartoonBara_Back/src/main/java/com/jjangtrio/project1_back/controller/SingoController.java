package com.jjangtrio.project1_back.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjangtrio.project1_back.service.SingoService;

@RestController 
@RequestMapping("/api/singo")
public class SingoController {
    
    @Autowired
    private SingoService singoService;

    /*
     * front에서 데이터가 넘어오는 형식
     * {
            singoCategory: number;                // 1.웹툰 리뷰, 2.웹툰 리뷰 댓글, 3.커뮤니티, 4.커뮤니티 댓글
            userNum: number;                      // 피신고자의 userNum
            primarynumber: number;                // 테이블이 갖는 primaryKey
            singoCurrentUrl: string;              // 신고당한 페이지의 url
     * }
     */

    @PostMapping
    public ResponseEntity<?> insertSingo(@RequestBody Map<String, Object> map) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long user_num = Long.valueOf(auth.getPrincipal().toString());

        map.put("userNum", user_num);
        
        if(singoService.existSingoByLog(map)){
            if(singoService.updateSingoFlag(map)){ // 글과 User 신고처리
                singoService.insertSingo(map);  // Singo Table에 신고 내역 저장
                return ResponseEntity.status(HttpStatus.CREATED).body("신고가 완료되었습니다.");
            }    
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("신고 처리 중 오류가 발생하였습니다."); 
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 신고 처리 된 글 입니다."); 
        }
    }

    // @GetMapping
    // public ResponseEntity<?> selectSingo() {
    //     return ResponseEntity.ok().body(singoService.selectSingo());
    // }
    
}
