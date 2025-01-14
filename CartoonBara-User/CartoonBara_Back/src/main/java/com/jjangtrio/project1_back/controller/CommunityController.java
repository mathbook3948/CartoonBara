package com.jjangtrio.project1_back.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jjangtrio.project1_back.entity.Community;
import com.jjangtrio.project1_back.entity.Community_Like;
import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.service.CommunityService;
import com.jjangtrio.project1_back.service.Community_LikeService;
import com.jjangtrio.project1_back.vo.CommunityVO;
import com.jjangtrio.project1_back.vo.PageVO;
import com.jjangtrio.project1_back.vo.UserVO;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/community")
@CrossOrigin("*")
@MultipartConfig(maxFileSize = 1024 * 1024 * 5)
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private Community_LikeService communityLikeService;

    private final Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");

    public CommunityController() {
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create upload directory.", e);
        }
    }

    // 좋아요 추가 또는 취소
    @PostMapping("/liketoggle")
    public ResponseEntity<?> toggleLike(@RequestBody Map<String, Long> requestBody) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userNum = Long.valueOf(auth.getPrincipal().toString());

        try {
            Map<String, Object> result = new HashMap<>();
            // userId와 communityId를 통해 User와 Community 객체를 가져옴
            User user = new User();
            user.setUserNum(userNum);
            Community community = new Community();
            community.setCommunityNum(requestBody.get("communityNum"));

            // 좋아요 상태 토글
            Community_Like cl = communityLikeService.toggleLike(user, community);

            result.put("likeToggle", cl.getCommunityLikeIslike() == 1 ? true : false);
            result.put("communityLike", communityLikeService.countLike(requestBody.get("communityNum")));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("오류 발생: " + e.getMessage());
        }
    }

    // 좋아요 전체 갯수 구하기
    @GetMapping("/likecount")
    public ResponseEntity<?> countLike(@RequestParam("communityNum") Long communityNum) {
        try {
            Long likeCount = communityLikeService.countLike(communityNum);
            return ResponseEntity.ok(likeCount);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(0);
        }
    }

    @GetMapping("/getpage")
    @CrossOrigin("*")
    public ResponseEntity<?> getAllList(@RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam("category") Integer category,
            @RequestParam(value = "title", defaultValue = "") String title) {
        try {
            Map<String, Object> result = new HashMap<>();

            Long count = communityService.getCommunityCount(category);

            PageVO pvo = PageVO.builder()
                    .currentPage(page)
                    .pageSize(5)
                    .totalRecords(count.intValue())
                    .totalPages((int) Math.ceil((double) count / 5)) // 올림 처리
                    .startIndex((page - 1) * 5 + 1) // 시작 인덱스 계산
                    .endIndex(Math.min(page * 5, count.intValue())) // 끝 인덱스 계산 (pageSize 사용)
                    .hasPrevPage(page > 1) // 이전 페이지 여부
                    .hasNextPage(page < (int) Math.ceil((double) count / 5)) // 다음 페이지 여부
                    .build();

            result.put("PageVO", pvo);
            result.put("community", communityService.mapToCommunityVO(pvo, category, title));

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("실패: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Community> createCommunity(@ModelAttribute CommunityVO vo,
            @RequestParam(value = "community_image", required = false) MultipartFile[] files,
            HttpServletRequest request) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userNum; // = Long.valueOf(auth.getPrincipal().toString());

        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalArgumentException("로그인 정보가 누락되었습니다.");
        }

        try {
            userNum = Long.valueOf(auth.getPrincipal().toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("유효하지 않은 사용자 번호입니다.");
        }

        // IP 받아오기
        vo.setCommunityIp(request.getRemoteAddr());

        String communityImagename = ""; // 이미지가 없을 경우 빈 문자열로 초기화

        if (userNum == null) {
            throw new IllegalArgumentException("User 정보가 누락되었습니다.");
        }
        vo.setUser(UserVO.builder().userNum(userNum).build());

        // 유효성 검사: 제목 확인
        if (vo.getCommunityTitle() == null || vo.getCommunityTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("커뮤니티 제목은 필수 입력 사항입니다.");
        }

        // 유효성 검사: 내용 확인, 비어 있을 경우 기본값 설정
        if (vo.getCommunityContent() == null || vo.getCommunityContent().trim().isEmpty()) {
            vo.setCommunityContent("기본 커뮤니티 내용이 없습니다."); // 기본 내용 추가
        }

        // 파일이 있을 경우만 처리
        if (files != null && files.length > 0) {
            for (MultipartFile multipartFile : files) {
                if (!multipartFile.isEmpty()) {
                    String originalFileName = multipartFile.getOriginalFilename();
                    String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
                    try {
                        Path destinationFile = uploadDir.resolve(uniqueFileName).normalize();
                        Files.copy(multipartFile.getInputStream(), destinationFile);
                        communityImagename = uniqueFileName; // 이미지 파일이 있으면 그 파일 이름을 저장
                    } catch (IOException e) {
                        System.out.println("파일 저장 실패: " + e.getMessage());
                    }
                }
            }
        }

        // 이미지가 없으면 빈 문자열 또는 null로 저장
        vo.setCommunityImage(communityImagename);

        return ResponseEntity.ok(communityService.createCommunity(vo));
    }

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<?> serveFile(@PathVariable String filename) {
        // 상대 경로가 아닌 절대 경로를 이용해 파일을 찾음
        Path filePath = uploadDir.resolve(filename).normalize(); // 업로드 경로에 파일 이름 추가

        // 파일이 존재하는지 확인
        if (!Files.exists(filePath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("파일이 존재하지 않습니다: " + filename);
        }

        try {
            // 파일을 리소스로 반환
            return ResponseEntity.ok()
                    .header("Content-Type", Files.probeContentType(filePath)) // 파일 MIME 타입
                    .body(Files.readAllBytes(filePath)); // 파일 내용 반환
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일을 읽는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PutMapping("/hit")
    public ResponseEntity<String> updateHit(@RequestParam("num") Long communityNum) {
        try {
            communityService.updateHit(communityNum);
            return ResponseEntity.ok("조회수 업데이트 완료");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("조회수 업데이트 실패: " + e.getMessage());
        }
    }

    @GetMapping("/detail/{communityNum}")
    public Community getDetail(@PathVariable Long communityNum) {
        try {
            Community community = communityService.getDetail(communityNum)
                    .orElseThrow(() -> new IllegalArgumentException("Community not found"));
            return community;
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/all")
    public List<Community> findByAllOrederByCommunityNumDesc() {
        return communityService.findByAllOrederByCommunityNumDesc();
    }

    @DeleteMapping("/delete/{communityNum}")
    public void deleteGallery(@PathVariable Long communityNum) {
        communityService.deleteCommunity(communityNum);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {

        try {
            List<Community> getAllList = communityService.findAll();
            List<CommunityVO> result = new ArrayList<>();
            for (Community community : getAllList) {
                CommunityVO cvo = new CommunityVO();
                cvo.setCommunityNum(community.getCommunityNum());
                cvo.setUser(UserVO.builder().userNum(community.getUser().getUserNum())
                        .userNickname(community.getUser().getUserNickname()).build());
                cvo.setCommunityCategory(community.getCommunityCategory());
                cvo.setCommunityTitle(community.getCommunityTitle());
                cvo.setCommunityHit(community.getCommunityHit());
                cvo.setCommunityContent(community.getCommunityContent());
                cvo.setCommunityIp(community.getCommunityIp());
                cvo.setCommunityDate(community.getCommunityDate());
                cvo.setCommunityImage(community.getCommunityImage());
                result.add(cvo);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("실패: " + e.getMessage());
        }
    }

    @PutMapping("/update/{communityNum}")
    public ResponseEntity<?> updateCommunity(
            @PathVariable Long communityNum,
            @RequestParam("communityCategory") Long communityCategory,
            @RequestParam("communityTitle") String communityTitle,
            @RequestParam("communityContent") String communityContent,
            @RequestParam("communityIp") String communityIp,
            @RequestParam(value = "community_image", required = false) MultipartFile communityImage) {

        String communityImagePath = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userNum = Long.valueOf(auth.getPrincipal().toString());

        // 이미지가 있으면 처리
        if (communityImage != null && !communityImage.isEmpty()) {
            String fileName = communityImage.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
            Path path = Paths.get(uploadDir.toString(), uniqueFileName);

            try {
                Files.createDirectories(path.getParent());
                Files.write(path, communityImage.getBytes());
                communityImagePath = uniqueFileName; // 저장된 이미지 경로
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 오류: " + e.getMessage());
            }
        }

        // CommunityVO 객체 생성
        CommunityVO communityVO = CommunityVO.builder()
                .communityNum(communityNum)
                .user(UserVO.builder().userNum(userNum).build())
                .communityCategory(communityCategory)
                .communityTitle(communityTitle)
                .communityContent(communityContent)
                .communityIp(communityIp)
                .communityImage(communityImagePath) // 이미지가 없으면 null
                .build();

        // 커뮤니티 정보 업데이트
        communityService.updateCommunity(communityNum, communityVO);

        return ResponseEntity.ok("수정 완료");
    }

    @GetMapping("/best/likes") // ?minLikes = 5
    public ResponseEntity<?> getCommunitiesWithMinLike(@RequestParam("minLikes") Long minLikes,
            @RequestParam(value = "page", defaultValue = "1") int page) {
        try {
            List<Community> communities = communityService.getCommunitiesWithMinLikes(minLikes);

            // 총 레코드 수 계산
            int totalRecords = communities.size();

            // 페이지네이션 계산
            int pageSize = 5;
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalRecords);

            // 현재 페이지의 커뮤니티 가져오기
            List<Community> paginatedCommunities = communities.subList(startIndex, endIndex);

            // PageVO 생성
            PageVO pvo = PageVO.builder()
                    .currentPage(page)
                    .pageSize(pageSize)
                    .totalRecords(totalRecords)
                    .totalPages(totalPages)
                    .startIndex(startIndex + 1) // 시작 인덱스 (1부터 시작)
                    .endIndex(endIndex) // 종료 인덱스
                    .hasPrevPage(page > 1) // 이전 페이지 여부
                    .hasNextPage(page < totalPages) // 다음 페이지 여부
                    .build();

            // 결과를 맵에 담기
            Map<String, Object> response = new HashMap<>();
            response.put("PageVO", pvo);
            response.put("communities", paginatedCommunities);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("실패: " + e.getMessage());
        }
    }

    @GetMapping("/anonymousbest/likes")
    public ResponseEntity<?> getCommunitiesWithMinLikes(@RequestParam("minLikes") Long minLikes,
            @RequestParam(value = "page", defaultValue = "1") int page) {
        try {
            // 커뮤니티 목록 필터링
            List<Community> filteredCommunities = communityService.getCommunitiesWithMinLikesCategory(minLikes);

            // 총 레코드 수 계산
            int totalRecords = filteredCommunities.size();

            // 페이지네이션 계산
            int pageSize = 5;
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalRecords);

            // 현재 페이지의 커뮤니티 가져오기
            List<Community> paginatedCommunities = filteredCommunities.subList(startIndex, endIndex);

            // PageVO 생성
            PageVO pvo = PageVO.builder()
                    .currentPage(page)
                    .pageSize(pageSize)
                    .totalRecords(totalRecords)
                    .totalPages(totalPages)
                    .startIndex(startIndex + 1) // 시작 인덱스 (1부터 시작)
                    .endIndex(endIndex) // 종료 인덱스
                    .hasPrevPage(page > 1) // 이전 페이지 여부
                    .hasNextPage(page < totalPages) // 다음 페이지 여부
                    .build();

            // 결과를 맵에 담기
            Map<String, Object> response = new HashMap<>();
            response.put("PageVO", pvo);
            response.put("communities", paginatedCommunities);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/category/{communityCategory}")
    public ResponseEntity<List<Community>> findByCommunityCategory(@PathVariable Long communityCategory) {
        List<Community> communities = communityService.findByCommunityCategory(communityCategory);
        return ResponseEntity.ok(communities);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Community>> searchCommunityTitles(@RequestParam("communityTitle") String title) {
        List<Community> communities = communityService.searchCommunityTitles(title);
        return ResponseEntity.ok(communities);
    }

    @GetMapping("/searchcategory")
    public ResponseEntity<List<Community>> searchCommunityCategory(@RequestParam("communityTitle") String title) {
        List<Community> communities = communityService.searchCommunityCategory(title);
        return ResponseEntity.ok(communities);
    }

    // 특정 커뮤니티에서 사용자의 좋아요 상태 확인
    @GetMapping("/likechecks")
    public ResponseEntity<?> checkLikes(@RequestParam("communityNum") Long communityNum,
            @RequestParam("isLike") Long isLike) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userNum = Long.valueOf(auth.getPrincipal().toString());
        try {
            // checkLikes 메서드를 호출하여 좋아요 상태 확인
            boolean isLiked = communityLikeService.checkLikes(communityNum, userNum, isLike);

            // 상태에 따라 적절한 메시지 반환
            if (isLiked) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.ok(false);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("오류 발생: " + e.getMessage());
        }
    }

    // 마이페이지 검색
    @GetMapping("/search/MyPage")
    public ResponseEntity<List<Community>> findByCommunityTitleAndUser_UserNum(
            @RequestParam("communityTitle") String communityTitle) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long user_num = Long.valueOf(auth.getPrincipal().toString());
        List<Community> communities = communityService.findByCommunityTitleAndUser_UserNum(communityTitle, user_num);
        return ResponseEntity.ok(communities);
    }

    @GetMapping("/getCommuLike")
    public ResponseEntity<?> getAllMyPageLike(@RequestParam(value = "page", defaultValue = "1") int page) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long user_num = Long.valueOf(auth.getPrincipal().toString());
        try {
            List<Community> getAllList;

            if (user_num != null) {
                getAllList = communityService.findLikedCommunitiesByUser(user_num);
            } else {
                getAllList = communityService.findAll();
            }

            // 총 레코드 수 계산
            int totalRecords = getAllList.size();

            // 페이지네이션 계산
            int pageSize = 5;
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            int startIndex = (page - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalRecords);

            // 현재 페이지의 커뮤니티 가져오기
            List<Community> paginatedCommunities = getAllList.subList(startIndex, endIndex);

            // Community 데이터를 CommunityVO로 변환
            List<CommunityVO> result = new ArrayList<>();
            for (Community community : paginatedCommunities) {
                CommunityVO cvo = CommunityVO.builder()
                        .user(UserVO.builder()
                                .userNum(community.getUser().getUserNum())
                                .userNickname(community.getUser().getUserNickname())
                                .build())
                        .communityNum(community.getCommunityNum())
                        .communityCategory(community.getCommunityCategory())
                        .communityTitle(community.getCommunityTitle())
                        .communityDate(community.getCommunityDate())
                        .communityHit(community.getCommunityHit())
                        .build();
                result.add(cvo);
            }

            // PageVO 생성
            PageVO pvo = PageVO.builder()
                    .currentPage(page)
                    .pageSize(pageSize)
                    .totalRecords(totalRecords)
                    .totalPages(totalPages)
                    .startIndex(startIndex + 1) // 시작 인덱스 (1부터 시작)
                    .endIndex(endIndex) // 종료 인덱스
                    .hasPrevPage(page > 1) // 이전 페이지 여부
                    .hasNextPage(page < totalPages) // 다음 페이지 여부
                    .build();

            Map<String, Object> response = new HashMap<>();
            response.put("PageVO", pvo);
            response.put("communities", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("실패: " + e.getMessage());
        }
    }

    // 마이페이지 커뮤니티 리스트(페이징처리)
    @GetMapping("/myPageCommu")
    public ResponseEntity<?> getAllMyPage(@RequestParam(value = "page", defaultValue = "1") int page) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long user_num = Long.valueOf(auth.getPrincipal().toString());
        try {
            Map<String, Object> result = new HashMap<>();
            // 커뮤니티의 전체 개수 가져오기
            Long count = communityService.getCommCount(user_num);
            // 페이지 정보 설정
            PageVO pvo = PageVO.builder()
                    .currentPage(page)
                    .pageSize(10)
                    .totalRecords(count.intValue())
                    .totalPages((int) Math.ceil((double) count / 10)) // 올림 처리
                    .startIndex((page - 1) * 10 + 1) // 시작 인덱스 계산
                    .endIndex(Math.min(page * 10, count.intValue())) // 끝 인덱스 계산
                    .hasPrevPage(page > 1) // 이전 페이지 여부
                    .hasNextPage(page < (int) Math.ceil((double) count / 10)) // 다음 페이지 여부 (수정됨)
                    .build();
            result.put("PageVO", pvo);

            // 로그인된 사용자 번호에 따른 커뮤니티 리스트 가져오기
            List<CommunityVO> getAllList = communityService.getcommuList(pvo, user_num);

            // CommunityVO 객체들을 그대로 리스트에 넣어서 반환
            result.put("communityList", getAllList);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("실패: " + e.getMessage());
        }
    }

    // other페이지 커뮤니티리스트(페이징처리)
    @GetMapping("/myPageCommu/{userNum}")
    public ResponseEntity<?> getAll(@RequestParam(value = "page", defaultValue = "1") int page,
            @PathVariable Long userNum) {

        try {
            Map<String, Object> result = new HashMap<>();
            Long count = communityService.getCommunityCount(-1);
            List<Community> getAllList;
            PageVO pvo = PageVO.builder()
                    .currentPage(page)
                    .pageSize(10)
                    .totalRecords(count.intValue())
                    .totalPages((int) Math.ceil((double) count / 10)) // 올림 처리
                    .startIndex((page - 1) * 10 + 1) // 시작 인덱스 계산
                    .endIndex(Math.min(page * 10, count.intValue())) // 끝 인덱스 계산
                    .hasPrevPage(page > 1) // 이전 페이지 여부
                    .hasNextPage(page < (int) Math.ceil((double) count / 30)) // 다음 페이지 여부
                    .build();
            result.put("PageVO", pvo);
            result.put("otherCommunityList", communityService.getCommunityList(pvo, userNum));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("실패: " + e.getMessage());
        }
    }

    @GetMapping("/latestCommunityList")
    public ResponseEntity<?> getLatestCommunityList() {
        try {
            return ResponseEntity.ok(communityService.getLatestCommunityList());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("실패: " + e.getMessage());
        }
    }
}
