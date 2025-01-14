package com.jjangtrio.project1_back.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jjangtrio.project1_back.entity.Webtoon;
import com.jjangtrio.project1_back.entity.Webtoon_Authors_List;
import com.jjangtrio.project1_back.entity.Webtoon_Search;
import com.jjangtrio.project1_back.repository.WebtoonRepository;
import com.jjangtrio.project1_back.repository.Webtoon_Authors_ListRepository;
import com.jjangtrio.project1_back.repository.Webtoon_SearchRepository;

@Service
public class WebtoonSearchService {

    @Autowired
    private WebtoonRepository webtoonRepository;

    @Autowired
    private Webtoon_Authors_ListRepository webtoon_Authors_ListRepository;

    @Autowired
    private Webtoon_SearchRepository webtoon_SearchRepository;

    public List<Webtoon> getAllWebtoons() {

        return webtoonRepository.findAll();
    }

    public List<Webtoon_Authors_List> getAllAuthors() {

        return webtoon_Authors_ListRepository.findAll();
    }

    public List<Webtoon_Search> getAllWebtoon_Searchs() {

        return webtoon_SearchRepository.findAll();
    }
}
