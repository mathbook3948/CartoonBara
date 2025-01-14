package com.jjangtrio.project1_back.project1_back.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jjangtrio.project1_back.project1_back.service.Managing_ReviewsService;
import com.jjangtrio.project1_back.project1_back.vo.Managing_ReviewsVO;

@RestController
@RequestMapping("/reviews")
public class Managing_ReviewsController {

    private final Managing_ReviewsService managingReviewsService;

    public Managing_ReviewsController(Managing_ReviewsService managingReviewsService) {
        this.managingReviewsService = managingReviewsService;
    }

    @GetMapping("/total")
    public Managing_ReviewsVO getTotalCounts() {
        return managingReviewsService.getTotalCounts();
    }

    @GetMapping("/daily")
    public Managing_ReviewsVO getDailyCounts() {
        return managingReviewsService.getDailyCounts();
    }

    @GetMapping("/weekly")
    public Managing_ReviewsVO getWeeklyCounts() {
        return managingReviewsService.getWeeklyCounts();
    }

    @GetMapping("/monthly")
    public Managing_ReviewsVO getMonthlyCounts() {
        return managingReviewsService.getMonthlyCounts();
    }
}