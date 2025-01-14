package com.jjangtrio.project1_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jjangtrio.project1_back.entity.Singo;

import io.lettuce.core.dynamic.annotation.Param;

public interface SingoRepository extends JpaRepository<Singo, Long> {
    
    @Query("SELECT 1 FROM Singo s WHERE s.singoCategory = :singoCategory AND s.user.userNum = :userNum AND s.primarynumber = :primarynumber")
    Long existSingoByLog(
        @Param("singoCategory") Long singoCategory, 
        @Param("userNum") Long userNum, 
        @Param("primarynumber") Long primarynumber
    );
}
