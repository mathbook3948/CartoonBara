package com.pwl.jwt;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.pwl.domain.Login.UserInfo;
import com.pwl.mapper.Login.LoginMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	
	@Autowired
	private LoginMapper loginMapper;
    
	private static final SecretKey key
    = Keys.hmacShaKeyFor // 바이트 배열을 사용하여 HMAC 방식으로 비밀 키를 생성한다.
    (
        Decoders.BASE64.decode // 바이트 배열로 변환하여 반환한다
        (
            "jwtpassword123jwtpassword123jwtpassword123jwtpassword123jwtpasswordjwtpassword123jwtpassword123jwtpassword123jwtpassword123jwtpasswordjwtpassword123jwtpassword123jwtpassword123jwtpassword123jwtpassword"
        )
    );

    
    public String createToken(UserInfo userinfo) {
    	
    	Map<String, Object> map = loginMapper.getUserDetail(userinfo);

        List<String> authorties = new ArrayList<>();

            authorties.add(PermissionRole.getRole(Long.valueOf(map.get("PERMISSIONMASTER").toString())));
            authorties.add(UserRole.getRole(Long.valueOf(map.get("PERMISSIONSTATE").toString())));

        String token = Jwts.builder()
            .claim("userNum", map.get("USERNUM"))
            .claim("authority", authorties)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 1)) // 1시간 유효
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();

            return token;
    } 
}
