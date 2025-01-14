package com.jjangtrio.project1_back.project1_back.service;

import com.jjangtrio.project1_back.project1_back.dao.Member_ChartDAO;
import com.jjangtrio.project1_back.project1_back.vo.Member_ChartVO;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class Member_ChartService {
    private final Member_ChartDAO memberChartDAO;

    // 생성자 주입
    public Member_ChartService(Member_ChartDAO memberChartDAO) {
        this.memberChartDAO = memberChartDAO;
    }

    // 연령대와 성별 통계 데이터를 가져오는 메서드
    public List<Member_ChartVO> getAgeGenderStatistics() {
        // DAO에서 데이터를 가져옵니다.
        List<Member_ChartVO> statistics = memberChartDAO.getAgeGenderStatistics();
        return statistics != null ? statistics : List.of(); // Null일 경우 빈 리스트 반환
    }

    
}
