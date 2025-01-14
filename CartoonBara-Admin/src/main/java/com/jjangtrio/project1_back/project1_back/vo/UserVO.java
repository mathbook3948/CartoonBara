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
public class UserVO {

    private Long userNum;
    private String userName;
    private String userId;
    private String userPwd;
    private String userSecurityQuestion;
    private String userSecurityAnswer;
    private String userImage;
    private Long userLevel;
    private String userEmail;
    private String userPhone;
    private String userBirth;
    private String userNickname;
    private Long userGender;
    private Date userDate;

}
