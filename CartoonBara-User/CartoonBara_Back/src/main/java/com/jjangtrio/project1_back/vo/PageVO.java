package com.jjangtrio.project1_back.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageVO {

    private int currentPage;       // 현재 페이지 번호
    private int pageSize;          // 한 페이지당 데이터 개수
    private int totalRecords;      // 전체 데이터 개수
    private int totalPages;        // 전체 페이지 수
    private int startIndex;        // 시작 인덱스
    private int endIndex;          // 끝 인덱스
    private boolean hasPrevPage;   // 이전 페이지 존재 여부
    private boolean hasNextPage;   // 다음 페이지 존재 여부
}
