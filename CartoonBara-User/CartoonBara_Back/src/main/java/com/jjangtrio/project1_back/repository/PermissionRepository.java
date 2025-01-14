package com.jjangtrio.project1_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.jjangtrio.project1_back.entity.Permission;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

        // 신고 당한 유저인지 판별
        @Query(value = "SELECT 1 FROM \\\"permission\\\"  WHERE user_num = :userNum AND permission_state = 3", nativeQuery = true)
        Long IsSingoFlagActive(@Param("userNum") Long userNum);

        // 신고 당한 유저로 등록
        @Modifying
        @Transactional
        @Query(value = "UPDATE \\\"permission\\\" SET permission_state = 3 WHERE user_num = :userNum", nativeQuery = true)
        Integer updateSingoFlag(@Param("userNum") Long userNum);
}

