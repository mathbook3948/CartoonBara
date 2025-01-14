package com.jjangtrio.project1_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jjangtrio.project1_back.entity.Webtoon;
import com.jjangtrio.project1_back.entity.Webtoon_Authors;

public interface Webtoon_AuthorsRepository extends JpaRepository<Webtoon_Authors, Long> {

    List<Webtoon_Authors> findByWebtoon(Webtoon webtoon);

    @Query(value = """
            SELECT
            b.webtoon_id webtoon_id,
            b.webtoon_url webtoon_url
            FROM webtoon_authors a, webtoon b
            WHERE a.webtoon_authors_list_num = :authorNum AND
            a.webtoon_id = b.webtoon_id
            """,nativeQuery = true)
    List<Object[]> getAuthorDetail(@Param("authorNum") Long authorNum);
}
