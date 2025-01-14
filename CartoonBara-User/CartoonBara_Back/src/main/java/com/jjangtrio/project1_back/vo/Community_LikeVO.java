package com.jjangtrio.project1_back.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Community_LikeVO {

    private Long communityLikeNum;
    private Long communityNum;
    private Long communityLikeIslike;
    private UserVO userNum;
}
