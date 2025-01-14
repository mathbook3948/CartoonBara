package com.jjangtrio.project1_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jjangtrio.project1_back.entity.Webtoon;
import com.jjangtrio.project1_back.entity.Webtoon_Search;

public interface Webtoon_SearchRepository extends JpaRepository<Webtoon_Search, Long> {

    boolean existsByWebtoonId(Webtoon webtoonId);

    @Query(value = "SELECT webtoon_title FROM webtoon WHERE webtoon_id = :webtoonId", nativeQuery = true)
    String findWebtoonTitleByWebtoon(Long webtoonId);
}
