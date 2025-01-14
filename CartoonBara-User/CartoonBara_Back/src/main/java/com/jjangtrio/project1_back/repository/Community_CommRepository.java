package com.jjangtrio.project1_back.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jjangtrio.project1_back.entity.Community_Comm;

import jakarta.transaction.Transactional;

@Repository
public interface Community_CommRepository extends JpaRepository<Community_Comm, Long> {

    List<Community_Comm> findByCommunityCommunityNum(Long communityNum);

    List<Community_Comm> findByUserUserNum(Long userNum);

    // 최신 댓글을 내림차순으로 조회
    List<Community_Comm> findAllByOrderByCommunityCommNumDesc();

    // 특정 커뮤니티의 댓글을 조회
    List<Community_Comm> findAllByCommunityCommunityNum(Long communityNum);

    // 특정 유저의 댓글을 조회
    List<Community_Comm> findAllByUserUserNum(Long userNum);

    // 댓글 내용에서 특정 단어가 포함된 댓글 조회
    List<Community_Comm> findAllByCommunityCommContentContaining(String content);

    // 특정 커뮤니티와 유저의 댓글 삭제
    void deleteByCommunityCommunityNumAndUserUserNum(Long communityNum, Long userNum);

    // User의 nickname을 함께 조회하는 JPQL 쿼리
    @Query("SELECT c FROM Community_Comm c JOIN c.user u WHERE c.community.communityNum = :communityNum")
    List<Community_Comm> findCommentsByCommunityWithNickname(@Param("communityNum") Long communityNum);

    // 특정 커뮤니티의 댓글 갯수 조회
    @Query("SELECT COUNT(c) FROM Community_Comm c WHERE c.community.communityNum = :communityNum")
    Long countCommentsByCommunityNum(@Param("communityNum") Long communityNum);

        //======================================================================================================================
         // 김채린이 추가함 오류나면 불러

        // 신고 당한 글인지 판별
        @Query(value = "SELECT 1 FROM community_comm  WHERE community_comm_num = :communityCommNum AND community_comm_singo_flag = 1", nativeQuery = true)
        Long IsSingoFlagActive(@Param("communityCommNum") Long communityCommNum);

        // 신고 당한 글로 등록
        @Modifying
        @Transactional
        @Query(value = "UPDATE community_comm SET community_comm_singo_flag = 1 WHERE community_comm_num = :communityCommNum", nativeQuery = true)
        Integer updateSingoFlag(@Param("communityCommNum") Long communityCommNum);


        //=======================================================================================================================
}   
