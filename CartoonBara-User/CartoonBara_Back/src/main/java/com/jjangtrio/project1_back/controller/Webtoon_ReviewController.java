package com.jjangtrio.project1_back.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjangtrio.project1_back.entity.Webtoon_Review;
import com.jjangtrio.project1_back.service.Webtoon_ReviewService;
import com.jjangtrio.project1_back.service.Webtoon_Review_LikeService;
import com.jjangtrio.project1_back.vo.PageVO;
import com.jjangtrio.project1_back.vo.Webtoon_ReviewVO;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/webtoon/review")
@CrossOrigin("*")
public class Webtoon_ReviewController {

    @Autowired
    private Webtoon_ReviewService webtoon_ReviewService;

    @Autowired
    private Webtoon_Review_LikeService webtoon_Review_LikeService;

    // 회원 번호와 웹툰 아이디로 웹툰 리뷰 입력
    @PostMapping("/reviewadd")
    public Webtoon_Review addWebtoon_Review(@RequestBody Map<String, Object> map,
            HttpServletRequest request) {
        // IP 주소 추출
        String ip = request.getRemoteAddr();
        map.put("webtoonReviewIp", ip);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userNum = Long.valueOf(auth.getPrincipal().toString());
        map.put("userNum", userNum);

        System.out.println("userNum : " + Long.parseLong(map.get("userNum").toString()));
        System.out.println("webtoonReviewReview : " + map.get("webtoonReviewReview"));
        System.out.println("webtoonId : " + map.get("webtoonId"));
        System.out.println("userNickname : " + map.get("userNickname"));
        System.out.println("webtoonReviewIp : " + map.get("webtoonReviewIp"));

        // Webtoon_Review 객체 생성 서비스로 전달
        return webtoon_ReviewService.addWebtoon_Review(map);
    }

    // 웹툰 고유넘버로 웹툰리스트 뽑기
    @GetMapping("/reviewlist/{webtoonId}")
    public List<Map<String, Object>> getReviewsByWebtoonId(@PathVariable("webtoonId") Long webtoonId) {
        // 요청 파라미터로 웹툰 ID를 받아와서 Map에 넣음
        Map<String, Object> params = Map.of("webtoonId", webtoonId);
        // 서비스에서 리뷰 목록을 가져옴
        return webtoon_ReviewService.getReviewsByWebtoonId(params);
    }

    // 웹툰 리뷰 삭제
    @GetMapping("/reviewdelete")
    public ResponseEntity<?> deleteWebtoon_Review(@RequestParam("webtoonReviewId") Long webtoonReviewId) {
        try {
            if (webtoonReviewId != null) {
                webtoon_ReviewService.deleteWebtoonReview(webtoonReviewId);
                System.out.println("웹툰 리뷰가 삭제되었습니다.");
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body("삭제 실패");
        }
    }

    // 웹툰 리뷰수정
    @PostMapping("/reviewupdate")
    public ResponseEntity<?> updateWebtoonReview(
            @RequestBody Map<String, Object> map,
            HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userNum = Long.parseLong(auth.getPrincipal().toString());
        String webtoonReivewIp = request.getRemoteAddr();
        Long webtoonReviewNum = Long.parseLong(map.get("webtoonReviewNum").toString());
        Long webtoonId = Long.parseLong(map.get("webtoonId").toString());
        String webtoonReviewReivew = map.get("webtoonReviewReview").toString();

        System.out.println("userNum : " + userNum);
        System.out.println("webtoonReviewNum : " + webtoonReviewNum);
        System.out.println("webtoonId : " + webtoonId);
        System.out.println("webtoonReviewReivew : " + webtoonReviewReivew);
        System.out.println("webtoonReivewIp : " + webtoonReivewIp);

        // 웹툰아이디를 Long으로 변환
        webtoon_ReviewService.updateWebtoonReview(userNum, webtoonId, webtoonReviewNum, webtoonReviewReivew,
                webtoonReivewIp);
        return ResponseEntity.ok("웹툰 리뷰수정 완료");
    }

    // 마이페이지용 리뷰 리스트
    @GetMapping("/otherReview")
    public ResponseEntity<?> getReviewsByUserNum(@RequestParam(value = "page", defaultValue = "1") int page) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long user_num = Long.valueOf(auth.getPrincipal().toString());
        try {
            Map<String, Object> result = new HashMap<>();
            Long count = webtoon_ReviewService.getReivewCount(user_num);
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
            List<Webtoon_ReviewVO> getAllList = webtoon_ReviewService.getMyReviewList(pvo, user_num);
            result.put("getMyReviewList", getAllList);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("실패: " + e.getMessage());
        }
    }

    // 아더페이지 프로필용 리뷰리스트
    @GetMapping("/otherReview/{userNum}")
    public ResponseEntity<?> getReviewListByUserNum(@RequestParam(value = "page", defaultValue = "1") int page,
            @PathVariable("userNum") Long userNum) {

        try {
            Map<String, Object> result = new HashMap<>();
            Long count = webtoon_ReviewService.getReviewCount(-1L);
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
            result.put("otherWebtoonReviewReviewList", webtoon_ReviewService.getReviewList(pvo, userNum));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("실패: " + e.getMessage());
        }
    }

    @GetMapping("/likeGood")
    public ResponseEntity<?> likeGood(@RequestParam Long webtoonReviewNum) {
        if (webtoonReviewNum == null) {
            return ResponseEntity.status(400).body("webtoonReviewNum is required");
        }
        return ResponseEntity.ok(webtoon_Review_LikeService.getWebtoonReviewLikeCount(webtoonReviewNum));
    }

    @GetMapping("/likeBad")
    public ResponseEntity<?> likeBad(@RequestParam Long webtoonReviewNum) {
        if (webtoonReviewNum == null) {
            return ResponseEntity.status(400).body("webtoonReviewNum is required");
        }
        return ResponseEntity.ok(webtoon_Review_LikeService.getWebtoonReviewDisLikeCount(webtoonReviewNum));
    }

    @PostMapping("/addGoodLike")
    public ResponseEntity<?> addGoodLike(@RequestParam Long webtoonReviewNum) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = auth.getPrincipal().toString();

        Long userNum = null;

        // 사용자 정보가 숫자로 되어 있는지 확인
        try {
            userNum = Long.parseLong(principal);
        } catch (NumberFormatException e) {
            // "anonymousUser"와 같은 값이 들어온 경우 처리
            if ("anonymousUser".equals(principal)) {
                return ResponseEntity.status(400).body("Anonymous users cannot like reviews");
            } else {
                return ResponseEntity.status(500).body("Invalid user ID");
            }
        }

        if (webtoonReviewNum == null) {
            return ResponseEntity.status(400).body("webtoonReviewNum is required");
        }

        try {
            boolean checkLike = webtoon_Review_LikeService.addWebtoonReviewGoodLike(userNum, webtoonReviewNum);
            return ResponseEntity.ok(checkLike);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/addBadLike")
    public ResponseEntity<?> addBadLike(@RequestParam Long webtoonReviewNum) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String principal = auth.getPrincipal().toString();

        Long userNum = null;

        // 사용자 정보가 숫자로 되어 있는지 확인
        try {
            userNum = Long.parseLong(principal);
        } catch (NumberFormatException e) {
            // "anonymousUser"와 같은 값이 들어온 경우 처리
            if ("anonymousUser".equals(principal)) {
                return ResponseEntity.status(400).body("Anonymous users cannot dislike reviews");
            } else {
                return ResponseEntity.status(500).body("Invalid user ID");
            }
        }

        if (webtoonReviewNum == null) {
            return ResponseEntity.status(400).body("webtoonReviewNum is required");
        }

        try {
            boolean checkDislike = webtoon_Review_LikeService.addWebtoonReviewBadLike(userNum, webtoonReviewNum);
            return ResponseEntity.ok(checkDislike);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // webtoonReviewNum으로 리뷰 조회
    @GetMapping("/review/{webtoonReviewNum}")
    public ResponseEntity<?> getWebtoonReviewByNum(@PathVariable("webtoonReviewNum") Long webtoonReviewNum) {
        return ResponseEntity.ok(webtoon_ReviewService.getWebtoonReviewByNum(webtoonReviewNum));
    }

    // getWebtoonReviewAndWebtoonReviewLikeList
    @PostMapping("/webtoonReviewLikeList/{webtoonId}")
    public ResponseEntity<?> webtoonReviewLikeList(@PathVariable("webtoonId") Long webtoonId) {

        try {
            return ResponseEntity.ok(webtoon_ReviewService.getWebtoonReviewAndWebtoonReviewLikeList(webtoonId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // 최신순 페이징 처리
    @GetMapping("/listdetail/{webtoonId}")
    public ResponseEntity<?> getlatesList(@PathVariable("webtoonId") Long webtoonId, @RequestParam("page") int page) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 웹툰 리뷰 총 개수 가져오기
            Long count = webtoon_ReviewService.getWebtoonIdCount(webtoonId); // webtoonId 포함

            // PageVO 생성
            int pageSize = 20; // 한 페이지에 표시할 리뷰 수
            int totalPages = (int) Math.ceil((double) count / pageSize); // 전체 페이지 수 계산

            PageVO pvo = PageVO.builder()
                    .currentPage(page) // 현재 페이지
                    .pageSize(pageSize) // 한 페이지에 표시할 리뷰 수
                    .totalRecords(count.intValue()) // 총 리뷰 개수
                    .totalPages(totalPages) // 전체 페이지 수
                    .startIndex((page - 1) * pageSize + 1) // 시작 인덱스 (1부터 시작)
                    .endIndex(Math.min(page * pageSize, count.intValue())) // 끝 인덱스 (끝값이 총 개수를 넘지 않도록)
                    .hasPrevPage(page > 1) // 이전 페이지 여부
                    .hasNextPage(page < totalPages) // 다음 페이지 여부
                    .build();

            // 결과 데이터 설정
            result.put("WebtoonReviewPage", pvo);
            result.put("webtoonReviewAndLikeList", webtoon_ReviewService.getWebtoon_Reviews(pvo, webtoonId));
            result.put("webtoonId", webtoonId);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("실패: " + e.getMessage());
        }
    }

    // 좋아요 포함 리뷰 페이징처리
    @PostMapping("/listdetailLike/{webtoonId}")
    public ResponseEntity<?> getLikelatesList(@PathVariable("webtoonId") Long webtoonId,
            @RequestParam("page") int page) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 웹툰 리뷰 총 개수 가져오기
            Long count = webtoon_ReviewService.getWebtoonIdCount(webtoonId); // webtoonId 포함

            // PageVO 생성
            int pageSize = 20; // 한 페이지에 표시할 리뷰 수
            int totalPages = (int) Math.ceil((double) count / pageSize); // 전체 페이지 수 계산

            PageVO pvo = PageVO.builder()
                    .currentPage(page) // 현재 페이지
                    .pageSize(pageSize) // 한 페이지에 표시할 리뷰 수
                    .totalRecords(count.intValue()) // 총 리뷰 개수
                    .totalPages(totalPages) // 전체 페이지 수
                    .startIndex((page - 1) * pageSize + 1) // 시작 인덱스 (1부터 시작)
                    .endIndex(Math.min(page * pageSize, count.intValue())) // 끝 인덱스 (끝값이 총 개수를 넘지 않도록)
                    .hasPrevPage(page > 1) // 이전 페이지 여부
                    .hasNextPage(page < totalPages) // 다음 페이지 여부
                    .build();

            // 결과 데이터 설정
            result.put("WebtoonReviewPage", pvo);
            result.put("webtoonReviewAndLikeList", webtoon_ReviewService.getWebtoon_ReviewsLike(pvo, webtoonId));
            result.put("webtoonId", webtoonId);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("실패: " + e.getMessage());
        }
    }

    @GetMapping("/getReviewLike")
    public ResponseEntity<?> getLikedReviews(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long user_num = Long.valueOf(auth.getPrincipal().toString());
        System.out.println("getReviewLike 호출됨, user_num: " + user_num);

        try {
            List<Webtoon_Review> getAllList;

            // 좋아요한 웹툰 리뷰 필터링
            if (user_num != null) {
                getAllList = webtoon_ReviewService.findLikedWebtoonsByUser(user_num);
            } else {
                getAllList = webtoon_ReviewService.findAll();
            }

            // 페이징 계산
            int totalRecords = getAllList.size();
            int totalPages = (totalRecords + size - 1) / size; // 올림 계산
            int currentPage = Math.min(page, totalPages); // 요청된 페이지가 총 페이지 수를 초과하지 않도록
            int start = Math.max(0, (currentPage - 1) * size);
            int end = Math.min(start + size, totalRecords);

            List<Webtoon_Review> paginatedList = getAllList.subList(start, end);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            List<Map<String, Object>> result = new ArrayList<>();
            for (Webtoon_Review webtoonReview : paginatedList) {
                Map<String, Object> reviewData = new HashMap<>();
                reviewData.put("webtoonTitle", webtoonReview.getWebtoon().getWebtoonTitle());
                reviewData.put("userNickname", webtoonReview.getUser().getUserNickname());
                reviewData.put("webtoonReviewReview", webtoonReview.getWebtoonReviewReview());
                reviewData.put("webtoonReviewDate", sdf.format(webtoonReview.getWebtoonReviewDate()).toString());
                // reviewData.put("webtoonId",
                // webtoonReview.getWebtoon().getWebtoonId()).toString();
                // reviewData.put("webtoonReviewNum",
                // webtoonReview.getWebtoonReviewNum().toString());
                result.add(reviewData);
            }

            PageVO pageVO = new PageVO(currentPage, size, totalRecords, totalPages, start + 1, end, currentPage > 1,
                    currentPage < totalPages);

            Map<String, Object> response = new HashMap<>();
            response.put("page", pageVO);
            response.put("reviews", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("오류 발생: " + e.getMessage());
            return ResponseEntity.status(500).body("실패: " + e.getMessage());
        }
    }

    // 좋아요 포함 리뷰 페이징처리
    @GetMapping("/listdetailLikePage/{webtoonId}")
    public ResponseEntity<?> getLikelatesListPage(@PathVariable("webtoonId") Long webtoonId,
            @RequestParam("page") int page) {
        try {
            Map<String, Object> result = new HashMap<>();

            // 웹툰 리뷰 총 개수 가져오기
            Long count = webtoon_ReviewService.getWebtoonIdCount(webtoonId); // webtoonId 포함

            // PageVO 생성
            int pageSize = 20; // 한 페이지에 표시할 리뷰 수
            int totalPages = (int) Math.ceil((double) count / pageSize); // 전체 페이지 수 계산

            PageVO pvo = PageVO.builder()
                    .currentPage(page) // 현재 페이지
                    .pageSize(pageSize) // 한 페이지에 표시할 리뷰 수
                    .totalRecords(count.intValue()) // 총 리뷰 개수
                    .totalPages(totalPages) // 전체 페이지 수
                    .startIndex((page - 1) * pageSize + 1) // 시작 인덱스 (1부터 시작)
                    .endIndex(Math.min(page * pageSize, count.intValue())) // 끝 인덱스 (끝값이 총 개수를 넘지 않도록)
                    .hasPrevPage(page > 1) // 이전 페이지 여부
                    .hasNextPage(page < totalPages) // 다음 페이지 여부
                    .build();

            // 결과 데이터 설정
            result.put("WebtoonReviewPage", pvo);
            result.put("webtoonReviewAndLikeList", webtoon_ReviewService.getWebtoon_ReviewsLike(pvo, webtoonId));
            result.put("webtoonId", webtoonId);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("실패: " + e.getMessage());
        }
    }
}
