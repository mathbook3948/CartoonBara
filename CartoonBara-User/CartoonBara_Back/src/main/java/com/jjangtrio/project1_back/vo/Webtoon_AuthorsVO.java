package com.jjangtrio.project1_back.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Webtoon_AuthorsVO {

    private Long webtoonAuthorsNum;
    private WebtoonVO webtoonId;
    private Webtoon_Authors_ListVO webtoonAuthorsListNum;
}
