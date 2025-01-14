package com.jjangtrio.project1_back.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Webtoon_TagVO {

    private Long webtoonTagNum;
    private WebtoonVO webtoonId;
    private Webtoon_Tag_ListVO webtoonTagListNum;
}
