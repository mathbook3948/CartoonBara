package com.jjangtrio.project1_back.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Community_Comm_LikeVO {

    private Long communityCommLikeNum;
    private Community_CommVO communityCommNum;
    private Long communityCommLikeIslike;
    private UserVO userNum;
}
