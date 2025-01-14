package com.jjangtrio.project1_back.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.entity.Webtoon_Review;
import com.jjangtrio.project1_back.entity.Webtoon_Review_Like;
import com.jjangtrio.project1_back.repository.UserRepository;
import com.jjangtrio.project1_back.repository.Webtoon_ReviewRepository;
import com.jjangtrio.project1_back.repository.Webtoon_Review_LikeRepository;

@Service
public class Webtoon_Review_LikeService {

    @Autowired
    private Webtoon_Review_LikeRepository webtoon_Review_LikeRepository;

    @Autowired
    private Webtoon_ReviewRepository webtoon_ReviewRepository;

    @Autowired
    private UserRepository userRepository;

    public static final Long LIKE = 1L;
    public static final Long DISLIKE = 0L;

    // 좋아요 싫어오 갯수 반환
    public Long getWebtoonReviewLikeCount(Long webtoonReivewNum) {
        return webtoon_Review_LikeRepository
                .getWebtoonReviewLike(Webtoon_Review.builder().webtoonReviewNum(webtoonReivewNum).build());
    }

    public Long getWebtoonReviewDisLikeCount(Long webtoonReviewNum) {
        return webtoon_Review_LikeRepository
                .getWebtoonReviewDisLike(Webtoon_Review.builder().webtoonReviewNum(webtoonReviewNum).build());
    }

    @Transactional
    public boolean addWebtoonReviewGoodLike(Long userNum, Long webtoonReviewNum) {
        Webtoon_Review webtoonReview = webtoon_ReviewRepository.findById(webtoonReviewNum)
                .orElseThrow(() -> new RuntimeException("해당 웹툰 리뷰가 없습니다"));
        User user = userRepository.findById(userNum)
                .orElseThrow(() -> new RuntimeException("해당 사용자가 없습니다"));

        // 기존 좋아요 상태 확인
        Optional<Webtoon_Review_Like> existingLikeOpt = webtoon_Review_LikeRepository
                .findByWebtoonReviewAndUser(webtoonReview, user)
                .stream().findFirst();

        if (existingLikeOpt.isPresent()) {
            Webtoon_Review_Like existingLike = existingLikeOpt.get();

            if (existingLike.getWebtoonReviewLikeIslike() == 1) { // 이미 좋아요 상태면
                // 좋아요를 취소
                webtoon_Review_LikeRepository.delete(existingLike);
                return false; // 취소되었으므로 false 반환
            } else if (existingLike.getWebtoonReviewLikeIslike() == 0) { // 싫어요 상태일 경우
                // 싫어요를 취소하고 좋아요로 변경
                existingLike.setWebtoonReviewLikeIslike(1L);
                webtoon_Review_LikeRepository.save(existingLike);
                return true; // 좋아요로 변경되었으므로 true 반환
            }
        }

        // 새로 좋아요 추가
        Webtoon_Review_Like newLike = Webtoon_Review_Like.builder()
                .webtoonReview(webtoonReview)
                .user(user)
                .webtoonReviewLikeIslike(1L) // 좋아요
                .build();

        webtoon_Review_LikeRepository.save(newLike);
        return true; // 좋아요가 새로 추가되었으므로 true 반환
    }

    @Transactional
    public boolean addWebtoonReviewBadLike(Long userNum, Long webtoonReviewNum) {
        Webtoon_Review webtoonReview = webtoon_ReviewRepository.findById(webtoonReviewNum)
                .orElseThrow(() -> new RuntimeException("해당 웹툰 리뷰가 없습니다"));
        User user = userRepository.findById(userNum)
                .orElseThrow(() -> new RuntimeException("해당 사용자가 없습니다"));

        // 기존 싫어요 상태 확인
        Optional<Webtoon_Review_Like> existingLikeOpt = webtoon_Review_LikeRepository
                .findByWebtoonReviewAndUser(webtoonReview, user)
                .stream().findFirst();

        if (existingLikeOpt.isPresent()) {
            Webtoon_Review_Like existingLike = existingLikeOpt.get();

            if (existingLike.getWebtoonReviewLikeIslike() == 0) { // 이미 싫어요 상태라면
                // 싫어요를 취소
                webtoon_Review_LikeRepository.delete(existingLike);
                return false; // 취소되었으므로 false 반환
            } else if (existingLike.getWebtoonReviewLikeIslike() == 1) { // 좋아요 상태일 경우
                // 좋아요를 취소하고 싫어요로 변경
                existingLike.setWebtoonReviewLikeIslike(0L);
                webtoon_Review_LikeRepository.save(existingLike);
                return true; // 싫어요로 변경되었으므로 true 반환
            }
        }

        // 새로 싫어요 추가
        Webtoon_Review_Like newLike = Webtoon_Review_Like.builder()
                .webtoonReview(webtoonReview)
                .user(user)
                .webtoonReviewLikeIslike(0L) // 싫어요
                .build();

        webtoon_Review_LikeRepository.save(newLike);
        return true; // 싫어요가 새로 추가되었으므로 true 반환
    }

}
