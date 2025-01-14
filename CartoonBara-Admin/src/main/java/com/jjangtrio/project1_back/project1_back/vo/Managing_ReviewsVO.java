package com.jjangtrio.project1_back.project1_back.vo;

import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Component
@Setter
@Getter
public class Managing_ReviewsVO {

    private int totalPostCount;
    private int totalCommentCount;

    private int webtoonReviewCount;
    private int communityPostCount;
    private int webtoonReviewCommentCount;
    private int communityCommentCount;

    private int dailyPostCount;
    private int weeklyPostCount;
    private int monthlyPostCount;

    private int dailyCommentCount;
    private int weeklyCommentCount;
    private int monthlyCommentCount;

}
