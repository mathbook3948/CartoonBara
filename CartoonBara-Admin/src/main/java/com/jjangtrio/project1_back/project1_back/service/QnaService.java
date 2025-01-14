package com.jjangtrio.project1_back.project1_back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jjangtrio.project1_back.project1_back.dao.QnaDAO;
import com.jjangtrio.project1_back.project1_back.vo.QnaVO;

@Service
public class QnaService {

    @Autowired
    private QnaDAO qnaDAO;

    public QnaVO qnaDetail(Long qnaNum) {
        return qnaDAO.qnaDetail(qnaNum);
    }
    public void qnaAnswer(QnaVO vo) {
        qnaDAO.qnaAnswer(vo);
    }
}
