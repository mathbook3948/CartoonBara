package com.jjangtrio.project1_back.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import com.jjangtrio.project1_back.entity.Qna;
import com.jjangtrio.project1_back.repository.QnaRepository;
import com.jjangtrio.project1_back.service.QnaService;
import com.jjangtrio.project1_back.vo.PageVO;
import com.jjangtrio.project1_back.vo.QnaVO;
import com.jjangtrio.project1_back.vo.Webtoon_ReviewVO;

@RestController
@RequestMapping("/api/qna")
public class QnaController {

    @Autowired
    private QnaService qnaService;

    @PostMapping("/test")
    public void test() {
        System.out.println("연결 성공");
    }

    // 업로드 디렉토리 경로 설정
    // private final Path uploadDir =
    // Paths.get("webapps/back/uploads").toAbsolutePath().normalize(); // 배포용
    private final Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");

    public QnaController() {
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    // Create(QnA_Form)
    @PostMapping
    public ResponseEntity<?> createQna(@ModelAttribute QnaVO qna, MultipartFile image) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long user_num = Long.valueOf(auth.getPrincipal().toString());

        if (auth == null || auth.getPrincipal() == null || user_num == null) {
            throw new IllegalArgumentException("로그인 정보가 누락되었습니다.");
        }

        if (image instanceof MultipartFile) {
            if (image != null) {

                // 업로드 경로가 유효한지 확인
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);// 업로드 디렉토리가 없으면 생성
                }
                // 원본 파일 이름 가져오기
                String fileName = image.getOriginalFilename();
                // 중복 방지를 위한 고유 파일 이름 생성
                String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
                // 파일 저장 경로 설정
                Path destinationFile = uploadDir.resolve(uniqueFileName).normalize();
                System.out.println("Serving file from:" + destinationFile.toAbsolutePath().toString()); // 디버깅용 로그 출력
                // 파일 복사
                Files.copy(image.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
                qna.setQnaImage(uniqueFileName); // 원본 파일명를 디버깅으로 보내기(uniqueFileName);
            }
        } else {
            System.out.println("imageObject is null");
            // 처리할 로직 (예: 예외 처리)
        }
        if (qnaService.saveQna(qna, user_num)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("글 등록이 완료되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("글 등록 중 오류가 발생하였습니다.");
        }
    }

    // Read(QnA_List)
    @GetMapping
    public List<Qna> getQnaList() {
        return qnaService.getQnaList();
    }

    // Read(QnA_Detail)
    @GetMapping("/{id}")
    public ResponseEntity<Qna> getQnaById(@PathVariable Long id) {
        return qnaService.getQnaByQnaNum(id)
                .map(qna -> ResponseEntity.ok().body(qna))
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete(QnA_Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQna(@PathVariable Long id) {
        qnaService.deleteQna(id);
        return ResponseEntity.ok().body("삭제 완료");
    }

    @GetMapping("/getpage")
    public ResponseEntity<?> getLatestList(@RequestParam("page") int page) {
        try {
            Map<String, Object> result = new HashMap<>();

            Long count = qnaService.countByQnaNUm();

            PageVO pvo = PageVO.builder()
                    .currentPage(page)
                    .pageSize(8)
                    .totalRecords(count.intValue())
                    .totalPages((int) Math.ceil((double) count / 8)) // 올림 처리
                    .startIndex((page - 1) * 8 + 1) // 시작 인덱스 계산
                    .endIndex(Math.min(page * 8, count.intValue())) // 끝 인덱스 계산 (pageSize 사용)
                    .hasPrevPage(page > 1) // 이전 페이지 여부
                    .hasNextPage(page < (int) Math.ceil((double) count / 5)) // 다음 페이지 여부
                    .build();

            result.put("PageVO", pvo);
            result.put("qna", qnaService.getQna(pvo));

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("실패: " + e.getMessage());
        }
    }


    @GetMapping("/search")
    public ResponseEntity<List<Qna>> searchQnaTitles(@RequestParam("qnaTitle") String QnaTitle) {
        List<Qna> Qna = qnaService.searchQnaTitles(QnaTitle);
        return ResponseEntity.ok(Qna);
    }

    @PostMapping("/oneByoneQnA")
    public ResponseEntity<?> getQnaByUserNum(@RequestParam(value = "page", defaultValue = "1") int page) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long user_num = Long.valueOf(auth.getPrincipal().toString());
        try {
            Map<String, Object> result = new HashMap<>();
            Long count = qnaService.getQnaCount(user_num);
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
            List<QnaVO> getAllList = qnaService.getQnaList(pvo, user_num);
            result.put("getMyReviewList", getAllList);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("실패: " + e.getMessage());
        }
    }

}
