package com.jjangtrio.project1_back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jjangtrio.project1_back.entity.Community;
import com.jjangtrio.project1_back.entity.Community_Like;
import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.repository.CommunityRepository;
import com.jjangtrio.project1_back.repository.Community_LikeRepository;
import com.jjangtrio.project1_back.repository.UserRepository;

@Service
public class Community_LikeService {

    @Autowired
    private Community_LikeRepository communityLikeRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private UserRepository userRepository;

    // 좋아요 상태 변경
    public Community_Like toggleLike(User user, Community community) {
        Community_Like communityLike = communityLikeRepository.findByUserAndCommunity(user, community);

        // 좋아요가 존재하면 상태를 반대로 변경
        if (communityLike != null) {
            communityLike.setCommunityLikeIslike(communityLike.getCommunityLikeIslike() == 1L ? 0L : 1L);
            return communityLikeRepository.save(communityLike);
        } else {
            // 좋아요가 없으면 새로 생성
            Community_Like newLike = new Community_Like();
            newLike.setUser(user);
            newLike.setCommunity(community);
            newLike.setCommunityLikeIslike(1L); // 초기 값은 좋아요(0)
            return communityLikeRepository.save(newLike);
        }
    }


    // 좋아요 상태 확인
    public boolean checkLike(Long communityId, Long userId, Long isLike) {
        User user = new User();
        user.setUserNum(userId);
        Community community = new Community();
        community.setCommunityNum(communityId);

        return communityLikeRepository.existsByUserAndCommunityAndCommunityIsLike(user, community, isLike);
    }

    // 좋아요 전체 갯수 확인
    public Long countLike(Long communityId) {
        Community community = new Community();
        community.setCommunityNum(communityId);
        return communityLikeRepository.countByCommunityAndCommunityLikeIslike(community);
    }

    // 좋아요 상태 확인 서비스 메서드
    public boolean checkLikes(Long communityNum, Long userNum, Long isLike) {
        // 커뮤니티와 사용자 객체 생성
        Community community = new Community();
        community.setCommunityNum(communityNum); // 커뮤니티 번호 설정
        User user = new User();
        user.setUserNum(userNum); // 사용자 번호 설정

        // findByUserAndCommunityAndCommunityLikeIslike 메서드를 호출하여 좋아요 상태 확인
        Community_Like communityLike = communityLikeRepository.findByUserAndCommunityAndCommunityIsLike(user,
                community, isLike);

        // 만약 해당 조건에 맞는 좋아요 상태가 존재하면 true, 그렇지 않으면 false 반환
        return communityLike != null;
    }

}
