package com.jjangtrio.project1_back.security;

public enum PermissionRole {
    ADMIN, USER, EDITOR;

    //admin(관리자): 127 , editor(작가): 126 , user(나머지): 1(0~125)
    public static String getRole(Long role) {
        if (role == 127) {
            return "ROLE_" + PermissionRole.ADMIN.name();
        } else if (role == 63) {
            return "ROLE_" + PermissionRole.EDITOR.name();
        } else {
            return "ROLE_" + PermissionRole.USER.name();
        }
    }
}
