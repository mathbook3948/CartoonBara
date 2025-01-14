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
public class Community_CommVO {
    private Long communityCommNum;
    private UserVO user;
    private CommunityVO community;
    private String communityCommContent;
    private Date communityCommDate;
    private String communityCommIp;
    private Long communityCommSingoFlag;

    // public Long getUserNum() {
    // return user != null ? user.getUserNum() : null;
    // }

    // public void setUserNum(Long userNum) {
    // if (this.user == null) {
    // this.user = new UserVO();
    // }
    // this.user.setUserNum(userNum);
    // }

    // public Long getCommunityNum() {
    // return community != null ? community.getCommunityNum() : null;
    // }

    // public void setCommunityNum(Long communityNum) {
    // if (this.community == null) {
    // this.community = new CommunityVO();
    // }
    // this.community.setCommunityNum(communityNum);
    // }
}
