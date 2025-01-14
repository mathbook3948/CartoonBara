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
public class NoticeVO {

    private Long noticeNum;
    private PermissionVO permissionNum;
    private String noticeTitle;
    private String noticeContent;
    private String noticeImage;
    private Date noticeDate;
}
