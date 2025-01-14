package com.jjangtrio.project1_back.project1_back.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jjangtrio.project1_back.project1_back.service.Report_MemberService;

@RestController
@RequestMapping("/reportmember")
public class Report_MemberController {

    private final Report_MemberService reportMemberService;

    public Report_MemberController(Report_MemberService reportMemberService) {
        this.reportMemberService = reportMemberService;
    }

    // 활동 정지: 사용자 상태를 4번 (활동 정지)로 변경
    @PostMapping("/suspend/{userNum}")
    public String suspendUser(@PathVariable("userNum") Long userNum) {
        boolean result = reportMemberService.suspendUser(userNum);
        return result ? "활동 정지 성공" : "활동 정지 실패";
    }

    // 활동 재개: 사용자 상태를 1번 (일반 사용자)로 변경
    @PostMapping("/resume/{userNum}")
    public String resumeUser(@PathVariable("userNum") Long userNum) {
        boolean result = reportMemberService.resumeUser(userNum);
        return result ? "활동 재개 성공" : "활동 재개 실패";
    }

    // 탈퇴시키기: 어떤 상태든지 5번 (탈퇴)로 변경
    @PostMapping("/withdraw/{userNum}")
    public String withdrawUser(@PathVariable("userNum") Long userNum) {
        boolean result = reportMemberService.withdrawUser(userNum);
        return result ? "탈퇴 성공" : "탈퇴 실패";
    }

    // 탈퇴 취소: 탈퇴된 사용자 상태를 다시 1번 (일반 사용자)로 변경
    @PostMapping("/restore/{userNum}")
    public String restoreUser(@PathVariable("userNum") Long userNum) {
        boolean result = reportMemberService.restoreUser(userNum);
        return result ? "탈퇴 취소 성공" : "탈퇴 취소 실패";
    }

    // 가입 승인: 사용자 상태를 1번으로 변경
    @PostMapping("/approve/{userNum}")
    public String approveUser(@PathVariable("userNum") Long userNum) {
        boolean result = reportMemberService.approveUser(userNum);
        return result ? "가입 승인 성공" : "가입 승인 실패";
    }

}
