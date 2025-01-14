package com.pwl.mapper.Login;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.pwl.domain.Login.UserInfo;

@Mapper
public interface LoginMapper {

    // 로그인 체크
    UserInfo checkPassword(UserInfo userinfo);
    
    
    // 패스워드 업데이트
    void updatePassword(UserInfo userinfo);
    
    // 비밀번호 변경
    void changepw(UserInfo userinfo);
    
    UserInfo getUserById(UserInfo userInfo);
    
    void updatePermission(Map<String, Object> map);
    
    int getPermission(UserInfo userInfo);
    
    Map<String, Object> getUserDetail(UserInfo userInfo);
    
}
