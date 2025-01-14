package com.jjangtrio.project1_back.vo;

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
    private WebtoonVO webtoon;
    private UserVO user;
    private String userNickname;
    private String webtoonReviewReview;
    private Date webtoonReviewDate;
    private String webtoonReviewIp;
    public Object getWebtoon;
    private Long webtoonReviewSingoFlag;

}
