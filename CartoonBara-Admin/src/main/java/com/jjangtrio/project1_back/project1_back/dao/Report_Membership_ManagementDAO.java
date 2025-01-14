package com.jjangtrio.project1_back.project1_back.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.jjangtrio.project1_back.project1_back.vo.Report_Membership_ManagementVO;

@Mapper
public interface Report_Membership_ManagementDAO {

    
    // 신고 회원 관리 리스트 조회 (페이징, 검색 포함)
    List<Report_Membership_ManagementVO> report_Membership_ManagementList(Map<String, Object> param);

    // 전체 데이터 개수 조회
    int totalCount(Map<String, Object> map);

    // 활동 정지 및 탈퇴
    int updateUserStatus(Map<String, Object> params);

    // 신고 처리 (신고 철회)
    int revokeReport(@Param("userNum") int userNum);
    
}
