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
public class CommunityVO {

    private Long communityNum;
    private UserVO userNum;
    private String communityCategory;
    private String communityTitle;
    private Date communityDate;
    private Long communityHit;
    private String communityImage;
    private String communityContent;
    private String communityIp;
    
}
