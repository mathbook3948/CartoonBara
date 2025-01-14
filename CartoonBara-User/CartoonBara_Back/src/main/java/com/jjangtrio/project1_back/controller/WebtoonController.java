package com.jjangtrio.project1_back.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjangtrio.project1_back.entity.Log;
import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.entity.Webtoon;
import com.jjangtrio.project1_back.repository.LogRepository;
import com.jjangtrio.project1_back.service.WebtoonService;
import com.jjangtrio.project1_back.vo.PageVO;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/webtoon")
public class WebtoonController {

    @Autowired
    private WebtoonService webtoonService;

    @Autowired
    private LogRepository logRepository;

    @GetMapping("/detail")
public ResponseEntity<?> getWebtoonDetail(@RequestParam("webtoon_id") Long webtoon_id, HttpServletRequest request) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long user_num = null;

        try {
            user_num = Long.valueOf(auth.getPrincipal().toString());
        } catch (NumberFormatException e) {
            // principal이 숫자로 변환되지 않는 경우 처리
            user_num = null;
        }

    try {
        logRepository.save(
            Log.builder()
                .webtoon(Webtoon.builder().webtoonId(webtoon_id).build())
                .logIp(request.getRemoteAddr())
                .user(user_num != null ? User.builder().userNum(user_num).build() : null) // user_num이 null이면 User도 null
                .logDate(new Date())
                .build()
        );

        // 결과 반환
        Map<String, Object> result = new HashMap<>();
        Webtoon webtoon = webtoonService.getWebtoon(webtoon_id);
        result.put("title", webtoon.getWebtoonTitle());
        result.put("id", webtoon.getWebtoonId());
        result.put("desc", webtoon.getWebtoonDesc());
        result.put("img", webtoon.getWebtoonUrl());
        List<String> authors = webtoonService.getWebtoonAuthors(webtoon);
        result.put("authors", authors);
        result.put("like", webtoonService.getWebtoon_LikeCount(webtoon_id));
        result.put("dislike", webtoonService.getWebtoon_DislikeCount(webtoon_id));

        return ResponseEntity.ok(result);
    } catch (Exception e) {
        return ResponseEntity.status(500).body("실패: " + e.getMessage());
    }
}

    @GetMapping("/latestList")
    public ResponseEntity<?> getLatestList(@RequestParam("page") int page, @RequestParam("tagNum") Long tagNum) {
        try {
            Map<String, Object> result = new HashMap<>();

            Long count = 0L;
            if(tagNum == 0) {
                count = webtoonService.getWebtoonCount();
            } else {
                count = webtoonService.getWebtoonCountWithCategory(tagNum);
            }

            PageVO pvo = PageVO.builder()
                    .currentPage(page)
                    .pageSize(30)
                    .totalRecords(count.intValue())
                    .totalPages((int) Math.ceil((double) count / 30)) // 올림 처리
                    .startIndex((page - 1) * 30 + 1) // 시작 인덱스 계산
                    .endIndex(Math.min(page * 30, count.intValue())) // 끝 인덱스 계산 (pageSize 사용)
                    .hasPrevPage(page > 1) // 이전 페이지 여부
                    .hasNextPage(page < (int) Math.ceil((double) count / 30)) // 다음 페이지 여부
                    .build();

            result.put("PageVO", pvo);
            result.put("webtoons", webtoonService.getWebtoons(pvo, tagNum));

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("실패: " + e.getMessage());
        }
    }

    @GetMapping("/getLike")
    public ResponseEntity<?> likeCount(@RequestParam("webtoon_id") Long webtoon_id) {
        return ResponseEntity.ok(webtoonService.getWebtoon_LikeCount(webtoon_id));
    }

    @GetMapping("/getDislike")
    public ResponseEntity<?> dislikeCount(@RequestParam("webtoon_id") Long webtoon_id) {
        return ResponseEntity.ok(webtoonService.getWebtoon_DislikeCount(webtoon_id));
    }

    @GetMapping("/addLike") // 성공 여부를 boolean 형태로 반환
    public ResponseEntity<?> addOrDeleteLike(@RequestParam("webtoon_id") Long webtoon_id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long user_num = Long.parseLong(auth.getPrincipal().toString());
        if (webtoon_id == null) {
            System.out.println("webtoon_id 또는 user_id가 null입니다");
            return ResponseEntity.status(500).body(false);
        }
        System.out.println("webtoon_id : " + webtoon_id);
        System.out.println("user_num : " + user_num);
        try {
            boolean result = webtoonService.addOrDeleteLike(webtoon_id, user_num);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.println("오류가 발생하였습니다");
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/addDislike") // 성공 여부를 boolean 형태로 반환
    public ResponseEntity<?> addOrDeleteDislike(@RequestParam("webtoon_id") Long webtoon_id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long user_num = Long.valueOf(auth.getPrincipal().toString());
        if (webtoon_id == null) {
            System.out.println("webtoon_id 또는 user_id가 null입니다");
            return ResponseEntity.status(500).body(false);
        }
        System.out.println("webtoon_id : " + webtoon_id);
        System.out.println("user_num : " + user_num);
        try {
            boolean result = webtoonService.addOrDeleteDislike(webtoon_id, user_num);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.println("오류가 발생하였습니다");
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/{webtoonId}")
    public ResponseEntity<List<Webtoon>> getWebtoonsByWebtoonId(@RequestParam int webtoonId) {
        List<Webtoon> webtoons = webtoonService.getWebtoonsByWebtoonId(webtoonId);
        if (webtoons.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // 웹툰 리스트 반환
        return ResponseEntity.ok(webtoons);
    }

    @GetMapping("/tagList")
    public ResponseEntity<?> tagList(@RequestParam("webtoonId") Long webtoonId) {
        try {
            return ResponseEntity.ok(webtoonService.getTagList(webtoonId));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/tag/search")
    public ResponseEntity<?> tagSearch(@RequestParam("query") String query) {
        try {
            return ResponseEntity.ok(webtoonService.getTagSearchList(query));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/addTag")
    public ResponseEntity<?> addTag(@RequestBody Map<String, Object> map) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long user_num = Long.valueOf(auth.getPrincipal().toString());
            Long tagId = Long.valueOf(map.get("tagId").toString());
            Long webtoonId = Long.valueOf((map.get("webtoonId").toString()));
            boolean result = webtoonService.addTag(user_num, tagId, webtoonId);

            if (result) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(202).build();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/getManyLike")//7일동안 조회수 많은 내용 불러오기
    public ResponseEntity<?> getManyLike() {
        try {
            return ResponseEntity.ok(webtoonService.getManyList());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/author")
    public ResponseEntity<?> getAuthorDetail(@RequestParam("authorNum") Long authorNum) {
        try {
            return ResponseEntity.ok(webtoonService.getAuthorDetail(authorNum));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    @GetMapping("/getAllTag")
    public ResponseEntity<?> getAllTag() {
        try {
            return ResponseEntity.ok(webtoonService.getAllTag());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/getTop5Webtoon")
    public ResponseEntity<?> getTop5Webtoon() {
        try {

            return ResponseEntity.ok(webtoonService.getTop5Webtoon());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
