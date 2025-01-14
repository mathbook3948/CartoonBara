package com.jjangtrio.project1_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jjangtrio.project1_back.entity.Webtoon;

public interface WebtoonRepository extends JpaRepository<Webtoon, Long> {

    // pvo startIndex~endIndex
    @Query(value = """
                SELECT webtoon_id, webtoon_title, webtoon_desc, webtoon_isend, webtoon_url
                FROM (
                    SELECT
                        ROW_NUMBER() OVER (ORDER BY webtoon_id DESC) AS row_num,
                        webtoon_id,
                        webtoon_title,
                        webtoon_desc,
                        webtoon_isend,
                        webtoon_url
                    FROM webtoon
                ) numbered
                WHERE numbered.row_num BETWEEN :startIndex AND :endIndex
                ORDER BY numbered.row_num
            """, nativeQuery = true)
    List<Object[]> findWebtoonsWithinRange(@Param("startIndex") int startIndex, @Param("endIndex") int endIndex);

    @Query(value = """
            SELECT
                numbered.webtoon_id,
                numbered.webtoon_title,
                numbered.webtoon_desc,
                numbered.webtoon_isend,
                numbered.webtoon_url
            FROM (
                SELECT
                    ROW_NUMBER() OVER (ORDER BY w.webtoon_id DESC) AS row_num,
                    w.webtoon_id webtoon_id,
                    w.webtoon_title webtoon_title,
                    w.webtoon_desc webtoon_desc,
                    w.webtoon_isend webtoon_isend,
                    w.webtoon_url webtoon_url
                FROM webtoon w
                INNER JOIN webtoon_tag tag ON w.webtoon_id = tag.webtoon_id
                AND tag.webtoon_tag_list_num = :category
            ) numbered
            WHERE numbered.row_num BETWEEN :startIndex AND :endIndex
            ORDER BY numbered.row_num
                        """, nativeQuery = true)
    List<Object[]> findWebtoonsWithinRangeWithCategory(@Param("startIndex") int startIndex,
            @Param("endIndex") int endIndex, @Param("category") Long category);

    List<Webtoon> findByWebtoonId(int webtoonId);

    @Query(value = "SELECT COUNT(b.webtoon_id) FROM webtoon a, webtoon_tag b WHERE a.webtoon_id = b.webtoon_id AND b.webtoon_tag_list_num = :category", nativeQuery = true)
    Object countByCategory(@Param("category") Long category);

}
