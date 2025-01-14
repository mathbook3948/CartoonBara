package com.jjangtrio.project1_back.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjangtrio.project1_back.entity.Webtoon_Review_Comm;
import com.jjangtrio.project1_back.repository.Webtoon_Review_CommRepository;
import com.jjangtrio.project1_back.service.Webtoon_Review_CommService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/webtoonReviewComm")
public class WebtoonReviewCommController {

    @Autowired
    private Webtoon_Review_CommService webtoonReview_CommService;

    @Autowired
    private Webtoon_Review_CommRepository webtoonReview_CommRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadWebtoon_Review_Comm(@RequestBody Map<String, Object> map,
            HttpServletRequest request) {

        try {

            String webtoonReviewCommIp = request.getRemoteAddr();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userNum = Long.parseLong(auth.getPrincipal().toString());

            Long webtoonReviewNum = Long.parseLong(map.get("webtoonReviewNum").toString());
            String webtoonReviewCommContet = map.get("webtoonReviewCommContet").toString();
            String webtoonReviewNickname = map.get("webtoonReviewNickname").toString();

            webtoonReview_CommService.uploadWebtoonReviewComm(webtoonReviewNum, userNum, webtoonReviewNickname,
                    webtoonReviewCommContet, webtoonReviewCommIp);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // 업데이트
    @PostMapping("/update")
    public ResponseEntity<?> updateWebtoonReviewComm(@RequestBody Map<String, Object> map) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userNum = Long.parseLong(auth.getPrincipal().toString());

            Long webtoonReviewCommNum = Long.parseLong(map.get("webtoonReviewCommNum").toString());
            String webtoonReviewCommContent = map.get("webtoonReviewCommContent").toString();

            // 댓글 조회
            Webtoon_Review_Comm wrc = webtoonReview_CommRepository.findById(webtoonReviewCommNum)
                    .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

            // 댓글 작성자와 현재 로그인한 사용자 비교
            if (!wrc.getUser().getUserNum().equals(userNum)) {
                return ResponseEntity.status(403).body("자신이 작성한 댓글만 수정할 수 있습니다.");
            }

            Webtoon_Review_Comm updatedWrc = webtoonReview_CommService.updateWebtoonReviewComm(webtoonReviewCommNum,
                    webtoonReviewCommContent);
            return ResponseEntity.ok(updatedWrc);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // 삭제
    @PostMapping("/delete")
    public ResponseEntity<?> deleteWebtoonReviewComm(@RequestBody Map<String, Object> map) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userNum = Long.parseLong(auth.getPrincipal().toString());

            Long webtoonReviewCommNum = Long.parseLong(map.get("webtoonReviewCommNum").toString());

            // 댓글 조회
            Webtoon_Review_Comm wrc = webtoonReview_CommRepository.findById(webtoonReviewCommNum)
                    .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

            // 댓글 작성자와 현재 로그인한 사용자 비교
            if (!wrc.getUser().getUserNum().equals(userNum)) {
                return ResponseEntity.status(403).body("자신이 작성한 댓글만 삭제할 수 있습니다.");
            }

            webtoonReview_CommService.deleteWebtoonReviewComm(webtoonReviewCommNum);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

}
