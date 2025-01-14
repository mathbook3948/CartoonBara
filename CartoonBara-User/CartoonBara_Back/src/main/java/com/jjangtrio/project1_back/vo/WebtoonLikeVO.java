package com.jjangtrio.project1_back.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebtoonLikeVO {

    private Long webtoonLikeNum;
    private Long webtoonIsLike;
    private Long userNum;
    private WebtoonVO webtoonId;

}
