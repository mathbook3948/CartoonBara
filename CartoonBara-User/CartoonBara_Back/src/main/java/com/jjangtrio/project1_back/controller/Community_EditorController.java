package com.jjangtrio.project1_back.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.jjangtrio.project1_back.entity.Community_Editor;
import com.jjangtrio.project1_back.entity.Community_Editor_Star;
import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.service.Community_EditorCommService;
import com.jjangtrio.project1_back.service.Community_EditorService;
import com.jjangtrio.project1_back.service.Community_EditorStarService;
import com.jjangtrio.project1_back.vo.Community_EditorVO;
import com.jjangtrio.project1_back.vo.PageVO;
import com.jjangtrio.project1_back.vo.UserVO;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@MultipartConfig(maxFileSize = 1024 * 1024 * 5)
@RequestMapping("/api/CommunityEditor")
@CrossOrigin("*")
public class Community_EditorController {

    @Autowired
    private Community_EditorService communityEditorService;

    @Autowired
    private Community_EditorCommService communityEditorCommService;

    @Autowired
    private Community_EditorStarService communityEditorStarService;

    private final Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");

    public Community_EditorController() {
        try {
            Files.createDirectories(uploadDir);
            System.out.println("File upload directory: " + uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create upload directory.", e);
        }
    }

    @PostMapping("/editor/detail")
    public List<Community_Editor> postMethodName(@RequestBody Long communityEditorNum) {

        return communityEditorService.getEditorsDetail(communityEditorNum);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> createCommunityEditor(
            @RequestParam("communityEditorTitle") String communityEditorTitle,
            @RequestParam("communityEditorCategory") Long communityEditorCategory,
            @RequestParam("communityEditorContent") String communityEditorContent,
            @RequestParam(value = "communityEditorImage", required = false) MultipartFile communityEditorImage,
            HttpServletRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long user_num = Long.parseLong(auth.getPrincipal().toString());

        String communityImagePath = null;

        try {
            String communityEditorIp = request.getRemoteAddr();

            if (communityEditorImage != null) {
                if (!communityEditorImage.isEmpty()) {
                    String originalFileName = communityEditorImage.getOriginalFilename();
                    String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
                    try {
                        Path destinationFile = uploadDir.resolve(uniqueFileName).normalize();
                        Files.copy(communityEditorImage.getInputStream(), destinationFile);
                        communityImagePath = uniqueFileName; // 이미지 파일이 있으면 그 파일 이름을 저장
                    } catch (IOException e) {
                        System.out.println("파일 저장 실패: " + e.getMessage());
                    }
                }
            }

            // VO 객체 생성 및 초기화
            Community_EditorVO vo = Community_EditorVO.builder()
                    .communityEditorTitle(communityEditorTitle)
                    .communityEditorCategory(communityEditorCategory)
                    .communityEditorContent(communityEditorContent)
                    .user(UserVO.builder().userNum(user_num).build())
                    .communityEditorIp(communityEditorIp)
                    .communityEditorImage(communityImagePath) // 바이너리 데이터 저장
                    .build();

            communityEditorService.createCommunityEditor(vo);

            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("error : " + e.getMessage());
        }
    }

    @GetMapping("/upload/{filename:.+}")
    public ResponseEntity<?> saveFile(
            @PathVariable String filename) {
        Path filePath = uploadDir.resolve(filename).normalize();

        if (!Files.exists(filePath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("파일이 존재하지 않습니다." + filename);
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

    @GetMapping("/detail/{communityEditorNum}")
    public Community_Editor getDetail(@PathVariable Long communityEditorNum) {
        try {
            Community_Editor communityEditor = communityEditorService.getDetail(communityEditorNum)
                    .orElseThrow(
                            () -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + communityEditorNum));
            return communityEditor;
        } catch (Exception e) {
            return null;
        }
    }

    @PutMapping("/hit")
    public ResponseEntity<String> updateHit(@RequestParam("num") Long communityEditorNum) {
        try {
            communityEditorService.updateHit(communityEditorNum);
            return ResponseEntity.ok("조회수 업데이트 완료");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("조회수 업데이트 실패: " + e.getMessage());
        }
    }

    @GetMapping("/latestList")
    public ResponseEntity<?> getCommunityEditors(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "category", defaultValue = "-1") Long category,
            @RequestParam(value = "title", defaultValue = "") String title) {
        try {
            // 전체 게시글 개수 조회
            Long count = communityEditorService.getCommunityEditorCount(category);

            // 페이지 정보 생성
            PageVO pvo = PageVO.builder()
                    .currentPage(page)
                    .pageSize(10) // 페이지당 게시글 수
                    .totalRecords(count.intValue())
                    .totalPages((int) Math.ceil((double) count / 10))
                    .startIndex((page - 1) * 10 + 1)
                    .endIndex(Math.min(page * 10, count.intValue()))
                    .hasPrevPage(page > 1)
                    .hasNextPage(page < (int) Math.ceil((double) count / 10))
                    .build();

            // 게시글 데이터 조회
            List<Community_EditorVO> editors = communityEditorService.getCommunity_Editors(pvo, category, title);

            // 결과 데이터 반환
            Map<String, Object> result = new HashMap<>();
            result.put("PageVO", pvo);
            result.put("communityEditors", editors);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            // 오류 발생 시 로그 출력 및 응답 처리
            System.err.println("Error fetching community editors: " + e.getMessage());
            return ResponseEntity.status(500).body("실패: " + e.getMessage());
        }
    }

    @PutMapping("/update/{communityEditorNum}")
    public ResponseEntity<?> updateCommunityEditor(
            @PathVariable Long communityEditorNum,
            // @ModelAttribute Community_EditorVO vo,
            @RequestParam("communityEditorCategory") Long communityCategory,
            @RequestParam("communityEditorTitle") String communityTitle,
            @RequestParam("communityEditorContent") String communityContent,
            @RequestParam("communityEditorIp") String communityIp,
            @RequestParam(value = "images", required = false) MultipartFile files) {

        String filesPath = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userNum = Long.valueOf(auth.getPrincipal().toString());

        // vo.setUser(UserVO.builder().userNum(userNum).build());

        if (files != null && !files.isEmpty()) {
            String fileName = files.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
            Path path = Paths.get(uploadDir.toString(), uniqueFileName);

            try {
                Files.createDirectories(path.getParent());
                Files.write(path, files.getBytes());
                filesPath = uniqueFileName; // 저장된 이미지 경로
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 오류: " + e.getMessage());
            }
        }

        Community_EditorVO vo = Community_EditorVO.builder()
                .communityEditorNum(communityEditorNum)
                .user(UserVO.builder().userNum(userNum).build())
                .communityEditorCategory(communityCategory)
                .communityEditorTitle(communityTitle)
                .communityEditorContent(communityContent)
                .communityEditorIp(communityIp)
                .communityEditorImage(filesPath)
                .build();

        // // 서비스 호출
        communityEditorService.updateCommunityEditor(communityEditorNum, vo);

        return ResponseEntity.ok("Community Editor updated successfully");
    }

    @PostMapping("/delete/{communityEditorNum}")
    public ResponseEntity<?> deleteCommunityEditor(@PathVariable Long communityEditorNum) {
        try {
            communityEditorService.deleteCommunityEditor(communityEditorNum);
            return ResponseEntity.ok("Community Editor deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Community Editor deletion failed: " + e.getMessage());
        }
    }

    // 별점 등록,수정
    @PostMapping("/startoggle/{communityEditorNum}")
    public ResponseEntity<?> toggleStart(
            @RequestBody Map<String, Long> requestBody) {

        System.out.println("requestBody!!!!!!!!!!!!: " + requestBody);

        try {
            Long userNum = Long.valueOf(requestBody.get("userNum").toString());
            Long communityEditorNum = Long.valueOf(requestBody.get("communityEditorNum").toString());
            Long starValue = Long.valueOf(requestBody.get("communityEditorStar").toString());

            User user = new User();
            user.setUserNum(userNum);

            Community_Editor communityEditor = new Community_Editor();
            communityEditor.setCommunityEditorNum(communityEditorNum);

            Community_Editor_Star star = new Community_Editor_Star();
            star.setCommunityEditorStar(starValue);

            System.out.println("userNum: " + user + ", communityEditorNum: " + communityEditor
                    + ", communityEditorStar: " + star);

            communityEditorStarService.toggleStar(user, communityEditor, star);

            return ResponseEntity.ok("Star toggled successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("error: " + e.getMessage());
        }
    }

    // 별점 개수 조회
    @GetMapping("/starCount/{communityEditorNum}")
    public ResponseEntity<?> countStar(@PathVariable("communityEditorNum") Long communityEditorNum,
            @RequestParam Long userNum) {
        try {
            Long starCount = communityEditorStarService.countStar(communityEditorNum, userNum);
            System.out.println("Star Count: " + starCount);
            return ResponseEntity.ok(starCount);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("error: " + e.getMessage());
        }
    }

    // 평균 별점 조회
    @GetMapping("/average-star/{communityEditorNum}")
    public ResponseEntity<?> getAverageStar(@PathVariable String communityEditorNum) {
        try {
            Double average = communityEditorStarService.calculateAverageStar(Long.parseLong(communityEditorNum));
            Long totalCount = communityEditorStarService.getTotlaStars(Long.parseLong(communityEditorNum));

            Map<String, Object> result = new HashMap<>();
            result.put("average", average != null ? average : 0.0);
            result.put("totalCount", totalCount);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/popularList")
    public ResponseEntity<?> getPopularEditors() {
        List<Map<String, Object>> popularEditors = communityEditorStarService.getPopularEditors();
        return ResponseEntity.ok(popularEditors);
    }

    // 총 별점 조회 (totalStars)
    @GetMapping("/totalstars/{communityEditorNum}")
    public ResponseEntity<?> getStarCount(@PathVariable Long communityEditorNum) {
        Long totalStars = communityEditorStarService.getTotlaStars(communityEditorNum);
        return ResponseEntity.ok(totalStars);
    }

    @GetMapping("/previous/{currentId}")
    public ResponseEntity<?> getPreviousPost(@PathVariable Long currentId) {
        Optional<Community_Editor> previousPost = communityEditorService.getPreviousPost(currentId);

        if (previousPost.isPresent()) {
            return ResponseEntity.ok(previousPost.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("이전글이 없습니다.");
        }
    }

    @GetMapping("/next/{currentId}")
    public ResponseEntity<?> getNextPost(@PathVariable Long currentId) {
        Optional<Community_Editor> nextPost = communityEditorService.getNextPost(currentId);

        if (nextPost.isPresent()) {
            return ResponseEntity.ok(nextPost.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("다음글이 없습니다.");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Community_Editor>> searchCommunityEditorTitles(
            @RequestParam("communityEditorTitle") String title) {
        List<Community_Editor> community_Editors = communityEditorService.searchCommunityEditorTitles(title);
        return ResponseEntity.ok(community_Editors);
    }
}
