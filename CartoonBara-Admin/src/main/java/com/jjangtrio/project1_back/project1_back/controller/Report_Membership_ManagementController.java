package com.jjangtrio.project1_back.project1_back.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjangtrio.project1_back.project1_back.service.Report_Membership_ManagementService;
import com.jjangtrio.project1_back.project1_back.vo.Report_Membership_ManagementVO;
import com.jjangtrio.project1_back.project1_back.vo.Report_Membership_Management_PageVO;

@RestController
@RequestMapping("/reportmembership")
public class Report_Membership_ManagementController {

    @Autowired
    private Report_Membership_ManagementService reportMembershipManagementService;

    @Autowired
    private Report_Membership_Management_PageVO repotepageVO;

    @GetMapping("/memberlist")
    public ResponseEntity<?> getMembershipList(
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "permissionNum", required = false) Integer permissionNum
            ) {

        // --- 1. 요청된 파라미터 로깅 ---
        System.out.println("Category: " + category);
        System.out.println("Keyword: " + keyword);
        System.out.println("Page: " + page);
        System.out.println("Size: " + size);
        System.out.println("PermissionNum: " + permissionNum);

        // --- 2. 파라미터 설정 ---
        Map<String, Object> params = new HashMap<>();
        params.put("category", category);
        params.put("keyword", keyword);

        // --- 3. 전체 레코드 개수 조회 ---
        int totalCnt = reportMembershipManagementService.getTotalCount(params);

         // 여기에서 totalCount 값을 로그로 출력
    System.out.println("Total Count: " + totalCnt); // 디버깅용

        // --- 4. 페이징 설정 ---
        repotepageVO.setTotalRecord(totalCnt);
        repotepageVO.setNowPage(page);
        repotepageVO.setTotalPage((int) Math.ceil(totalCnt / (double) repotepageVO.getNumPerPage()));
        repotepageVO.setBeginPerPage((page - 1) * repotepageVO.getNumPerPage() + 1);
        repotepageVO.setEndPerPage(repotepageVO.getBeginPerPage() + repotepageVO.getNumPerPage() - 1);

         // 만약 끝 번호가 총 레코드 수를 초과하면, 총 레코드 수로 설정
         if (repotepageVO.getEndPerPage() > totalCnt) {
            repotepageVO.setEndPerPage(totalCnt);
        }

         // 현재 블록 및 전체 블록 설정
         repotepageVO.setTotalBlock((int) Math.ceil((double) repotepageVO.getTotalPage() / repotepageVO.getPagePerBlock()));
         repotepageVO.setNowBlock((int) Math.ceil((double) page / repotepageVO.getPagePerBlock()));
 

        // --- 5. 페이징을 위한 offset 및 limit 설정 ---
        int offset = (page - 1) * size;
        params.put("offset", offset);
        params.put("limit", size);
        

        // --- 6. 데이터 조회 ---
        List<Report_Membership_ManagementVO> list = reportMembershipManagementService.reportMembershipManagementList(params);

          // --- 7. 페이지 블록의 시작/끝 계산 ---
          for (Report_Membership_ManagementVO member : list) {
            System.out.println("Member info: " + member.getNickname() + " | 승인 상태: ");
        }
  
           // --- 8. 페이지 블록의 시작/끝 계산 ---
        int startPage = ((repotepageVO.getNowBlock() - 1) * repotepageVO.getPagePerBlock()) + 1;
        int endPage = startPage + repotepageVO.getPagePerBlock() - 1;

           // 만약 끝 페이지가 전체 페이지 수를 초과하면 전체 페이지 수로 설정
           if (endPage > repotepageVO.getTotalPage()) {
            endPage = repotepageVO.getTotalPage();
        }
  
        repotepageVO.setStartPage(startPage);
        repotepageVO.setEndPage(endPage);
  

        // --- 9. 응답 데이터 구성 ---
        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("totalCount", totalCnt);
        response.put("totalPages", repotepageVO.getTotalPage());
        response.put("currentPage", repotepageVO.getNowPage());
        response.put("startPage", startPage);
        response.put("endPage", endPage);
        response.put("pagePerBlock", repotepageVO.getPagePerBlock());

        // --- 9. ResponseEntity로 응답 반환 ---
        return ResponseEntity.ok(response);
    }

      @PutMapping("/updateStatus")
    public ResponseEntity<?> updateUserStatus(@RequestBody Map<String, Object> params) {
        int userNum = (int) params.get("userNum");
        int permissionState = (int) params.get("permissionState");

        boolean success = reportMembershipManagementService.updateUserStatus(userNum, permissionState);
        if (success) {
            return ResponseEntity.ok("상태 업데이트 성공");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("상태 업데이트 실패");
        }
    }

    @PostMapping("/revoke")
    public ResponseEntity<String> revokeReport(@RequestBody Map<String, Object> params) {
    int userNum = (int) params.get("userNum");
    boolean result = reportMembershipManagementService.revokeReport(userNum);
    if (result) {
        return ResponseEntity.ok("신고 철회가 완료되었습니다.");
    } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("신고 철회에 실패했습니다.");
    }
}


    
    
    
}

