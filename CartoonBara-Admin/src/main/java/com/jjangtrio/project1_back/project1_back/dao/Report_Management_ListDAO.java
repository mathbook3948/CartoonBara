package com.jjangtrio.project1_back.project1_back.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.jjangtrio.project1_back.project1_back.vo.Report_Management_List;

@Mapper
@Repository
public interface Report_Management_ListDAO {
    List<Report_Management_List> getWReportList(Long userNum);
    List<Report_Management_List> getWCReportList(Long userNum);
    List<Report_Management_List> getCReportList(Long userNum);
    List<Report_Management_List> getCCReportList(Long userNum);
    void updateSingoFlag(Map<String, Long> params);
}
