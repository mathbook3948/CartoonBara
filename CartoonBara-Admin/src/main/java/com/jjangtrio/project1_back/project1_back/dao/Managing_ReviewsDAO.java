package com.jjangtrio.project1_back.project1_back.dao;

import org.apache.ibatis.annotations.Mapper;
import com.jjangtrio.project1_back.project1_back.vo.Managing_ReviewsVO;

@Mapper
public interface Managing_ReviewsDAO {

    Managing_ReviewsVO getTotalCounts();
    Managing_ReviewsVO getDailyCounts();
    Managing_ReviewsVO getWeeklyCounts();
    Managing_ReviewsVO getMonthlyCounts();

}
