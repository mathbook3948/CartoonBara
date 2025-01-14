package com.jjangtrio.project1_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jjangtrio.project1_back.entity.Webtoon;
import com.jjangtrio.project1_back.entity.Webtoon_Tag;
import com.jjangtrio.project1_back.entity.Webtoon_Tag_List;

public interface Webtoon_TagRepository extends JpaRepository<Webtoon_Tag, Long> {
    List<Webtoon_Tag> findByWebtoon(Webtoon webtoon);

    @Query("SELECT wt FROM Webtoon_Tag wt WHERE wt.webtoonTagListNum = :webtoonTagListNum AND wt.webtoon = :webtoon")
    List<Webtoon_Tag> findTags(@Param("webtoonTagListNum") Webtoon_Tag_List webtoonTagListNum, @Param("webtoon") Webtoon webtoon);
}
