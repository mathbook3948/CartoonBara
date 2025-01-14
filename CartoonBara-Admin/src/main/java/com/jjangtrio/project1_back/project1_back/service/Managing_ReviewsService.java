package com.jjangtrio.project1_back.project1_back.service;

import org.springframework.stereotype.Service;
import com.jjangtrio.project1_back.project1_back.dao.Managing_ReviewsDAO;
import com.jjangtrio.project1_back.project1_back.vo.Managing_ReviewsVO;

@Service
public class Managing_ReviewsService {

    private final Managing_ReviewsDAO managingReviewsDAO;

    public Managing_ReviewsService(Managing_ReviewsDAO managingReviewsDAO) {
        this.managingReviewsDAO = managingReviewsDAO;
    }

    public Managing_ReviewsVO getTotalCounts() {
        return managingReviewsDAO.getTotalCounts();
    }

    public Managing_ReviewsVO getDailyCounts() {
        return managingReviewsDAO.getDailyCounts();
    }

    public Managing_ReviewsVO getWeeklyCounts() {
        return managingReviewsDAO.getWeeklyCounts();
    }

    public Managing_ReviewsVO getMonthlyCounts() {
        return managingReviewsDAO.getMonthlyCounts();
    }
}
