package com.jjangtrio.project1_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jjangtrio.project1_back.entity.Webtoon_Authors_List;

public interface Webtoon_Authors_ListRepository extends JpaRepository<Webtoon_Authors_List, Long> {

    // author exits?? by string
    @Query("SELECT COUNT(w) > 0 FROM Webtoon_Authors_List w WHERE w.webtoonAuthor = :webtoonAuthor")
    boolean existsByWebtoonAuthor(@Param("webtoonAuthor") String webtoonAuthor);

    @Query("SELECT w FROM Webtoon_Authors_List w WHERE w.webtoonAuthor = :webtoonAuthor")
    Webtoon_Authors_List findByWebtoonAuthor(@Param("webtoonAuthor") String webtoonAuthor);
}
