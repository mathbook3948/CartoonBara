package com.jjangtrio.project1_back.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"permission\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @PrimaryKeyJoinColumn(name = "user_num")
    private Long userNum;

    @Column(name = "permission_master", nullable = false)
    private Long permissionMaster;

    @Column(name = "permission_state", nullable = false)
    private Long permissionState;

    @Column(name = "is_password_less", nullable = false)  // 여기를 수정
    private Long isPasswordLess;
}
