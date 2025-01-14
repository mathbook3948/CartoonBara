package com.jjangtrio.project1_back.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getHeader("Authorization") == null) {
            System.out.println("없음1");
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization");
        if (!token.startsWith("Bearer ")) {
            System.out.println("없음2");
            filterChain.doFilter(request, response);
            return;
        }

        token = token.substring(7);

        try {
            Claims claims = jwtService.extractToken(token);
            List<String> authoritiesd = claims.get("authority", List.class);
            List<SimpleGrantedAuthority> authorities = authoritiesd.stream()
                    .map(authority -> new SimpleGrantedAuthority(authority))
                    .toList();

            for (SimpleGrantedAuthority authority : authorities) {
                System.out.println(authority.getAuthority());
            }
            System.out.println("authorities size =>" + authorities.size());

            UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(
                    claims.get("userNum"), // Long 자료형임 알아서 User 엔티티 잘 꺼내쓰길
                    null,
                    authorities);

            SecurityContextHolder.getContext().setAuthentication(user);
            System.out.println("검증 완료");
            System.out.println(claims.get("userNum"));
            filterChain.doFilter(request, response);
            return;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.setStatus(401);
            return;
        }
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper;
    }
}
