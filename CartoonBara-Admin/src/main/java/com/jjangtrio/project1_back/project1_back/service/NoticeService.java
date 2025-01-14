package com.jjangtrio.project1_back.project1_back.service;

import com.jjangtrio.project1_back.project1_back.dao.NoticeDAO;
import com.jjangtrio.project1_back.project1_back.vo.NoticeVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class NoticeService {

    @Autowired
    private NoticeDAO noticeDAO;
        //공지사항 추가
    public void insertNotice(NoticeVO noticeVO) {
         noticeDAO.insertNotice(noticeVO); 
        }

        public int totalCount(Map<String, String> map) {
            return noticeDAO.totalCount(map);
        }

        //공지사항 목록
    	public List<NoticeVO> NoticeList(Map<String, String> map) {
            return noticeDAO.NoticeList(map);
        }

        // //공지사항 리스트
        // public List<NoticeVO> list() {
        //     return noticeDAO.list();
        // }

        //공지사항 디테일 
        public NoticeVO noticedetail(Long noticenum){
            //noticeDAO.hit(noticenum);
            return noticeDAO.Noticedetail(noticenum);
        }

        // 공지사항 삭제
        public void delete(Long noticenum) {
            noticeDAO.delete(noticenum);
        }
}
