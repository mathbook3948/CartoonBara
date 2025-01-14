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
public class Webtoon_Review_CommVO {
    private Long webtoonReviewCommNum;
    private UserVO userNum;
    private Webtoon_ReviewVO webtoonReviewNum;
    private String webtoonReviewNickname;
    private String webtoonReviewCommContent;
    private Date webtoonReviewCommDate;
    private String webtoonReviewCommIp;
    private Long webtoonReviewCommSingoFlag;

}
