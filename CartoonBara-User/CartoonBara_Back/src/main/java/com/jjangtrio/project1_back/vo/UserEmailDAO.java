package com.jjangtrio.project1_back.vo;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository

public class UserEmailDAO {

    private final StringRedisTemplate redisTemplate;

    // 이메일 인증번호 저장
    public void saveUserEmail(String userEmail, String userId) {
        redisTemplate.opsForValue().set(userEmail, userId, Duration.ofSeconds(10000));
    }

    // 이메일 인증번호 검증
    public String getUserEmail(String userEmail) {
        return redisTemplate.opsForValue().get(userEmail);
    }

    // 이메일 인증번호 삭제
    public void deleteUserEmail(String userEmail) {
        redisTemplate.delete(userEmail);
    }

    // 이메일 존재여부 확인
    public boolean exists(String userEmail) {

        return redisTemplate.hasKey(userEmail);
    }

}
