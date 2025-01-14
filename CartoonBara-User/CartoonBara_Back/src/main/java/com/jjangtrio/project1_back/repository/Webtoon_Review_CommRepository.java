package com.jjangtrio.project1_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.jjangtrio.project1_back.entity.Webtoon_Review_Comm;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;

public interface Webtoon_Review_CommRepository extends JpaRepository<Webtoon_Review_Comm, Long> {

    // ======================================================================================================================
    // 김채린이 추가함 오류나면 불러

    // 신고 당한 글인지 판별
    @Query(value = "SELECT 1 FROM webtoon_review_comm  WHERE webtoon_review_comm_num = :webtoonReviewCommNum AND webtoon_review_comm_singo_flag = 1", nativeQuery = true)
    Long IsSingoFlagActive(@Param("webtoonReviewCommNum") Long webtoonReviewCommNum);

    // 신고 당한 글로 등록
    @Modifying
    @Transactional
    @Query(value = "UPDATE webtoon_review_comm SET webtoon_review_comm_singo_flag = 1 WHERE webtoon_review_comm_num = :webtoonReviewCommNum", nativeQuery = true)
    Integer updateSingoFlag(@Param("webtoonReviewCommNum") Long webtoonReviewCommNum);

    // =======================================================================================================================

    List<Webtoon_Review_Comm> findByWebtoonReviewWebtoonReviewNumOrderByWebtoonReviewCommNumDesc(Long webtoonReviewNum);

    @Query(value = """
            SELECT
            WEBTOON_REVIEW_COMM_NUM,
            USER_NICKNAME,
            WEBTOON_REVIEW_COMM_CONTENT,
            WEBTOON_REVIEW_COMM_DATE,
            WEBTOON_REVIEW_NUM
            FROM (
                SELECT ROW_NUMBER() OVER (ORDER BY WEBTOON_REVIEW_COMM_NUM DESC) AS ROW_NUM,
                       WEBTOON_REVIEW_COMM_NUM,
                       USER_NICKNAME,
                       WEBTOON_REVIEW_COMM_CONTENT,
                       WEBTOON_REVIEW_COMM_DATE,
                       WEBTOON_REVIEW_NUM
                FROM WEBTOON_REVIEW_COMM
                JOIN USER ON WEBTOON_REVIEW_COMM.USER_NUM = USER.USER_NUM
                WHERE WEBTOON_REVIEW_NUM = :webtoonReviewNum
            ) numbered
            WHERE numbered.ROW_NUM BETWEEN :startIndex AND :endIndex
            ORDER BY numbered.WEBTOON_REVIEW_COMM_NUM DESC
            """, nativeQuery = true)
    List<Object[]> findWebtoonReviewCommWithinPage(@Param("startIndex") int startIndex,
            @Param("endIndex") int endIndex,
            @Param("webtoonReviewNum") Long webtoonReviewNum);

}
