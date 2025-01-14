package com.jjangtrio.project1_back.project1_back.service;

import org.springframework.stereotype.Service;

import com.jjangtrio.project1_back.project1_back.dao.Report_MemberDAO;

@Service
public class Report_MemberService {

    private final Report_MemberDAO reportMemberDAO;

    public Report_MemberService(Report_MemberDAO reportMemberDAO) {
        this.reportMemberDAO = reportMemberDAO;
    }

    // 활동 정지: 사용자 상태를 5번 (활동 정지)로 변경
    public boolean suspendUser(Long userNum) {
        int result = reportMemberDAO.suspendUser(userNum);
        return result > 0; // 업데이트된 행이 1 이상이면 성공으로 간주
    }

    // 활동 재개: 사용자 상태를 1번 (일반 사용자)로 변경
    public boolean resumeUser(Long userNum) {
        int result = reportMemberDAO.resumeUser(userNum);
        return result > 0; // 업데이트된 행이 1 이상이면 성공으로 간주
    }

    // 탈퇴시키기: 어떤 상태든지 4번 (탈퇴)로 변경
    public boolean withdrawUser(Long userNum) {
        int result = reportMemberDAO.withdrawUser(userNum);
        return result > 0; // 업데이트된 행이 1 이상이면 성공으로 간주
    }

    // 탈퇴 취소: 탈퇴 상태 (4번)를 다시 1번 (일반 사용자)로 변경
    public boolean restoreUser(Long userNum) {
        int result = reportMemberDAO.restoreUser(userNum);
        return result > 0; // 업데이트된 행이 1 이상이면 성공으로 간주
    }

    // 가입 승인: 사용자 상태를 1번으로 변경
    public boolean approveUser(Long userNum) {
        int result = reportMemberDAO.approveUser(userNum);
        return result > 0; // 업데이트된 행이 1 이상이면 성공으로 간주
    }
}
