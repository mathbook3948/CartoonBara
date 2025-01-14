package com.jjangtrio.project1_back.project1_back.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jjangtrio.project1_back.project1_back.vo.Membership_ManagementVO;

@Mapper
public interface Membership_ManagementDAO {

  // 페이징 및 검색을 포함한 회원관리 리스트
  List<Membership_ManagementVO> Membership_ManagementList(Map<String, Object> param);

  // 전체 데이터 개수 조회
  int totalCount(Map<String, Object> map);

  // 특정 승인 상태에 해당하는 전체 데이터 개수 조회
  int totalCountByApprovalStatus(Map<String, Object> param);

  // 특정 승인 상태를 가진 회원 목록 조회
  List<Membership_ManagementVO> Membership_ManagementListByApprovalStatus(Map<String, Object> param);


  // 가입 대기자 조회
  int getPermissionState2Count();
}
