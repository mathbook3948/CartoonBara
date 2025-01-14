package com.jjangtrio.project1_back.security;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    
    private static final SecretKey key
            = Keys.hmacShaKeyFor // 바이트 배열을 사용하여 HMAC 방식으로 비밀 키를 생성한다.
            (
                Decoders.BASE64.decode // 바이트 배열로 변환하여 반환한다
                (
                    "jwtpassword123jwtpassword123jwtpassword123jwtpassword123jwtpasswordjwtpassword123jwtpassword123jwtpassword123jwtpassword123jwtpasswordjwtpassword123jwtpassword123jwtpassword123jwtpassword123jwtpassword"
                )
            );

    
    public String createToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        UserForLogin user = null;
        if(auth.getPrincipal() instanceof UserForLogin) {
            user = (UserForLogin) auth.getPrincipal();
        } else {
            throw new RuntimeException("Principal is not UserForLogin");
        }


        List<String> authorties = new ArrayList<>();

        for(GrantedAuthority authority : auth.getAuthorities()) {
            authorties.add(authority.getAuthority());
        }

        String token = Jwts.builder()
            .claim("userNum", user.getUserNum())
            .claim("authority", authorties)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 1)) // 1시간 유효
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();

            return token;
    }

    public Claims extractToken(String token) {
        Claims claims = Jwts
        .parserBuilder()
        .setSigningKey(key)
        .build() // 해당 Key를 이용하여 JwtParser 객체를 불러온다
        .parseClaimsJws(token)
        .getBody();
        return claims;
    }


    
}
