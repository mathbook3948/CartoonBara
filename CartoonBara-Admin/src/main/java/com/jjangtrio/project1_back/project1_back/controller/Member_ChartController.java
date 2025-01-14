package com.jjangtrio.project1_back.project1_back.controller;

import com.jjangtrio.project1_back.project1_back.service.Member_ChartService;
import com.jjangtrio.project1_back.project1_back.vo.Member_ChartVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/memberchart")
public class Member_ChartController {
    private final Member_ChartService memberChartService;

    // 생성자 주입
    public Member_ChartController(Member_ChartService memberChartService) {
        this.memberChartService = memberChartService;
    }

    // 연령대, 성별 통계 데이터 및 총 사용자 수, 오늘 가입 사용자 수를 반환
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> response = new HashMap<>();

        // 서비스에서 데이터를 가져옵니다.
        List<Member_ChartVO> statistics = memberChartService.getAgeGenderStatistics();

        if (statistics.isEmpty()) {
            return ResponseEntity.noContent().build(); // 데이터가 없으면 204 반환
        }

        // 총 사용자 수와 오늘 가입 사용자 수를 계산
        int totalUsers = statistics.isEmpty() ? 0 : statistics.get(0).getTotalUsers();
        int todaysSignups = statistics.isEmpty() ? 0 : statistics.get(0).getTodaysSignups();

        response.put("totalUsers", totalUsers);
        response.put("todaysSignups", todaysSignups);
        response.put("statistics", statistics); // 연령대 및 성별 데이터 포함

        return ResponseEntity.ok(response);
    }
}