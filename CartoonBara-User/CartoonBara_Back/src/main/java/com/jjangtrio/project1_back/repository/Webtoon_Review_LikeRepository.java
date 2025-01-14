package com.jjangtrio.project1_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import io.lettuce.core.dynamic.annotation.Param;

import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.entity.Webtoon_Review;
import com.jjangtrio.project1_back.entity.Webtoon_Review_Like;

public interface Webtoon_Review_LikeRepository extends JpaRepository<Webtoon_Review_Like, Long> {

        List<Webtoon_Review_Like> findByWebtoonReviewLikeNum(Long webtoonReviewNum);

        // 특정 웹툰 리뷰에 대해 좋아요 개수 반환
        @Query("""
                            SELECT COUNT(wrl) FROM Webtoon_Review_Like wrl
                            WHERE wrl.webtoonReview = :webtoonReview
                            AND wrl.webtoonReviewLikeIslike = 1
                        """)
        Long getWebtoonReviewLike(@Param("webtoonReview") Webtoon_Review webtoonReview);

        // 특정 웹툰 리뷰에 대해 싫어요 개수 반환
        @Query("""
                            SELECT COUNT(wrl) FROM Webtoon_Review_Like wrl
                            WHERE wrl.webtoonReview = :webtoonReview
                            AND wrl.webtoonReviewLikeIslike = 0
                        """)
        Long getWebtoonReviewDisLike(@Param("webtoonReview") Webtoon_Review webtoonReview);

        // 사용자가 특정 웹툰 리뷰에 대해 좋아요/싫어요를 눌렀는지 확인
        @Query("""
                        SELECT CASE
                               WHEN EXISTS (
                                   SELECT 1
                                   FROM Webtoon_Review_Like wrl
                                   WHERE wrl.webtoonReview.webtoonReviewNum = :webtoonReviewNum
                                   AND wrl.user = :user
                                   AND wrl.webtoonReviewLikeIslike = :isLike
                               ) THEN true
                               ELSE false
                               END
                        """)
        boolean existsByWebtoonReviewNumAndUserAndIsLike(@Param("webtoonReviewNum") Long webtoonReviewNum,
                        @Param("user") User user,
                        @Param("isLike") Long isLike);

        // 사용자가 좋아요를 누른 웹툰 리뷰 반환
        Webtoon_Review_Like findByUser(User user);

        // 사용자와 특정 웹툰 리뷰에 대해 좋아요 여부를 확인
        Webtoon_Review_Like findByUserAndWebtoonReview(User user, Webtoon_Review webtoonReview);

        // 특정 웹툰 리뷰와 사용자가 좋아요/싫어요를 누른 기록을 반환
        @Query("""
                            SELECT wrl FROM Webtoon_Review_Like wrl
                            WHERE wrl.webtoonReview = :webtoonReview
                            AND wrl.user = :user
                        """)
        List<Webtoon_Review_Like> findByWebtoonReviewAndUser(@Param("webtoonReview") Webtoon_Review webtoonReview,
                        @Param("user") User user);
}
