package com.jjangtrio.project1_back.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Webtoon_Authors_ListVO {
    
    private Long webtoonAuthorsListNum;
    private String webtoonAuthor;
}
