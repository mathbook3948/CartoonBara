package com.jjangtrio.project1_back.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User_InterestVO {
    private Long userInterestNum;
    private UserVO userNum;
    private Webtoon_Tag_ListVO userWebtoonTagList;
}
