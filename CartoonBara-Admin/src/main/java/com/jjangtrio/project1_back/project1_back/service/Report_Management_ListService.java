package com.jjangtrio.project1_back.project1_back.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jjangtrio.project1_back.project1_back.dao.Report_Management_ListDAO;
import com.jjangtrio.project1_back.project1_back.vo.Report_Management_List;

@Service
public class Report_Management_ListService {

    @Autowired
    private Report_Management_ListDAO reportManagementListDAO;

    public List<Report_Management_List> getWReportList(Long userNum) {
        return reportManagementListDAO.getWReportList(userNum);
    }

    public List<Report_Management_List> getWCReportList(Long userNum) {
        return reportManagementListDAO.getWCReportList(userNum);
    }

    public List<Report_Management_List> getCReportList(Long userNum) {
        return reportManagementListDAO.getCReportList(userNum);
    }

    public List<Report_Management_List> getCCReportList(Long userNum) {
        return reportManagementListDAO.getCCReportList(userNum);
    }

    public void updateSingoFlag(Map<String, Long> params) {
        reportManagementListDAO.updateSingoFlag(params);
    }
    
}

   