package com.jjangtrio.project1_back.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jjangtrio.project1_back.entity.Log;

@Repository
public interface LogRepository extends JpaRepository<Log, Long>{
    @Query("SELECT l FROM Log l WHERE l.logDate >= :sevenDaysAgo")
    List<Log> findLogsFromLastSevenDays(@Param("sevenDaysAgo") Date sevenDaysAgo);
}
