package com.jjangtrio.project1_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.entity.Webtoon_Review;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;

public interface Webtoon_ReviewRepository extends JpaRepository<Webtoon_Review, Long> {

    List<Webtoon_Review> findByWebtoonWebtoonIdOrderByWebtoonReviewDateDesc(Long webtoonId);

    // 마이페이지 리뷰리스트
    List<Webtoon_Review> findByUser(User user);

    // ======================================================================================================================
    // 김채린이 추가함 오류나면 불러

    // 신고 당한 글인지 판별
    @Query(value = "SELECT 1 FROM webtoon_review  WHERE webtoon_review_num = :webtoonReviewNum AND webtoon_review_singo_flag = 1", nativeQuery = true)
    Long IsSingoFlagActive(@Param("webtoonReviewNum") Long webtoonReviewNum);

    // 신고 당한 글로 등록
    @Modifying
    @Transactional
    @Query(value = "UPDATE webtoon_review SET webtoon_review_singo_flag = 1 WHERE webtoon_review_num = :webtoonReviewNum", nativeQuery = true)
    Integer updateSingoFlag(@Param("webtoonReviewNum") Long webtoonReviewNum);

    // =======================================================================================================================

    // webtoonReviewNum으로 리뷰 조회
    Webtoon_Review findByWebtoonReviewNum(Long webtoonReviewNum);

    @Query(value = """
            SELECT subquery.* FROM (
                SELECT a.webtoon_review_num as webtoonReviewNum,
                       a.user_num as userNum,
                       a.user_nickname as userNickname,
                       a.webtoon_review_review as webtoonReviewReview,
                       a.webtoon_review_date as webtoonReviewDate,
                       COUNT(DECODE(b.webtoon_review_like_islike, 1, b.webtoon_review_like_num, NULL))
                           OVER (PARTITION BY a.WEBTOON_REVIEW_NUM) AS like_count,
                       COUNT(DECODE(b.webtoon_review_like_islike, 0, b.webtoon_review_like_num, NULL))
                           OVER (PARTITION BY a.webtoon_review_num) AS disLike_count,
                       ROW_NUMBER() OVER (ORDER BY a.webtoon_review_num DESC) AS row_num
                FROM webtoon_review a
                LEFT JOIN webtoon_review_like b ON a.webtoon_review_num = b.webtoon_review_num
                WHERE a.webtoon_id = :webtoonId
            ) subquery
            WHERE subquery.row_num BETWEEN :startIndex AND :endIndex
            ORDER BY subquery.row_num
                                                """, nativeQuery = true)
    List<Object[]> getWebtoonReviewAndWebtoonReviewLikeListWithPaging(
            @Param("webtoonId") Long webtoonId,
            @Param("startIndex") int startIndex,
            @Param("endIndex") int endIndex);

    @Query(value = """
            SELECT COUNT(a.webtoon_review_num)
            FROM webtoon_review a
            WHERE webtoon_id = :webtoonId
                          """, nativeQuery = true)
    Long getWebtoonReviewCount(@Param("webtoonId") Long webtoonId);

    // MyPage_내가 좋아요한 웹툰 리뷰
    @Query("SELECT wr FROM Webtoon_Review wr JOIN FETCH wr.user u JOIN FETCH wr.webtoon w")
    List<Webtoon_Review> findAllWithUser();

    @Query("SELECT wr FROM Webtoon_Review wr " +
            "JOIN Webtoon_Review_Like wrl ON wr.webtoonReviewNum = wrl.webtoonReview.webtoonReviewNum " +
            "WHERE wrl.user.userNum = :userNum AND wrl.webtoonReviewLikeIslike = 1")
    List<Webtoon_Review> findLikedWebtoonsByUser(@Param("userNum") Long userNum);

    // 아더페이지 페이징처리
    @Query(value = """
                SELECT
                    numbered.WEBTOON_REVIEW_NUM,
                    numbered.USER_NICKNAME,
                    numbered.WEBTOON_REVIEW_REVIEW,
                    numbered.WEBTOON_REVIEW_DATE,
                    numbered.WEBTOON_ID,
                    w.WEBTOON_TITLE,
                    u.user_num
                FROM (
                    SELECT ROW_NUMBER() OVER(ORDER BY WEBTOON_REVIEW_NUM DESC) AS ROW_NUM,
                           WEBTOON_REVIEW_NUM,
                           WEBTOON_ID,
                           USER_NUM,
                           USER_NICKNAME,
                           WEBTOON_REVIEW_REVIEW,
                           WEBTOON_REVIEW_DATE
                    FROM WEBTOON_REVIEW
                ) numbered, "user" u, webtoon w
                WHERE numbered.ROW_NUM BETWEEN :startIndex AND :endIndex
                AND numbered.user_num = :userNum AND
                w.webtoon_id = numbered.webtoon_id AND
                numbered.user_num = u.user_num
                ORDER BY numbered.ROW_NUM
            """, nativeQuery = true)
    List<Object[]> findWebtoonReviewListotherPage(
            @Param("startIndex") int startIndex,
            @Param("endIndex") int endIndex,
            @Param("userNum") Long userNum);

    @Query(value = "SELECT COUNT(*) FROM WEBTOON_REVIEW WHERE WEBTOON_REVIEW_NUM = :WEBTOON_REVIEW_NUM", nativeQuery = true)
    Long countByWebtoonReview(@Param("WEBTOON_REVIEW_NUM") Long WEBTOON_REVIEW_NUM);

    // 마이페이지 리뷰리스트 페이징처리 카운트
    @Query(value = """
            SELECT COUNT(*)
            FROM WEBTOON_REVIEW u
            WHERE u.user_num = :userNum
            """, nativeQuery = true)
    Long countWebtoonRiviewByUserNum(@Param("userNum") Long userNum);

    // 마이페이지 리뷰리스트 페이징처리
    @Query(value = """
                SELECT
                    numbered.WEBTOON_REVIEW_NUM,
                    numbered.USER_NICKNAME,
                    numbered.WEBTOON_REVIEW_REVIEW,
                    numbered.WEBTOON_REVIEW_DATE,
                    numbered.WEBTOON_ID,
                    webtoon.WEBTOON_TITLE
                FROM (
                    SELECT ROW_NUMBER() OVER(ORDER BY WEBTOON_REVIEW_NUM DESC) AS ROW_NUM,
                           WEBTOON_REVIEW_NUM,
                           WEBTOON_ID,
                           USER_NUM,
                           USER_NICKNAME,
                           WEBTOON_REVIEW_REVIEW,
                           WEBTOON_REVIEW_DATE
                    FROM WEBTOON_REVIEW
                ) numbered
                JOIN "user" u ON numbered.USER_NUM = u.USER_NUM
                JOIN webtoon webtoon ON numbered.WEBTOON_ID = webtoon.WEBTOON_ID
                WHERE numbered.ROW_NUM BETWEEN :startIndex AND :endIndex
                AND numbered.USER_NUM = :userNum
                ORDER BY numbered.ROW_NUM
            """, nativeQuery = true)
    List<Object[]> findWebtoonReviewListWithTitle(
            @Param("startIndex") int startIndex,
            @Param("endIndex") int endIndex,
            @Param("userNum") Long userNum);

    // 좋아요 순
    @Query(value = """
            SELECT subquery.* FROM (
                    SELECT a.webtoon_review_num as webtoonReviewNum,
                           a.user_num as userNum,
                           a.user_nickname as userNickname,
                           a.webtoon_review_review as webtoonReviewReview,
                           a.webtoon_review_date as webtoonReviewDate,
                           COUNT(DECODE(b.webtoon_review_like_islike, 1, b.webtoon_review_like_num, NULL))
                               OVER (PARTITION BY a.WEBTOON_REVIEW_NUM) AS like_count,
                           COUNT(DECODE(b.webtoon_review_like_islike, 0, b.webtoon_review_like_num, NULL))
                               OVER (PARTITION BY a.webtoon_review_num) AS disLike_count,
                           ROW_NUMBER() OVER (ORDER BY a.webtoon_review_num DESC) AS row_num
                    FROM webtoon_review a
                    LEFT JOIN webtoon_review_like b ON a.webtoon_review_num = b.webtoon_review_num
                    WHERE a.webtoon_id = :webtoonId
                ) subquery
                WHERE subquery.row_num BETWEEN :startIndex AND :endIndex
                ORDER BY like_count DESC
                """, nativeQuery = true)
    List<Object[]> getWebtoonReviewAndWebtoonReviewLikeDescLike(@Param("webtoonId") Long webtoonId,
            @Param("startIndex") int startIndex,
            @Param("endIndex") int endIndex);

    @Query(value = """
            SELECT a.WEBTOON_REVIEW_NUM,
                   a.USER_NUM,
                   a.USER_NICKNAME,
                   a.WEBTOON_REVIEW_REVIEW,
                   a.WEBTOON_REVIEW_DATE,
                   COUNT(DECODE(b.webtoon_review_like_islike, 1, b.WEBTOON_REVIEW_LIKE_NUM, NULL)) OVER (PARTITION BY a.WEBTOON_REVIEW_NUM) AS like_count,
                   COUNT(DECODE(b.webtoon_review_like_islike, 0, b.WEBTOON_REVIEW_LIKE_NUM, NULL)) OVER (PARTITION BY a.WEBTOON_REVIEW_NUM) AS disLike_count
            FROM webtoon_review a
            LEFT JOIN webtoon_review_like b ON a.webtoon_review_num = b.webtoon_review_num
            WHERE a.webtoon_id = :webtoonId ORDER BY a.webtoon_review_num DESC
            """, nativeQuery = true)
    List<Object[]> getWebtoonReviewAndWebtoonReviewLikeList(@Param("webtoonId") Long webtoonId);

}