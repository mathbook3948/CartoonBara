package com.jjangtrio.project1_back.project1_back.dao;

import com.jjangtrio.project1_back.project1_back.vo.Member_ChartVO;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Member_ChartDAO {
    // 연령대와 성별별 비율을 조회
    List<Member_ChartVO> getAgeGenderStatistics();

    
}
