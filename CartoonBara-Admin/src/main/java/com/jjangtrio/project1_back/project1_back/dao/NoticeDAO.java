package com.jjangtrio.project1_back.project1_back.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jjangtrio.project1_back.project1_back.vo.NoticeVO;

@Mapper
public interface NoticeDAO {

    // 공지사항 추가
    void insertNotice(NoticeVO noticeVO); // INSERT 매핑

    // 공지사항 목록 조회
    List<NoticeVO> NoticeList(Map<String, String> map); // SELECT 매핑

    int totalCount(Map<String, String> map);

    // 공지사항 디테일 조회
    NoticeVO Noticedetail(Long noticenum);

    // // 공지사항 리스트
    // List<NoticeVO> list();

    // 공지사항 삭제
    void delete(Long noticenum);
}