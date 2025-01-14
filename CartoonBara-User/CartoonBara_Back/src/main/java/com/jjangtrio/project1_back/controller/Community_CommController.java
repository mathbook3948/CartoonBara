package com.jjangtrio.project1_back.controller;

import java.util.Date;
import java.util.List;

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

import com.jjangtrio.project1_back.service.Community_CommService;
import com.jjangtrio.project1_back.vo.CommunityVO;
import com.jjangtrio.project1_back.vo.Community_CommVO;
import com.jjangtrio.project1_back.vo.UserVO;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/community_comm")
@CrossOrigin("*")
public class Community_CommController {

    @Autowired
    private Community_CommService community_CommService;

    // 댓글 추가
    @PostMapping("/add")
    public ResponseEntity<String> addComment(
            @RequestParam("communityNum") Long communityNum,
            @RequestParam("communityCommContent") String communityCommContent,
            HttpServletRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userNum = Long.valueOf(auth.getPrincipal().toString());

        try {
            // 클라이언트 IP 주소 가져오기
            String clientIp = request.getRemoteAddr();

            // Community_CommVO 객체 생성 시, 날짜를 현재 시간으로 자동 설정
            Community_CommVO community_CommVO = Community_CommVO.builder()
                    .user(UserVO.builder().userNum(userNum).build())
                    .community(CommunityVO.builder().communityNum(communityNum).build())
                    .communityCommContent(communityCommContent)
                    .communityCommDate(new Date()) // 현재 시간으로 설정
                    .communityCommIp(clientIp) // IP 주소 설정
                    .build();

            // 댓글 추가
            community_CommService.addComment(community_CommVO);
            return new ResponseEntity<>("댓글이 추가되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("댓글 추가 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 댓글 전체
    @GetMapping("/all")
    public ResponseEntity<List<Community_CommVO>> getAllComments() {
        try {
            List<Community_CommVO> comments = community_CommService.getAllComments();
            return new ResponseEntity<>(comments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // 특정 커뮤니티의 댓글 조회
    @GetMapping("/community/{communityNum}")
    public ResponseEntity<List<Community_CommVO>> getCommentsByCommunity(@PathVariable Long communityNum) {
        try {
            List<Community_CommVO> comments = community_CommService.getCommentsByCommunity(communityNum);
            return new ResponseEntity<>(comments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // 특정 유저의 댓글 조회
    @GetMapping("/user/{userNum}")
    public ResponseEntity<List<Community_CommVO>> getCommentsByUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userNum = Long.valueOf(auth.getPrincipal().toString());
        try {
            List<Community_CommVO> comments = community_CommService.getCommentsByUser(userNum);
            return new ResponseEntity<>(comments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // 댓글 삭제
    @DeleteMapping("/delete/{communityCommNum}")
    public ResponseEntity<String> deleteComment(@PathVariable Long communityCommNum) {
        try {
            community_CommService.deleteComment(communityCommNum);
            return new ResponseEntity<>("댓글이 삭제되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("댓글 삭제 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 댓글 수정
    @PutMapping("/update/{communityCommNum}")
    public ResponseEntity<String> updateComment(@PathVariable Long communityCommNum,
            @RequestBody Community_CommVO community_CommVO) {
        try {
            community_CommService.updateComment(communityCommNum, community_CommVO);
            return new ResponseEntity<>("댓글이 수정되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("댓글 수정 실패: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    // 특정 커뮤니티의 댓글 갯수 조회
    @GetMapping("/count/{communityNum}")
    public ResponseEntity<Long> getCommentCount(@PathVariable Long communityNum) {
        try {
            Long count = community_CommService.getCommentCountByCommunity(communityNum);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0L);
        }
    }
}
