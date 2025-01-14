package com.jjangtrio.project1_back.controller.test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjangtrio.project1_back.entity.Webtoon;
import com.jjangtrio.project1_back.entity.Webtoon_Authors;
import com.jjangtrio.project1_back.entity.Webtoon_Authors_List;
import com.jjangtrio.project1_back.entity.Webtoon_Search;
import com.jjangtrio.project1_back.repository.WebtoonRepository;
import com.jjangtrio.project1_back.repository.Webtoon_AuthorsRepository;
import com.jjangtrio.project1_back.repository.Webtoon_Authors_ListRepository;
import com.jjangtrio.project1_back.repository.Webtoon_SearchRepository;

import hangul.hangul.HangulConverter;

@RestController
@RequestMapping("/add")
public class WebtoonAddController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebtoonRepository webtoonRepository;

    @Autowired
    private Webtoon_SearchRepository webtoon_SearchRepository;

    @Autowired
    private Webtoon_Authors_ListRepository webtoon_Authors_ListRepository;

    @Autowired
    private Webtoon_AuthorsRepository webtoon_AuthorsRepository;

    @GetMapping("/webtoon")
    @Transactional
    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<String> add() {
        try {
            int page = 1;

            while (true) {
                Map<String, Object> map = getWebtoonList(page);
                System.out.println("==================================================");
                List<Map<String, Object>> webtoons = (List<Map<String, Object>>) map.get("webtoons");
                for (Map<String, Object> webtoon : webtoons) {
                    Long webtoon_id = Integer
                            .toUnsignedLong(Integer.parseInt(((String) webtoon.get("id")).substring(6)));
                    if (containsWebtoon(webtoon_id)) {
                        continue;
                    }

                    // ===================================================새로운 웹툰일 경우 처리
                    String title = (String) webtoon.get("title");
                    String url = ((List<String>) webtoon.get("thumbnail")).get(0);
                    Long isend = (Boolean) webtoon.get("isEnd") ? 1L : 0L;
                    String desc = getDesc(webtoon_id);
                    Webtoon webtoonEntity = Webtoon.builder().webtoonId(webtoon_id).webtoonTitle(title)
                            .webtoonDesc(desc).webtoonIsEnd(isend).webtoonUrl(url).build();

                    Webtoon savedWebtoon = saveWebtoon(webtoonEntity);
                    webtoonRepository.flush();
                    System.out.println("webtoon_id => " + savedWebtoon.getWebtoonId());

                    if (!webtoonRepository.existsById(savedWebtoon.getWebtoonId())) {
                        throw new RuntimeException("Webtoon 저장 실패!");
                    }

                    List<String> authors = (List<String>) webtoon.get("authors");

                    saveWebtoonAuthors(authors, savedWebtoon);

                }
                if ((Boolean) map.get("isLastPage")) {
                    break;
                }
                page++;
            }
            return ResponseEntity.ok("성공");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("실패: " + e.getMessage());
        }
    }

    @GetMapping("/webtoon_search")
    @Transactional
    public ResponseEntity<String> addWebtoon_Search() {
        try {
            List<Webtoon> webtoons = webtoonRepository.findAll();
            for (Webtoon webtoon : webtoons) {
                if (webtoon_SearchRepository.existsByWebtoonId(webtoon)) {
                    continue;
                }
                String[] titles = HangulConverter.seperateHangul(webtoon.getWebtoonTitle());
                StringBuilder result = new StringBuilder();
                for (String t : titles) {
                    if (HangulConverter.isHangul(t)) {
                        String[] choseong = HangulConverter.getChoseong(t);
                        for (String c : choseong) {
                            result.append(c);
                        }
                    } else {
                        result.append(t); // 초성이 아닌 부분은 그대로 추가
                    }
                    System.out.println("t => " + t);
                }
                System.out.println("result => " + result.toString());
                // 결과를 저장
                System.out.println(webtoon.getWebtoonTitle() + "=" + result.toString());
                webtoon_SearchRepository.save(new Webtoon_Search(null, webtoon, result.toString()));
            }
            return ResponseEntity.ok("성공");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("실패: " + e.getMessage());
        }
    }

    // ===============================================================
    private Map<String, Object> getWebtoonList(int page) throws IOException, InterruptedException {
        // HttpClient 타임아웃 설정
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20)) // 연결 타임아웃 설정
                .build();

        String url = "https://korea-webtoon-api-cc7dda2f0d77.herokuapp.com/webtoons?provider=NAVER&page=" + page
                + "&perPage=100&sort=ASC";
        System.out.println("url => " + url);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();

        int retryCount = 3; // 최대 재시도 횟수
        while (retryCount > 0) {
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Map<String, Object> map = objectMapper.readValue(response.body(), Map.class);
                return map;
            } catch (IOException | InterruptedException e) {
                retryCount--;
                if (retryCount == 0) {
                    throw new RuntimeException("최대 재시도 횟수를 초과했습니다. " + e.getMessage());
                }
                Thread.sleep(1000); // 1초 대기 후 재시도
            }
        }
        return null;
    }

    private boolean containsWebtoon(Long webtoon_id) {
        return webtoonRepository.existsById(webtoon_id);
    }

    private boolean containsAuthor(String author) {
        return webtoon_Authors_ListRepository.existsByWebtoonAuthor(author);
    }

    private Webtoon saveWebtoon(Webtoon webtoon) {
        Webtoon savedWebtoon = webtoonRepository.save(webtoon);
        webtoonRepository.flush(); // 데이터베이스에 즉시 반영
        return savedWebtoon;
    }

    private void saveWebtoonAuthors(List<String> authors, Webtoon webtoon) {
        // webtoon이 저장되었는지 확인한 후, authors 저장
        if (webtoonRepository.existsById(webtoon.getWebtoonId())) {
            for (String author : authors) {
                Webtoon_Authors_List webtoonAuthorsList = findOrSaveAuthor(author);
                webtoon_AuthorsRepository.save(Webtoon_Authors.builder()
                        .webtoon(webtoon)
                        .webtoonAuthorsListNum(webtoonAuthorsList)
                        .build());
            }
        } else {
            throw new RuntimeException("Webtoon이 저장되지 않았습니다.");
        }
    }

    private Webtoon_Authors_List findOrSaveAuthor(String author) {
        Webtoon_Authors_List authorList = webtoon_Authors_ListRepository.findByWebtoonAuthor(author);
        if (authorList == null) {
            authorList = saveWebtoonAuthors(author); // 없으면 새로 저장
        }
        return authorList;
    }

    private Webtoon_Authors_List saveWebtoonAuthors(String author) {
        return webtoon_Authors_ListRepository.save(Webtoon_Authors_List.builder().webtoonAuthor(author).build());
    }

    private String getDesc(Long webtoon_id) throws IOException, InterruptedException {
        String url = "https://comic.naver.com/webtoon/list?titleId=" + webtoon_id;
        System.out.println("url => " + url);
        try {
            org.jsoup.nodes.Document doc = Jsoup.connect(url).timeout(10000).get();
            Element metaTag = doc.select("meta[property=og:description]").first();
            if (metaTag != null) {
                return metaTag.attr("content");
            } else {
                return "웹툰 설명이 없습니다";
            }
        } catch (Exception e) {
            return "웹툰 설명이 없습니다";
        }
    }
}
