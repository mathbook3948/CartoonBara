package com.jjangtrio.project1_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjangtrio.project1_back.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

}
