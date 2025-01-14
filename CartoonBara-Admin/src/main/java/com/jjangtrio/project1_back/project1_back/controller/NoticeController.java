package com.jjangtrio.project1_back.project1_back.controller;

import com.jjangtrio.project1_back.project1_back.service.NoticeService;
import com.jjangtrio.project1_back.project1_back.vo.NoticeVO;
import com.jjangtrio.project1_back.project1_back.vo.Notice_PageVO;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/notice")
@CrossOrigin(origins = "http://localhost:3009", allowCredentials = "true")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    private final Path filePath = Paths.get(System.getProperty("user.dir"), "uploads");

    @Autowired
    private Notice_PageVO notice_PageVO;

    // 공지사항 추가
    @PostMapping("/add")
    public ResponseEntity<?> insertNotice(@RequestParam(value = "image", required = false) MultipartFile image, // 파일이
                                                                                                                // 필수가
                                                                                                                // 아님
            @ModelAttribute NoticeVO noticeVO,
            HttpServletRequest request) {

                File directory = new File(filePath.toString());
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // 임시로 USER_NUM을 부모 테이블에 존재하는 값으로 설정 (예를 들어 1로 설정)
        // noticeVO.setPermissionNum(3L); // 부모 테이블에 존재하는 USER_NUM을 설정
        if (image != null && !image.isEmpty()) {
            String oriFn = image.getOriginalFilename();
            File file = new File(filePath +"/" +oriFn);


            System.out.println("===========================================");
            System.out.println(noticeVO.getPermissionNum());
            System.out.println(noticeVO.getNoticeContent());
            System.out.println(noticeVO.getNoticeTitle());


            try {
                image.transferTo(file); // 파일 저장
                noticeVO.setNoticeImage(oriFn); // 파일 이름 저장
                noticeService.insertNotice(noticeVO); // 공지사항 저장
                return ResponseEntity.ok("Upload successful");
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("File upload failed" + e.getMessage());
            }
        } else {
            // 이미지가 없는 경우 공지사항만 저장
            noticeService.insertNotice(noticeVO);
            return ResponseEntity.ok("Notice added successfully without file");
        }
    }

    // 공지사항 목록
    @RequestMapping("/list")
    public Map<String, Object> noticeList(@RequestParam Map<String, String> paramMap, HttpServletRequest request) {

        String cPage = paramMap.get("cPage"); // map key가 cPage인 값을 가져온다.

        // 1. 총 게시물 수
        int totalCnt = noticeService.totalCount(paramMap);
        notice_PageVO.setTotalRecord(totalCnt);

        // 2. 총 페이지 수 구하기 => 총 게시물 수 / 한 페이지당 보여질 게시물 수
        int totalPage = (int) Math.ceil(totalCnt / (double) notice_PageVO.getNumPerPage());
        notice_PageVO.setTotalPage(totalPage);

        // 3. 총 블록 수 구하기 => 전체 페이지 수 / 페이지 당 블럭 수
        int totalBlock = (int) Math.ceil((double) totalPage / notice_PageVO.getPagePerBlock());
        notice_PageVO.setTotalBlock(totalBlock);

        // 4. 현재 페이지 설정
        if (cPage != null) {
            notice_PageVO.setNowPage(Integer.parseInt(cPage));
        } else {
            notice_PageVO.setNowPage(1);
        }
        System.out.println("cPage :" + notice_PageVO.getNowPage());

        // 5. 현재 페이지의 시작 게시물과 끝 게시물 번호를 계산해서 pageVO에 저장한다.
        // 시작 페이지 cPage가 1일 때 연산 공식 (1 - 1) * 10 + 1 => 시작페이지가 0이면 + 1을 더하겠다. => 1
        // 시작 페이지 cPage가 2일 때 연산 공식 (2 - 1) * 10 + 1 => 0이 아니라 11이다.
        notice_PageVO.setBeginPerPage((notice_PageVO.getNowPage() - 1) * notice_PageVO.getNumPerPage() + 1);
        notice_PageVO.setEndPerPage(notice_PageVO.getBeginPerPage() + notice_PageVO.getNumPerPage() - 1);

        // Json 으로 메서드가 반환할 타입이고, 그 데이터를 저장할 맵--------------------
        Map<String, Object> response = new HashMap<>();
        // -----------------------------------------------------------------
        // 기존의 paramMap에 새로운 데이터를 추가한다. (시작과 끝 번호를 추가)
        // mapper에서 사용할 값들을 포함한다.
        Map<String, String> map = new HashMap<>(paramMap);
        map.put("begin", String.valueOf(notice_PageVO.getBeginPerPage()));
        map.put("end", String.valueOf(notice_PageVO.getEndPerPage()));

        // 페이징처리 결과 데이터가 저장이 되어서 반환
        List<NoticeVO> list = noticeService.NoticeList(map);
        System.out.println(" List Size :" + list.size());

        // 6. 페이지 블록을 구현
        int startPage = ((notice_PageVO.getNowPage() - 1) / notice_PageVO.getPagePerBlock())
                * notice_PageVO.getPagePerBlock() + 1;
        int endPage = startPage + notice_PageVO.getPagePerBlock() - 1;
        if (endPage > notice_PageVO.getTotalPage()) {
            endPage = notice_PageVO.getTotalPage();
        }

        System.out.println("6. startPage = " + startPage);
        System.out.println("6. endPage = " + endPage);

        response.put("data", list); // 페이징 처리가 완료된 리스트를 저장한 데이터
        response.put("totalItem", notice_PageVO.getTotalRecord()); // 전체 게시물의 수
        response.put("totalPages", notice_PageVO.getTotalPage());// 전체 페이지
        response.put("currentPage", notice_PageVO.getNowPage()); // 현재 페이지
        response.put("startPage", startPage); // 시작
        response.put("endPage", endPage); // 끝

        return response;
    }

    // 요청: updetail 메서드명 detail
    @GetMapping("/detail")
    public NoticeVO noticedetail(@RequestParam("noticenum") Long num) {
        NoticeVO noticeVO = noticeService.noticedetail(num);
        return noticeVO;
    }

    // // 공지사항 리스트
    // @GetMapping("/list")
    // public List<NoticeVO> list(Model model) {
    // return noticeService.list();
    // }

    // 공지사항 삭제
    @DeleteMapping("/delete")
    public void delete(@RequestParam("noticenum") Long num) {
        noticeService.delete(num);
    }
}
