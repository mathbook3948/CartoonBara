package com.jjangtrio.project1_back.project1_back.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjangtrio.project1_back.project1_back.service.Report_Management_ListService;
import com.jjangtrio.project1_back.project1_back.vo.Report_Management_List;

@RestController
@RequestMapping("/report")
public class Report_Management_ListController {

    @Autowired
    private Report_Management_ListService reportManagementListService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, List<Report_Management_List>>> getReportList(@RequestParam Long userNum) {
        Map<String, List<Report_Management_List>> response = new HashMap<>();

        // 웹툰 리뷰 게시글
        List<Report_Management_List> webtoonReviews = reportManagementListService.getWReportList(userNum);
        response.put("webtoonReviews", webtoonReviews);

        // 웹툰 리뷰 댓글
        List<Report_Management_List> webtoonReviewComments = reportManagementListService.getWCReportList(userNum);
        response.put("webtoonReviewComments", webtoonReviewComments);

        // 커뮤니티 게시글
        List<Report_Management_List> communityPosts = reportManagementListService.getCReportList(userNum);
        response.put("communityPosts", communityPosts);

        // 커뮤니티 댓글
        List<Report_Management_List> communityComments = reportManagementListService.getCCReportList(userNum);
        response.put("communityComments", communityComments);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/update")
    public ResponseEntity<String> updateReportStatus(@RequestParam Long singoCategory,
            @RequestParam Long primaryNumber) {
        Map<String, Long> params = new HashMap<>();
        params.put("singoCategory", singoCategory); // 카테고리 값
        params.put("primarynumber", primaryNumber); // PK 값
        reportManagementListService.updateSingoFlag(params);
        return ResponseEntity.ok("Report status updated successfully.");
    }

}
