package com.jjangtrio.project1_back.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjangtrio.project1_back.service.Community_EditorCommService;
import com.jjangtrio.project1_back.vo.Community_Editor_CommVO;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/communityEditorComm")
@CrossOrigin("*")
public class Community_EditorCommController {
    @Autowired
    private Community_EditorCommService communityEditorCommService;

    @PostMapping("/add")
    public ResponseEntity<?> addComment(
            // @RequestParam("communityEditorNum") Long communityEditor,
            // @RequestParam("communityEditorCommContent") String content,
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request) 
            {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userNum = Long.valueOf(auth.getPrincipal().toString());
        
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        
        try {

        Long communityEditorNum = Long.valueOf(requestBody.get("communityEditorNum").toString());
        String content = requestBody.get("communityEditorCommContent").toString();
            // 클라이언트 IP 가져오기
            // String clientIp = request.getRemoteAddr();

            // VO 생성
            Community_Editor_CommVO vo = Community_Editor_CommVO.builder()
                    .communityEditor(communityEditorNum)
                    .userNum(userNum)
                    .communityEditorCommContent(content)
                    .build();

            // 댓글 추가
            communityEditorCommService.addComment(vo);
            return ResponseEntity.ok("댓글이 추가되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("댓글 추가 실패: " + e.getMessage());
        }
    }

    // 댓글 삭제
    @DeleteMapping("/delete/{communityEditorCommNum}")
    public ResponseEntity<String> deleteComment(@PathVariable Long communityEditorCommNum) {
        try {
            communityEditorCommService.deleteComment(communityEditorCommNum);
            return new ResponseEntity<>("댓글이 삭제되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("댓글 삭제 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 댓글 수정
    @PutMapping("/update/{communityEditorCommNum}")
    public ResponseEntity<String> updateComment(
            @PathVariable Long communityEditorCommNum,
            @RequestBody Community_Editor_CommVO vo) {
        if (vo.getCommunityEditorCommContent() == null || vo.getCommunityEditorCommContent().trim().isEmpty()) {
            return new ResponseEntity<>("수정할 댓글 내용이 없습니다.", HttpStatus.BAD_REQUEST);
        }
        try {
            communityEditorCommService.updateComment(communityEditorCommNum, vo);
            return new ResponseEntity<>("댓글이 수정되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("댓글 수정 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/commentsByEditor/{communityEditorNum}")
    public List<Map<String, Object>> getCommentsByEditor(
            @PathVariable("communityEditorNum") Long communityEditorNum) {

        try {
            Map<String, Object> params = Map.of("communityEditorNum", communityEditorNum);
            return communityEditorCommService.getCommentsByCommunityEditorNum(params);
        } catch (Exception e) {
            // 오류 발생 시 로그를 남기면 문제 해결에 도움이 될 수 있음
            System.out.println("댓글 조회 중 오류 발생: " + e.getMessage());
            return Collections.emptyList(); // 빈 리스트를 반환하도록 변경
        }
    }

    //특정 커뮤니티 댓글 갯수 조회
    @GetMapping("/count/{communityEditorNum}")
    public ResponseEntity<Long> countCommentsByEditor(@PathVariable Long communityEditorNum) {
        try {
            Long count = communityEditorCommService.countCommentsByCommunityEditorNum(communityEditorNum);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0L);
        }
    }
}