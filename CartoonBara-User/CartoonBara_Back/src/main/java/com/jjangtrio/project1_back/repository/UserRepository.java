package com.jjangtrio.project1_back.repository;

import java.lang.StackWalker.Option;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.jjangtrio.project1_back.entity.User;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 모든 유저를 userNum 기준으로 내림차순 정렬하여 가져오기
    List<User> findByOrderByUserNumAsc();

    // userNum에 맞는 유저 목록을 가져오기
    List<User> findByUserNum(Long userNum);

    // userId에 맞는 유저 목록을 가져오기
    List<User> findByUserId(String userId);

    // userId를 Boolean을 통해 id체크 확인
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.userId = :userId")
    boolean existsByUserId(@Param("userId") String userId);

    // 유저에서 이메일이 존재하는지 확인하고 없다면 이메일 전송
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.userEmail = :userEmail")
    boolean existsByUserEmail(@Param("userEmail") String userEmail);

    // 아이디 찾기
    User findByUserNameAndUserEmail(String userName, String userEmail);

    // 닉네임으로 사용자 존재 여부 확인
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.userNickname = :nickname")
    boolean existsByUserNickname(@Param("nickname") String nickname);

    // 전화번호로 사용자 존재 여부 확인
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.userPhone = :phone")
    boolean existsByUserPhone(@Param("phone") String phone);

    // 이메일로 사용자 조회
    User findByUserEmail(String userEmail);

    // 이메일과 아이디로 사용자 조회
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.userEmail = :userEmail AND u.userId = :userId")
    boolean existsByUserEmailAndUserId(@Param("userEmail") String userEmail, @Param("userId") String userId);

    @Query(value = """
            SELECT user_num, user_name, user_image, user_email, user_phone, user_birth, user_nickname, user_gender, user_date, user_introduce
            FROM(
                SELECT
                    ROW_NUMBER()OVER (ORDER BY user_num DESC) AS row_num,
                    user_num,
                    user_name,
                    user_image,
                    user_email,
                    user_phone,
                    user_birth,
                    user_nickname,
                    user_gender,
                    user_date,
                    user_introduce
                FROM user
                   ) AS numbered
                        WHERE numbered.row_num BETWEEN :startIndex AND :endIndex
                        ORDER BY numbered.row_num
                    """, nativeQuery = true)
    List<Object[]> findUsersWithinRange(@Param("startIndex") int startIndex, @Param("endIndex") int endIndex);

    List<User> findAllByOrderByUserNumAsc();

    // =====================================================================================================================
    // 김채린이 추가함 오류나면 불러
    @Modifying
    @Transactional
    @Query(value = "UPDATE  \\\"user\\\"  SET user_singo_accumulated = user_singo_accumulated + 1 WHERE user_num = :userNum", nativeQuery = true)
    Integer updateSingoAccumulated(@Param("userNum") Long userNum);
    // =====================================================================================================================

    // 회원탈퇴
    @Transactional
    @Modifying
    void deleteByUserNum(Long userNum);

    String findPasswordByUserNum(Long userNum);

}
