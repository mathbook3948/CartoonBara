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
public class SingoVO {

    private Long singoNum;
    private UserVO user;
    private Date singoDate;
    private Long singoCategory;
    private String primarynumber;
    private String singoCurrentUrl;
}
