package com.jjangtrio.project1_back.project1_back.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.jjangtrio.project1_back.project1_back.dao.Report_Membership_ManagementDAO;
import com.jjangtrio.project1_back.project1_back.vo.Report_Membership_ManagementVO;

@Service
public class Report_Membership_ManagementService {

    private final Report_Membership_ManagementDAO reportMembershipManagementDAO;

    // 생성자 주입
    public Report_Membership_ManagementService(Report_Membership_ManagementDAO reportMembershipManagementDAO) {
        this.reportMembershipManagementDAO = reportMembershipManagementDAO;
    }

    // 신고 회원 관리 리스트 조회 (페이징 및 검색 포함.)
    public List<Report_Membership_ManagementVO> reportMembershipManagementList(Map<String, Object> param) {
      
            // DAO 호출
            List<Report_Membership_ManagementVO> list = reportMembershipManagementDAO
                    .report_Membership_ManagementList(param);

            // DAO 호출 후 로그로 리스트의 값을 확인 (추가된 부분)
            for (Report_Membership_ManagementVO vo : list) {
                System.out.println("회원 승인 여부: " + vo.getApprovalStatus());
            }
            return list;
    }

    // 전체 데이터 개수 조회
    public int getTotalCount(Map<String, Object> param) {
            // DAO 호출
            int count = reportMembershipManagementDAO.totalCount(param);

            // 로그로 데이터 개수를 확인 (추가된 부분)
            System.out.println("Total Count: " + count);

            return count;
    }

      // 상태 변경 (활동 정지, 탈퇴)
    public boolean updateUserStatus(int userNum, int permissionState) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("userNum", userNum);
            params.put("permissionState", permissionState);

            int result = reportMembershipManagementDAO.updateUserStatus(params);
            return result > 0;
        } catch (Exception e) {
            System.out.println("사용자 상태 업데이트 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("사용자 상태 업데이트 중 오류 발생.", e);
        }
    }

    // 신고 철회
    public boolean revokeReport(int userNum) {
        return reportMembershipManagementDAO.revokeReport(userNum) > 0;
    }
    }

