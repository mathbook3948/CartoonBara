package com.jjangtrio.project1_back.project1_back.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebtoonVO {

    private Integer webtoonId;
    private String webtoonTitle;
    private String webtoonDesc;
    private Integer webtoonIsend;
    private String webtoonUrl;

}
