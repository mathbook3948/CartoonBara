package com.jjangtrio.project1_back.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
// import com.jjangtrio.project1_back.project1_back.entity.Editor;

import org.springframework.data.repository.query.Param;
import com.jjangtrio.project1_back.entity.Community_Editor;
import jakarta.transaction.Transactional;

public interface Community_EditorRepository extends JpaRepository<Community_Editor, Long> {

    // 특정 제목(community_editor_title)이 포함된 게시글을 페이징 처리하여 조회
    @Query(value = """
            SELECT * FROM (SELECT ce.*, ROWNUM rn FROM community_editor ce
            WHERE ce.community_editor_title LIKE '%' || :community_editor_title || '%'
            ORDER BY ce.community_editor_num DESC) WHERE rn BETWEEN :startIndex + 1 AND :endIndex
            """, nativeQuery = true)
    List<Community_Editor> getList(@Param("community_editor_title") String community_editor_title,
            @Param("startIndex") int startIndex, @Param("endIndex") int endIndex);

    // 특정 제목(community_editor_title)이 포함된 게시글의 총 개수를 반환
    @Query(value = "SELECT COUNT(*) FROM community_editor ce WHERE ce.community_editor_title LIKE '%' || :community_editor_title || '%'", nativeQuery = true)
    Long countByCommunityEditorTitleContaining(@Param("community_editor_title") String community_editor_title);

    // 특정 게시글의 조회수(hit)를 1 증가
    @Transactional
    @Modifying
    @Query("UPDATE Community_Editor ce SET ce.communityEditorHit = ce.communityEditorHit + 1 WHERE ce.communityEditorNum = :communityEditorNum")
    void updateHit(@Param("communityEditorNum") Long communityEditorNum);

    // 모든 Community_Editor 엔티티와 관련된 Editor 엔티티를 함께 조회
    @Query("SELECT ce FROM Community_Editor ce JOIN FETCH ce.user")
    List<Community_Editor> findAllWithEditor();

    // 특정 Community_Editor와 관련된 Editor를 함께 조회
    List<Community_Editor> findByCommunityEditorNum(Long communityEditorNum);

    // 페이징 처리를 위해 특정 범위의 데이터를 조회하는 역할
    @Query(value = """
            SELECT
                numbered.community_editor_num,
                numbered.user_num,
                numbered.community_editor_category,
                numbered.community_editor_title,
                numbered.community_editor_date,
                numbered.community_editor_hit,
                numbered.community_editor_content,
                numbered.community_editor_ip,
                numbered.community_editor_image,
                u.user_nickname,
                u.user_image
            FROM (
                SELECT
                    ROW_NUMBER() OVER (ORDER BY community_editor_num DESC) AS row_num,
                    community_editor_num,
                    user_num,
                    community_editor_category,
                    community_editor_title,
                    community_editor_date,
                    community_editor_hit,
                    community_editor_content,
                    community_editor_ip,
                    community_editor_image
                FROM community_editor
            ) numbered, "user" u
            WHERE numbered.row_num BETWEEN :startIndex AND :endIndex
            AND numbered.user_num = u.user_num
            ORDER BY numbered.row_num
            """, nativeQuery = true)
    List<Object[]> findCommunityEditorsWithNumbered(
            @Param("startIndex") int startIndex,
            @Param("endIndex") int endIndex);

    // 이전 게시글 (ROWNUM 사용)
    @Query(value = "SELECT * FROM ( " +
            "SELECT * FROM community_editor " +
            "WHERE community_editor_num < :currentId " +
            "ORDER BY community_editor_num DESC" +
            ") WHERE ROWNUM = 1", nativeQuery = true)
    Optional<Community_Editor> findPreviousPost(@Param("currentId") Long currentId);

    // 다음 게시글 (ROWNUM 사용)
    @Query(value = "SELECT * FROM ( " +
            "SELECT * FROM community_editor " +
            "WHERE community_editor_num > :currentId " +
            "ORDER BY community_editor_num ASC" +
            ") WHERE ROWNUM = 1", nativeQuery = true)
    Optional<Community_Editor> findNextPost(@Param("currentId") Long currentId);

    @Query(value = "SELECT COUNT(*) FROM community_editor WHERE community_editor_category = :communityEditorCategory", nativeQuery = true)
    Long countByCommunityEditorCategory(@Param("communityEditorCategory") Long communityEditorCategory);

    List<Community_Editor> findByCommunityEditorTitleContaining(String communityEditorTitle);
}
