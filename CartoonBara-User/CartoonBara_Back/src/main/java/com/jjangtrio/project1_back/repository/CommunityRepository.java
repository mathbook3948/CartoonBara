package com.jjangtrio.project1_back.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.jjangtrio.project1_back.entity.Community;
import com.jjangtrio.project1_back.vo.CommunityVO;
import com.jjangtrio.project1_back.vo.PageVO;

import jakarta.transaction.Transactional;

public interface CommunityRepository extends JpaRepository<Community, Long> {

        // 페이징 처리
        @Query(value = """
                        SELECT community_num,
                        numbered.community_title,
                        numbered.community_content,
                        numbered.community_date,
                        numbered.community_hit,
                        numbered.community_category,
                        numbered.community_image,
                        numbered.user_num,
                        u.user_nickname,
                        u.user_image
                        FROM (
                        SELECT ROW_NUMBER() OVER (ORDER BY community_num DESC) AS row_num,
                        community_num,
                        community_title,
                        community_content,
                        community_date,
                        community_hit,
                        community_category,
                        community_image,
                        user_num
                        FROM community
                        WHERE community_category BETWEEN 1 AND 4
                        ) numbered, "user" u
                        WHERE numbered.row_num BETWEEN :startIndex AND :endIndex
                        AND numbered.user_num = u.user_num
                        ORDER BY numbered.row_num
                        """, nativeQuery = true)
        List<Object[]> findCommunityWithinRange(@Param("startIndex") int startIndex, @Param("endIndex") int endIndex);

        @Query(value = """
                        SELECT community_num,
                        numbered.community_title,
                        numbered.community_content,
                        numbered.community_date,
                        numbered.community_hit,
                        numbered.community_category,
                        numbered.community_image,
                        numbered.user_num,
                        u.user_nickname,
                        u.user_image
                        FROM (
                        SELECT ROW_NUMBER() OVER (ORDER BY community_num DESC) AS row_num,
                        community_num,
                        community_title,
                        community_content,
                        community_date,
                        community_hit,
                        community_category,
                        community_image,
                        user_num
                        FROM community WHERE community_category = :communityCategory
                        ) numbered, "user" u
                        WHERE numbered.row_num BETWEEN :startIndex AND :endIndex
                        AND numbered.user_num = u.user_num
                        ORDER BY numbered.row_num
                        """, nativeQuery = true)
        List<Object[]> findCommunityWithinRangeWithCategory(@Param("startIndex") int startIndex,
                        @Param("endIndex") int endIndex, @Param("communityCategory") Long communityCategory);

        // 제목으로 검색한 총 게시물 수
        @Query(value = "SELECT COUNT(*) FROM community c WHERE (:communityTitle IS NULL OR c.community_title LIKE '%' || :communityTitle || '%')", nativeQuery = true)
        Long countByTitleContaining(@Param("communityTitle") String communityTitle);

        // 조회수 업데이트
        @Modifying
        @Transactional
        @Query(value = "UPDATE community SET community_hit = community_hit + 1 WHERE community_num = :community_num", nativeQuery = true)
        void updateHit(@Param("community_num") Long community_num);

        @Query("SELECT c FROM Community c JOIN FETCH c.user")
        List<Community> findAllWithUser();

        @Query(value = "SELECT * FROM community WHERE community_category = :communityCategory", nativeQuery = true)
        List<Community> findByCommunityCategory(@Param("communityCategory") Long communityCategory);

        @Query(value = "SELECT COUNT(*) FROM community WHERE community_category = :communityCategory", nativeQuery = true)
        Long countByCommunityCategory(@Param("communityCategory") Long communityCategory);

        @Query(value = "SELECT COUNT(*) FROM community WHERE community_category NOT IN (5)", nativeQuery = true)
        Long countByCommunity();

        // 익명 게시판을 제외한 제목으로만 검색하는 메서드
        @Query(value = "SELECT * FROM community WHERE community_category NOT IN (5) AND community_title LIKE '%' || :title || '%'", nativeQuery = true)
        List<Community> findByCommunityTitleContaining(String title);

        // 익명게시판 검색기능
        @Query(value = "SELECT * FROM community WHERE community_category = 5 AND community_title LIKE '%' || :title || '%'", nativeQuery = true)
        List<Community> findByCommunityTitleContainingForCategory(String title);

        // 좋아요
        @Query("SELECT c FROM Community c JOIN FETCH c.user WHERE c.communityNum = :communityNum")
        Optional<Community> findCommunityWithUser(@Param("communityNum") Long communityNum);

        // other페이지 커뮤니티 불러오기
        List<Community> findAllByUser_UserNum(Long userNum);

        // MyPage에서 검색
        public List<Community> findByCommunityTitle(String communityTitle);

        // MyPage_내가 좋아요한 커뮤니티
        @Query("SELECT c FROM Community c JOIN Community_Like cl ON c.communityNum = cl.community.communityNum "
                        + "WHERE cl.user.userNum = :userNum AND cl.communityLikeIslike = 1")
        List<Community> findLikedCommunitiesByUser(@Param("userNum") Long userNum);

        // otherpage 페이징처리
        @Query(value = """
                        SELECT community_num,
                        numbered.community_title,
                        numbered.community_content,
                        numbered.community_date,
                        numbered.community_hit,
                        numbered.community_category,
                        numbered.community_image,
                        numbered.user_num,
                        u.user_nickname
                        FROM (
                        SELECT ROW_NUMBER() OVER (ORDER BY community_num DESC) AS row_num,
                        community_num,
                        community_title,
                        community_content,
                        community_date,
                        community_hit,
                        community_category,
                        community_image,
                        user_num
                        FROM community
                        ) numbered, "user" u
                        WHERE numbered.row_num BETWEEN :startIndex AND :endIndex
                        AND numbered.user_num = u.user_num AND numbered.user_num = :userNum
                        ORDER BY numbered.row_num
                        """, nativeQuery = true)
        List<Object[]> findCommunityListotherPage(@Param("startIndex") int startIndex,
                        @Param("endIndex") int endIndex, @Param("userNum") Long userNum);

        // ======================================================================================================================
        // 김채린이 추가함 오류나면 불러
        // 신고 당한 글인지 판별
        @Query(value = "SELECT 1 FROM community  WHERE community_num = :communityNum AND community_singo_flag = 1", nativeQuery = true)
        Long IsSingoFlagActive(@Param("communityNum") Long communityNum);

        // 신고 당한 글로 등록
        @Modifying
        @Transactional
        @Query(value = "UPDATE community SET community_singo_flag = 1 WHERE community_num = :communityNum", nativeQuery = true)
        Integer updateSingoFlag(@Param("communityNum") Long communityNum);

        // =======================================================================================================================
        // 익명게시판을 제외한 인기글
        @Query(value = "SELECT * FROM community WHERE community_category NOT IN (5) ORDER BY community_num DESC", nativeQuery = true)
        List<Community> findTop10ByOrderByCommunityNumDescNative();

        // 익명게시판만 해당하는 인기글
        @Query(value = "SELECT * FROM community WHERE community_category = 5 ORDER BY community_num DESC", nativeQuery = true)
        List<Community> findAllOrderByCommunity();

        // =============================================================================================================
        @Query(value = """
                                                SELECT *
                        FROM (
                            SELECT c.*, ROWNUM rnum
                            FROM (
                                SELECT * FROM community
                                WHERE community_category != 4
                                ORDER BY community_num DESC
                            ) c
                            WHERE ROWNUM <= 10
                        )
                                                """, nativeQuery = true)
        List<Community> findTop10ByOrderByCommunityNumDesc();

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

        List<Community> findByCommunityTitleAndUser_UserNum(String communityTitle, Long userNum);

}
