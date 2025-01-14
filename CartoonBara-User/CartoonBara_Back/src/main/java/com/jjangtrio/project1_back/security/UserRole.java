package com.jjangtrio.project1_back.security;

public enum UserRole {
    COMMON, NEW, BAN, DELETED;

    public static String getRole(Long role) {
        if(role == 1) {
            return "ROLE_"+UserRole.COMMON.name();
        } else if(role == 2) {
            return "ROLE_"+UserRole.NEW.name();
        } else if(role == 3) {
            return "ROLE_"+UserRole.BAN.name();
        } else if(role == 4) {
            return "ROLE_"+UserRole.DELETED.name();
        } else {
            return "ROLE_"+UserRole.COMMON.name();
        }
    }
}
