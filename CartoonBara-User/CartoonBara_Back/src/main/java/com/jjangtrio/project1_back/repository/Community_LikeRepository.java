package com.jjangtrio.project1_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jjangtrio.project1_back.entity.Community;
import com.jjangtrio.project1_back.entity.Community_Like;
import com.jjangtrio.project1_back.entity.User;

import io.lettuce.core.dynamic.annotation.Param;

public interface Community_LikeRepository extends JpaRepository<Community_Like, Long> {

    @Query("SELECT COUNT(c) FROM Community_Like c WHERE c.community = :community AND c.communityLikeIslike = 1")
    Long countByCommunityAndCommunityLikeIslike(Community community);

    @Query("SELECT COUNT(c) FROM Community_Like c WHERE c.community = :community AND c.communityLikeIslike = 0")
    Long countByCommunityAndCommunityLikeIsNotLike(Community community);

    @Query("SELECT COUNT(c) FROM Community_Like c WHERE c.community = :community AND c.communityLikeIslike = 1")
    Long countByCommunityAndCommunityLikeIslikesss(Community community);

    @Query("""
            SELECT EXISTS(
                SELECT 1
                FROM Community_Like cl
                WHERE cl.user = :user AND cl.community = :community
                AND cl.communityLikeIslike = :isLike

            )
            """)
    boolean existsByUserAndCommunityAndCommunityIsLike(User user, Community community, @Param("isLike") Long isLike);

    Community_Like findByUserAndCommunity(User user, Community community);

    // 좋아요 상태 확인
    @Query("SELECT cl FROM Community_Like cl WHERE cl.user = :user AND cl.community = :community AND cl.communityLikeIslike = :isLike")
    Community_Like findByUserAndCommunityAndCommunityIsLike(User user, Community community,
            @Param("isLike") Long isLike);

}
