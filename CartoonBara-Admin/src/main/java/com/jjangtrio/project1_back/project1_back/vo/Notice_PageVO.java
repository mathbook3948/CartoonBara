package com.jjangtrio.project1_back.project1_back.vo;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Setter
@Getter
public class Notice_PageVO {
    //페이징 처리를 위한 속성
	private int nowPage; // 현재 페이지 값 => 메뉴페이지와 연동 되는 변수
	private int nowBlock; // 현재 블럭 -> [] [] [] [] [] -> 1 Block
	private int totalRecord; // 총 게시물 수 .Dao로 부터 받음
	private int numPerPage; // 한 페이지 당 보여질 게시물 수
	private int pagePerBlock; // 한 블럭당 보여질 페이지 수
	private int totalBlock; // 전체 블럭 수
	private int totalPage; // 전체 페이지 수 => totalRecord/numPerPage
	private int beginPerPage; // 각 페이지별 시작 게시물이 index 값
	private int endPerPage; // 각 페이지별 끝 게시물이 index 값
	public Notice_PageVO() { // 기본 생성자에서 페이징 처리에 기본 값을 초기화
		this.nowPage = 1;      // 기본적으로 첫 페이지
        this.nowBlock = 1;     // 첫 블록
        this.numPerPage = 10;  // 한 페이지당 보여질 게시물 수
        this.pagePerBlock = 5; // 한 블록당 보여질 페이지 수
		System.out.println("페이지 처리 객체가 생성 되었습니다.");
	}
}
