package com.jjangtrio.project1_back.project1_back.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface Report_MemberDAO {
    int suspendUser(Long userNum); 
    int resumeUser(Long userNum);  
    int withdrawUser(Long userNum); 
    int restoreUser(Long userNum);
    int approveUser(Long userNum);
}
