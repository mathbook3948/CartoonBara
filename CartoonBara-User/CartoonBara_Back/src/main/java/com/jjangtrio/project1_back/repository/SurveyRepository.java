package com.jjangtrio.project1_back.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.jjangtrio.project1_back.entity.Survey;

import jakarta.transaction.Transactional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE surveycontent SET surveycnt = surveycnt + 1" +
            " WHERE subcode = :subcode AND surveytype = :surveytype", nativeQuery = true)
    void incrementSurveyCount(@Param("subcode") Long subcode, @Param("surveytype") String surveytype);

    // 마이페이지 커뮤니티 카운트
    @Query(value = """
            SELECT COUNT(*)
            FROM community c
            WHERE c.user_num = :userNum
            """, nativeQuery = true)
    Long countCommunityByUserNum(@Param("userNum") Long userNum);

    // 마이페이지 커뮤니티리스트 쿼리
    @Query(value = """
            SELECT community_num,
            numbered.community_title,
            numbered.community_content,
            numbered.community_date,
            numbered.community_hit,
            numbered.community_category,
            numbered.community_image,
            numbered.user_num,
            user_nickname,
            numbered.row_num
            FROM (
            SELECT ROW_NUMBER() OVER (ORDER BY community_num DESC) AS row_num,
            c.community_num community_num,
            c.community_title community_title,
            c.community_content community_content,
            c.community_date community_date,
            c.community_hit community_hit,
            c.community_category community_category,
            c.community_image community_image,
            c.user_num user_num,
            u.user_nickname user_nickname
            FROM community c, "user" u
            WHERE c.user_num = u.user_num AND c.user_num = :userNum
            ) numbered
            WHERE numbered.row_num BETWEEN :startIndex AND :endIndex
            ORDER BY numbered.row_num
            """, nativeQuery = true)
    List<Object[]> findCommunityListmyPage(@Param("startIndex") int startIndex,
            @Param("endIndex") int endIndex, @Param("userNum") Long userNum);
}
