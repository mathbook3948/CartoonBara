package com.jjangtrio.project1_back.project1_back.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionVO {

    private Long permissionNum;
    private UserVO userNum;
    private Long permissionMaster;
    private Long isPasswordLess;

}
