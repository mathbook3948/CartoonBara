package com.jjangtrio.project1_back.project1_back.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjangtrio.project1_back.project1_back.service.Membership_ManagementService;
import com.jjangtrio.project1_back.project1_back.vo.Membership_ManagementVO;
import com.jjangtrio.project1_back.project1_back.vo.Membership_Management_PageVO;

@RestController
@RequestMapping("/membership")
public class Membership_ManagementController {

    @Autowired
    private Membership_ManagementService membershipManagementService;

    @Autowired
    private Membership_Management_PageVO pageVO;

    @GetMapping("/memberlist")
    public ResponseEntity<?> getMembershipList(
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "1") int page, // 기본 페이지는 1로 설정
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "approvalStatus", required = false) String approvalStatus,
            @RequestParam(name = "permissionNum", required = false) Integer permissionNum 
            ) {
            
        // --- 1. 요청된 파라미터 로깅 ---
        System.out.println("Category: " + category);
        System.out.println("Keyword: " + keyword);
        System.out.println("Page: " + page);
        System.out.println("Size: " + size);
        System.out.println("ApprovalStatus: " + approvalStatus);
        System.out.println("PermissionNum: " + permissionNum);
    
        // --- 2. 파라미터 설정 ---
        Map<String, Object> params = new HashMap<>();
        params.put("category", category);
        params.put("keyword", keyword);
        if (approvalStatus != null) {
            params.put("approvalStatus", approvalStatus);  // 여기서 approvalStatus를 params에 추가
        }
        

        // --- 3. 전체 레코드 개수 조회 ---
        int totalCnt = membershipManagementService.getTotalCount(params);

        // --- 4. 페이징 설정 ---
        pageVO.setTotalRecord(totalCnt);
        pageVO.setNowPage(page);
        pageVO.setTotalPage((int) Math.ceil(totalCnt / (double) pageVO.getNumPerPage()));
        pageVO.setBeginPerPage((page - 1) * pageVO.getNumPerPage() + 1);
        pageVO.setEndPerPage(pageVO.getBeginPerPage() + pageVO.getNumPerPage() - 1);

        // 만약 끝 번호가 총 레코드 수를 초과하면, 총 레코드 수로 설정
        if (pageVO.getEndPerPage() > totalCnt) {
            pageVO.setEndPerPage(totalCnt);
        }

        // 현재 블록 및 전체 블록 설정
        pageVO.setTotalBlock((int) Math.ceil((double) pageVO.getTotalPage() / pageVO.getPagePerBlock()));
        pageVO.setNowBlock((int) Math.ceil((double) page / pageVO.getPagePerBlock()));

        // --- 5. 페이징을 위한 offset 및 limit 설정 ---
        int offset = (page - 1) * size;
        params.put("offset", offset);
        params.put("limit", size);

        // --- 6. 데이터 조회 ---
        List<Membership_ManagementVO> list = membershipManagementService.getMembershipManagementList(params);

        // --- 7. 데이터 로깅 ---
        for (Membership_ManagementVO member : list) {
            System.out.println("Member info: " + member.getNickname() + " | 승인 상태: " + member.getApprovalStatus());
        }

        // --- 8. 페이지 블록의 시작/끝 계산 ---
        int startPage = ((pageVO.getNowBlock() - 1) * pageVO.getPagePerBlock()) + 1;
        int endPage = startPage + pageVO.getPagePerBlock() - 1;

        // 만약 끝 페이지가 전체 페이지 수를 초과하면 전체 페이지 수로 설정
        if (endPage > pageVO.getTotalPage()) {
            endPage = pageVO.getTotalPage();
        }

        pageVO.setStartPage(startPage);
        pageVO.setEndPage(endPage);

      
        // --- 9. 응답 데이터 구성 ---
        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("totalCount", totalCnt);
        response.put("totalPages", pageVO.getTotalPage());
        response.put("currentPage", pageVO.getNowPage());
        response.put("startPage", startPage);
        response.put("endPage", endPage);
        response.put("pagePerBlock", pageVO.getPagePerBlock());

        // --- 10. ResponseEntity로 응답 반환 ---
        return ResponseEntity.ok(response);


       
        
    }
     // 가입 대기자 목록 조회
    @GetMapping("/getPermissionState2Count")
    public ResponseEntity<?> getPermissionState2Count() {
        try {
            // 가입 대기자 수를 가져오기 위한 서비스 호출
            int pendingCount = membershipManagementService.getpermissionstate2();
            
            // 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("count", pendingCount);  // 'count'에 대기자 수 할당

            // 200 OK 상태와 함께 JSON 응답 반환
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 오류 처리
            return ResponseEntity.status(500).body("Internal Server Error");
        }
}
}