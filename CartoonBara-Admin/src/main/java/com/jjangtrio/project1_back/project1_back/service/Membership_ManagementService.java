package com.jjangtrio.project1_back.project1_back.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.jjangtrio.project1_back.project1_back.dao.Membership_ManagementDAO;
import com.jjangtrio.project1_back.project1_back.vo.Membership_ManagementVO;

@Service
public class Membership_ManagementService {

    private final Membership_ManagementDAO membershipManagementDAO;

    // 생성자 주입
    public Membership_ManagementService(Membership_ManagementDAO membershipManagementDAO) {
        this.membershipManagementDAO = membershipManagementDAO;
    }

    // 회원 관리 리스트 조회 (페이징 및 검색 포함)
    public List<Membership_ManagementVO> getMembershipManagementList(Map<String, Object> param) {
        // DAO 호출
        List<Membership_ManagementVO> list = membershipManagementDAO.Membership_ManagementList(param);
        
        // DAO 호출 후 로그로 리스트의 값을 확인 (추가된 부분)
        for (Membership_ManagementVO vo : list) {
            System.out.println("회원 승인 여부: " + vo.getApprovalStatus());
        }
        
        return list;
    }

    // 전체 데이터 개수 조회
    public int getTotalCount(Map<String, Object> param) {
        // DAO 호출
        int count = membershipManagementDAO.totalCount(param);
        
        // 로그로 데이터 개수를 확인 (추가된 부분)
        System.out.println("Total Count: " + count);
        
        return count;
    }

    // 특정 승인 상태를 가진 회원 목록 조회
    public List<Membership_ManagementVO> getMembershipManagementListByApprovalStatus(Map<String, Object> param) {
        // DAO 호출
        List<Membership_ManagementVO> list = membershipManagementDAO.Membership_ManagementListByApprovalStatus(param);
        
        // DAO 호출 후 로그로 리스트의 값을 확인
        for (Membership_ManagementVO vo : list) {
            System.out.println("회원 승인 여부: " + vo.getApprovalStatus());
        }
        
        return list;
    }

    // 특정 승인 상태에 해당하는 전체 데이터 개수 조회
    public int getTotalCountByApprovalStatus(Map<String, Object> param) {
        // DAO 호출
        int count = membershipManagementDAO.totalCountByApprovalStatus(param);
        
        // 로그로 데이터 개수를 확인
        System.out.println("Total Count by Approval Status: " + count);
        
        return count;
    }

    // 가입 대기자 조회
    public int getpermissionstate2() {
        return membershipManagementDAO.getPermissionState2Count();
}
}
