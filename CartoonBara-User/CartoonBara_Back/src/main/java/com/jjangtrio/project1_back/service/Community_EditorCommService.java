package com.jjangtrio.project1_back.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jjangtrio.project1_back.entity.Community_Editor;
import com.jjangtrio.project1_back.entity.Community_Editor_Comm;
import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.repository.Community_EditorRepository;
import com.jjangtrio.project1_back.repository.Community_Editor_CommRepository;
import com.jjangtrio.project1_back.repository.UserRepository;
import com.jjangtrio.project1_back.vo.Community_Editor_CommVO;

@Service
public class Community_EditorCommService {

    @Autowired
    private Community_Editor_CommRepository community_Editor_CommRepository;

    @Autowired
    private Community_EditorRepository communityEditorRepository;

    @Autowired
    private UserRepository userRepository;

    // 특정 에디터 번호(editorNum)로 댓글 목록을 조회
    public List<Community_Editor_Comm> getCommentsByEditorNum(Long editorNum) {
        return community_Editor_CommRepository.findByCommunityEditorUserUserNum(editorNum);
    }

    // 특정 커뮤니티 에디터 번호(communityEditorNum)로 댓글 목록을 조회
    public List<Map<String, Object>> getCommentsByCommunityEditorNum(Map<String, Object> maps) {

        try {
            User user = User.builder()
                    .userNum(maps.get("userNum") != null ? Long.parseLong(maps.get("userNum").toString()) : null)
                    .userNickname(maps.get("userNickname") != null ? maps.get("userNickname").toString() : null)
                    .build();

            Community_Editor community_Editor = Community_Editor.builder()
                    .communityEditorNum(
                            maps.get("communityEditorNum") != null
                                    ? Long.parseLong(maps.get("communityEditorNum").toString())
                                    : null)
                    .build();

            List<Map<String, Object>> mapss = new ArrayList<>();

            // 댓글 목록을 조회(정우코드어렵다)
            List<Community_Editor_Comm> reviews = community_Editor_CommRepository
                    .findByCommunityEditorCommunityEditorNumOrderByCommunityEditorCommNum(
                            community_Editor.getCommunityEditorNum());

            for (Community_Editor_Comm review : reviews) {
                Map<String, Object> map = new HashMap<>();
                map.put("communityEditorCommNum", review.getCommunityEditorCommNum());
                map.put("userNum", review.getUser().getUserNum());
                map.put("userNickname", review.getUser().getUserNickname());
                map.put("communityEditorCommContent", review.getCommunityEditorCommContent());

                // 날짜 포맷팅
                String cec = review.getCommunityEditorCommDate().toString();
                String[] cecArr = cec.split(" ");
                String[] cecArr2 = cecArr[0].split("-");
                String[] cecArr3 = cecArr[1].split(":");
                String cecDate = cecArr2[0] + "-" + cecArr2[1] + "-" + cecArr2[2] + " " + cecArr3[0] + ":" + cecArr3[1];
                map.put("communityEditorCommDate", cecDate);

                // 맵에 추가
                mapss.add(map);
            }

            return mapss;
        } catch (Exception e) {
            // 예외 처리 시 로그 등을 추가하는 것도 좋은 습관
            System.out.println("댓글 조회 중 오류 발생: " + e.getMessage());
            return null;
        }
    }

    // 댓글 추가
    public void addComment(Community_Editor_CommVO vo) {
        User user = userRepository.findById(vo.getUserNum())
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));

        // 커뮤니티 에디터 조회
        Community_Editor communityEditor = communityEditorRepository.findById(vo.getCommunityEditor())
                .orElseThrow(() -> new RuntimeException("커뮤니티 에디터가 존재하지 않습니다."));

        // 댓글 엔티티 생성
        Community_Editor_Comm communityEditorComm = Community_Editor_Comm.builder()
                .user(user)
                .communityEditor(communityEditor)
                .communityEditorCommContent(vo.getCommunityEditorCommContent())
                .communityEditorCommDate(new Date())
                .build();

        // 댓글 저장
        community_Editor_CommRepository.save(communityEditorComm);
    }

    // 댓글 삭제
    public void deleteComment(Long communityEditorCommNum) {
        community_Editor_CommRepository.deleteById(communityEditorCommNum);
    }

    // 댓글 수정
    public void updateComment(Long communityEditorCommNum, Community_Editor_CommVO vo) {
        Community_Editor_Comm communityEditorComm = community_Editor_CommRepository.findById(communityEditorCommNum)
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

        communityEditorComm.setCommunityEditorCommContent(vo.getCommunityEditorCommContent());
        communityEditorComm.setCommunityEditorCommDate(new Date());
        community_Editor_CommRepository.save(communityEditorComm);
    }

    // 댓글 갯수 구하기
    public Long countCommentsByCommunityEditorNum(Long communityEditorNum) {
        return community_Editor_CommRepository.countCommentsByCommunityEditorNum(communityEditorNum);
    }

}
