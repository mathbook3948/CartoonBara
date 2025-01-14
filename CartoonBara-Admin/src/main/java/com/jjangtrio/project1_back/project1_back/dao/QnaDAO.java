package com.jjangtrio.project1_back.project1_back.dao;

import org.apache.ibatis.annotations.Mapper;

import com.jjangtrio.project1_back.project1_back.vo.QnaVO;

@Mapper
public interface QnaDAO {
    void qnaAnswer(QnaVO qnaVO);
    QnaVO qnaDetail(Long qnaNum);
}
