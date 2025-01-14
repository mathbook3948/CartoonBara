package com.jjangtrio.project1_back.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.entity.Webtoon;
import com.jjangtrio.project1_back.entity.Webtoon_Like;

public interface Webtoon_LikeRepository extends JpaRepository<Webtoon_Like, Long> {

    @Query("SELECT COUNT(w) FROM Webtoon_Like w WHERE w.webtoon = :webtoon AND w.webtoonIsLike = 1")
    Long getLike(Webtoon webtoon);

    @Query("SELECT COUNT(w) FROM Webtoon_Like w WHERE w.webtoon = :webtoon AND w.webtoonIsLike = 0")
    Long getDislike(Webtoon webtoon);

    @Query("""
    SELECT EXISTS(
        SELECT 1
        FROM Webtoon_Like wl
        WHERE wl.webtoon = :webtoon
        AND wl.user = :user
        AND wl.webtoonIsLike = :isLike
    )
""")
    boolean existsByWebtoonIdAndUserIdAndWebtoonIsLike(Webtoon webtoon, User user, @Param("isLike") Long isLike);

    Webtoon_Like findByUser(User user);

    // Find Webtoon_Like by Webtoon and User
    Webtoon_Like findByWebtoonAndUser(Webtoon webtoon, User user);

    // Webtoon(webtoon_id)와 User(user_num)(1 = Like, 0 = Dislike)
    @Query("SELECT w FROM Webtoon_Like w WHERE w.webtoon = :webtoon AND w.user = :user")
    List<Webtoon_Like> findByWebtoonAndUserAndWebtoonIsLike(Webtoon webtoon, User user);

    @Query(value = """
            WITH like_counts AS (
    SELECT 
        b.webtoon_id AS webtoon_id,
        b.webtoon_url AS webtoon_url,
        COUNT(*) AS like_count
    FROM 
        webtoon_Like a
    JOIN 
        webtoon b
    ON 
        a.webtoon_id = b.webtoon_id
    WHERE 
        a.webtoon_Islike = 1
    GROUP BY 
        b.webtoon_id, b.webtoon_url
),
ranked_webtoons AS (
    SELECT 
        webtoon_id,
        webtoon_url,
        like_count,
        ROW_NUMBER() OVER (ORDER BY like_count DESC, webtoon_id) AS rank
    FROM 
        like_counts
)
SELECT 
    webtoon_id,
    webtoon_url,
    like_count
FROM 
    ranked_webtoons
WHERE 
    rank <= 5
            """, nativeQuery = true)
    List<Object[]> findTop5ByOrderByViewCountDesc();
}
