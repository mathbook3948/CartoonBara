package com.jjangtrio.project1_back.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jjangtrio.project1_back.entity.Community_Editor_Comm;

public interface Community_Editor_CommRepository extends JpaRepository<Community_Editor_Comm, Long> {

    // List<Community_Editor_Comm> findAllByOrderByCommunityEditorNumDesc();

    // 특정 Community_Editor와 관련된 Editor를 삭제.
    void deleteByCommunityEditorCommunityEditorNumAndCommunityEditorUserUserNum(Long communityEditorNum,
            Long editorNum);

    // 특정 에디터(Editor)가 작성한 댓글을 조회.
    List<Community_Editor_Comm> findByCommunityEditorUserUserNum(Long userNum);

    // 전체 댓글을 최신 댓글 순으로 조회.
    List<Community_Editor_Comm> findAllByOrderByCommunityEditorCommNumDesc();

    // 특정 Community_Editor와 관련된 댓글을 조회.
    List<Community_Editor_Comm> findByCommunityEditorCommunityEditorNumOrderByCommunityEditorCommNum(
            Long communityEditorNum);

    // 특정 댓글 번호와 에디터 번호를 기반으로 댓글 삭제.
    void deleteByCommunityEditorCommNumAndCommunityEditorUserUserNum(Long communityEditorCommNum, Long userNum);

    // 특정 커뮤니티의 댓글 갯수 조회
    @Query("SELECT COUNT(e) FROM Community_Editor_Comm e WHERE e.communityEditor.communityEditorNum = :communityEditorNum")
    Long countCommentsByCommunityEditorNum(@Param("communityEditorNum") Long communityEditorNum);

}



