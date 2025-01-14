package com.jjangtrio.project1_back.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jjangtrio.project1_back.entity.Community_Editor;
import com.jjangtrio.project1_back.entity.Community_Editor_Star;
import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.repository.Community_Editor_StarRepository;

import jakarta.transaction.Transactional;

@Service
public class Community_EditorStarService {

    @Autowired
    private Community_Editor_StarRepository communityEditorStarRepository;

    @Transactional
    public void toggleStar(User user, Community_Editor community_Editor, Community_Editor_Star star) {
        System.out.println("userNum: " + user.getUserNum());
        System.out.println("communityEditorNum: " + community_Editor.getCommunityEditorNum());
        System.out.println("starValue: " + star.getCommunityEditorStar());

        Optional<Community_Editor_Star> optionalStar = communityEditorStarRepository
                .findByUserUserNumAndCommunityEditorCommunityEditorNum(user.getUserNum(),
                        community_Editor.getCommunityEditorNum());

        // 만약 별점 정보가 이미 존재한다면
        if (optionalStar.isPresent()) {
            // 기존 별점 정보 수정
            Community_Editor_Star existingStar = optionalStar.get();
            existingStar.setCommunityEditorStar(star.getCommunityEditorStar());
            communityEditorStarRepository.save(existingStar);
            System.out.println("Updated existing star entry.");
        } else {
            // 새 별점 정보 추가
            Community_Editor_Star newStar = new Community_Editor_Star();
            newStar.setUser(user);
            newStar.setCommunityEditor(community_Editor);
            newStar.setCommunityEditorStar(star.getCommunityEditorStar());
            communityEditorStarRepository.save(newStar);
            System.out.println("Created new star entry.");
        }
    }

    // 특정 게시물의 별점 데이터를 조회하는 역할
    @Transactional
public Long countStar(Long communityEditorNum, Long userNum) {
    // communityEditorNum 및 userNum으로 별점 조회
    return communityEditorStarRepository.countByCommunityEditorCommunityEditorNumAndUserUserNum(
        communityEditorNum, userNum);
}

    public Double calculateAverageStar(Long communityEditorNum) {
        return communityEditorStarRepository.calculateAverageStar(communityEditorNum);
    }

    public Long getTotlaStars(Long communityEditorNum) {
        return communityEditorStarRepository.countByCommunityEditorCommunityEditorNum(communityEditorNum);
    }

    public List<Map<String, Object>> getPopularEditors() {
        List<Object[]> results = communityEditorStarRepository.findPopularEditors();

        // 반환된 데이터를 매핑
        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("communityEditorNum", row[0]);
            map.put("communityEditorTitle", row[1]);
            map.put("communityEditorDate", row[2]);
            map.put("communityEditorCategory", row[3]);
            map.put("communityEditorImage", row[4]);
            map.put("averageStar", row[5]);
            return map;
        }).collect(Collectors.toList());
    }

}
