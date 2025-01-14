package com.jjangtrio.project1_back.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jjangtrio.project1_back.entity.Community_Editor;
import com.jjangtrio.project1_back.entity.Community_Editor_Star;
import com.jjangtrio.project1_back.entity.User;

@Repository
public interface Community_Editor_StarRepository extends JpaRepository<Community_Editor_Star, Long> {
        // 특정 게시물의 별점 데이터를 조회하는 역할 = 이 게시물에 "별 1개"를 준 사용자의 수를 반환.
        // @Query("SELECT COUNT(s) FROM Community_Editor_Star s WHERE s.communityEditor
        // = :communityEditor AND s.communityEditorStar = 1")
        // Long getStar(@Param("communityEditor") Community_Editor communityEditor);

        @Query(value = "SELECT COMMUNITY_EDITOR_STAR FROM Community_Editor_Star WHERE community_editor_num = :communityEditorNum AND USER_NUM = :userNum", nativeQuery = true)
        Long getStar(@Param("communityEditorNum") Long communityEditorNum,
                        @Param("user") Long userNum);

        Community_Editor_Star findByUserAndCommunityEditor(User user, Community_Editor community_Editor);

        @Query(value = "SELECT AVG(c.community_editor_star) FROM community_editor_star c WHERE c.community_editor_num = :communityEditorNum", nativeQuery = true)
        Double calculateAverageStar(@Param("communityEditorNum") Long communityEditorNum);

        Long countByCommunityEditorCommunityEditorNum(Long communityEditorNum);

        @Query(value = "select * from community_editor_star WHERE COMMUNITY_EDITOR_NUM = :communityEditorNum AND USER_NUM = :userNum", nativeQuery = true)
        Optional<Community_Editor_Star> findByUserUserNumAndCommunityEditorCommunityEditorNum(
                        @Param("userNum") Long userNum,
                        @Param("communityEditorNum") Long communityEditorNum);

                        @Query(value = "SELECT e.community_editor_num AS communityEditorNum, " +
                        "e.community_editor_title AS communityEditorTitle, " +
                        "e.community_editor_date AS communityEditorDate, " +
                        "e.community_editor_category AS communityEditorCategory, " +
                        "e.community_editor_image AS communityEditorImageUrl, " +
                        "AVG(s.community_editor_star) AS averageStar " +
                        "FROM community_editor e " +
                        "LEFT JOIN community_editor_star s ON e.community_editor_num = s.community_editor_num " +
                        "GROUP BY e.community_editor_num, e.community_editor_title, e.community_editor_date, " +
                        "e.community_editor_category, e.community_editor_image " +
                        "ORDER BY averageStar DESC", nativeQuery = true)
         List<Object[]> findPopularEditors();


         Long countByCommunityEditorCommunityEditorNumAndUserUserNum(Long communityEditorNum, Long userNum);

}
