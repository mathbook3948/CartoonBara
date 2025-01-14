package com.jjangtrio.project1_back.controller;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jjangtrio.project1_back.entity.Webtoon;
import com.jjangtrio.project1_back.entity.Webtoon_Authors_List;
import com.jjangtrio.project1_back.entity.Webtoon_Search;
import com.jjangtrio.project1_back.service.WebtoonSearchService;
import com.jjangtrio.project1_back.service.WebtoonService;
import com.jjangtrio.project1_back.trie.Pair;
import com.jjangtrio.project1_back.trie.Trie;
import com.jjangtrio.project1_back.trie.TrieDeserializer;
import com.jjangtrio.project1_back.trie.TrieSerializer;

@RestController
@RequestMapping("/api/search")
@CrossOrigin("*")
public class WebtoonSearchController {

    @Autowired
    private WebtoonSearchService webtoonSearchService;

    @Autowired
    private WebtoonService webtoonService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static Trie webtoonTrie;
    private static Trie authorTrie;
    private static Trie choseongTrie;

    private static final int MAX_RESULTS = 5;
    private static final int CACHE_DURATION_SECONDS = 600;

    @GetMapping("/webtoon")
    @JsonSerialize(using = TrieSerializer.class)
    @JsonDeserialize(using = TrieDeserializer.class)
    public ResponseEntity<?> webtoonSearch(@RequestParam("query") String query) {
        try {
            if (query.isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>());
            }

            List<Pair<String, Long>> results;

            results = searchByFullText(query);

            if (results.size() < MAX_RESULTS) {
                List<Pair<String, Long>> emsi = searchByChoseong(query);

                for (Pair<String, Long> pair : emsi) {
                    if (results.size() == 5) {
                        break;
                    }
                    results.add(pair);
                }
            }

            return ResponseEntity.ok(results);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("검색 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private List<Pair<String, Long>> searchByFullText(String query) throws JsonProcessingException {
        webtoonTrie = loadTrieFromRedis("webtoonTrie");
        if (webtoonTrie == null) {
            webtoonTrie = createWebtoonTrie();
            saveTrieToRedis("webtoonTrie", webtoonTrie);
        }

        return webtoonTrie.getWordsWithPrefix(query).stream()
                .limit(MAX_RESULTS)
                .collect(Collectors.toList());
    }

    private List<Pair<String, Long>> searchByChoseong(String query) throws JsonProcessingException {
        choseongTrie = loadTrieFromRedis("choseongTrie");
        if (choseongTrie == null) {
            choseongTrie = createChoseongTrie();
            saveTrieToRedis("choseongTrie", choseongTrie);
        }

        List<Pair<String, Long>> results = choseongTrie.getWordsWithPrefix(query).stream()
                .limit(MAX_RESULTS)
                .collect(Collectors.toList());

        // Replace Choseong with actual webtoon titles
        results.forEach(pair -> {
            String webtoon_title =  webtoonService.findWebtoonTitleByWebtoon(pair.getSecond());
            pair.setFirst(webtoon_title);
        });

        return results;
    }

    private Trie loadTrieFromRedis(String key) {
        try {
            String value = redisTemplate.opsForValue().get(key);
            return value != null ? objectMapper.readValue(value, Trie.class) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private void saveTrieToRedis(String key, Trie trie) {
        try {
            String jsonString = objectMapper.writeValueAsString(trie);
            redisTemplate.opsForValue().set(key, jsonString,
                    Duration.ofSeconds(CACHE_DURATION_SECONDS));
        } catch (Exception e) {
        }
    }

    private Trie createWebtoonTrie() {
        Trie trie = new Trie();
        List<Webtoon> webtoons = webtoonSearchService.getAllWebtoons();
        webtoons.forEach(webtoon -> trie.insert(webtoon.getWebtoonTitle(), webtoon.getWebtoonId()));
        return trie;
    }

    private Trie createChoseongTrie() {
        Trie trie = new Trie();
        List<Webtoon_Search> searches = webtoonSearchService.getAllWebtoon_Searchs();
        searches.forEach(search -> trie.insert(search.getWebtoonTitleChoseong(),
                search.getWebtoonId().getWebtoonId()));
        return trie;
    }

    @GetMapping("/author")
    @JsonSerialize(using = TrieSerializer.class)
    @JsonDeserialize(using = TrieDeserializer.class)
    public ResponseEntity<?> authorSearch(@RequestParam("query") String query) {
        try {
            List<Pair<String, Long>> result = new ArrayList<>();

            if (query.equals("")) {
                return ResponseEntity.ok(result);
            }

            if (redisTemplate.hasKey("authorTrie")) {
                try {
                    authorTrie = objectMapper.readValue(redisTemplate.opsForValue().get("authorTrie"), Trie.class);
                } catch (Exception e) {
                    System.out.println("Redis에서 authorTrie 로드 실패: " + e.getMessage());
                }
            }

            List<Pair<String, Long>> emsi1 = new ArrayList<>();

            if (authorTrie == null) {
                authorTrie = new Trie();
                List<Webtoon_Authors_List> authors = webtoonSearchService.getAllAuthors();
                for (Webtoon_Authors_List author : authors) {
                    authorTrie.insert(author.getWebtoonAuthor(), author.getWebtoonAuthorsListNum());
                }
                try {
                    String jsonString = objectMapper.writeValueAsString(authorTrie);
                    redisTemplate.opsForValue().set("authorTrie", jsonString, java.time.Duration.ofSeconds(600));
                } catch (Exception e) {
                    e.printStackTrace(); // 예외가 발생한 경우 출력
                }
            }

            emsi1 = authorTrie.getWordsWithPrefix(query);

            result = emsi1.stream()
                    .limit(5) // 5개까지만 선택
                    .collect(Collectors.toList()); // 리스트로 수집
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

}
