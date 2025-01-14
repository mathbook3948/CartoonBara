package com.jjangtrio.project1_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jjangtrio.project1_back.entity.Community;
import com.jjangtrio.project1_back.entity.Qna;
import com.jjangtrio.project1_back.entity.User;

import io.lettuce.core.dynamic.annotation.Param;

public interface QnaRepository extends JpaRepository<Qna, Long> {

    List<Qna> findAllByOrderByQnaNumDesc();

     // 페이징
    // pvo startIndex~endIndex
    @Query(value = """
                SELECT 
                    numbered.qna_num,
                    numbered.user_num, 
                    numbered.qna_category, 
                    numbered.qna_title, 
                    numbered.qna_content, 
                    numbered.qna_answer, 
                    numbered.qna_image, 
                    numbered.qna_question_date, 
                    numbered.qna_answer_date, 
                    u.user_nickname
                FROM (
                    SELECT
                        ROW_NUMBER() OVER (ORDER BY qna_num DESC) AS row_num,
                        qna_num,
                        user_num, 
                        qna_category, 
                        qna_title, 
                        qna_content, 
                        qna_answer, 
                        qna_image, 
                        qna_question_date, 
                        qna_answer_date
                    FROM qna
                ) numbered, "user" u
                WHERE numbered.row_num BETWEEN :startIndex AND :endIndex
                AND numbered.user_num = u.user_num
                ORDER BY numbered.row_num
            """, nativeQuery = true)
    List<Object[]> findQnaWithinRange(@Param("startIndex") int startIndex, @Param("endIndex") int endIndex);


    // // 제목으로 검색
    // @Query(value = "SELECT COUNT(*) FROM QNA g WHERE g.QNA_TITLE LIKE '%' ||
    // :title || '%'", nativeQuery = true)
    // Long countByTitleContaining(@Param("title") String title);

    // 제목으로만 검색하는 메서드
    List<Qna> findByQnaTitleContaining(String title);

    @Query(value = "SELECT COUNT(*) FROM qna", nativeQuery = true)
    Long countByQnaNum();

    // 마이페이지 1대1 문의 리스트
    List<Qna> findAllByUser(User user);

    // 마이페이지 1대1문의 페이징처리 카운트
    @Query(value = """
            SELECT COUNT(*)
            FROM QNA g
            WHERE g.USER_NUM = :userNum
            """, nativeQuery = true)
    Long countQnaByUserNum(@Param("userNum") Long userNum);

    // 마이페이지 1대1문의 페이징처리
    @Query(value = """
            SELECT
            QNA_NUM,
            numbered.QNA_CATEGORY,
            numbered.QNA_TITLE,
            numbered.QNA_QUESTION_DATE,
            numbered.QNA_ANSWER_DATE,
            numbered.USER_NUM,
            numbered.row_num
            FROM (
            SELECT ROW_NUMBER() OVER (ORDER BY QNA_NUM DESC) AS ROW_NUM,
            q.QNA_NUM QNA_NUM,
            q.QNA_CATEGORY QNA_CATEGORY,
            q.QNA_TITLE QNA_TITLE,
            q.QNA_QUESTION_DATE QNA_QUESTION_DATE,
            q.QNA_ANSWER_DATE QNA_ANSWER_DATE,
            u.USER_NUM USER_NUM
            FROM QNA q, "user" u
            WHERE q.USER_NUM = u.USER_NUM AND q.USER_NUM = :userNum
            ) numbered
            WHERE numbered.ROW_NUM BETWEEN :startIndex AND :endIndex
            ORDER BY numbered.ROW_NUM
                                    """, nativeQuery = true)
    List<Object[]> findQnaListWithTitle(
            @Param("startIndex") int startIndex,
            @Param("endIndex") int endIndex,
            @Param("userNum") Long userNum);

}
