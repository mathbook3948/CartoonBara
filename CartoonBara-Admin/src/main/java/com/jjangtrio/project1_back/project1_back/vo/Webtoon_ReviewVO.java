package com.jjangtrio.project1_back.project1_back.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Webtoon_ReviewVO {
    private Long webtoonReviewNum;
    private WebtoonVO webtoonId;
    private UserVO userNum;
    private String webtoonReviewReview;
    private Date webtoonReviewDate;
    private String webtoonReviewIp;
}
