package com.jjangtrio.project1_back.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjangtrio.project1_back.entity.Log;
import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.entity.Webtoon;
import com.jjangtrio.project1_back.entity.Webtoon_Authors;
import com.jjangtrio.project1_back.entity.Webtoon_Like;
import com.jjangtrio.project1_back.entity.Webtoon_Tag;
import com.jjangtrio.project1_back.entity.Webtoon_Tag_List;
import com.jjangtrio.project1_back.repository.LogRepository;
import com.jjangtrio.project1_back.repository.UserRepository;
import com.jjangtrio.project1_back.repository.WebtoonRepository;
import com.jjangtrio.project1_back.repository.Webtoon_AuthorsRepository;
import com.jjangtrio.project1_back.repository.Webtoon_Authors_ListRepository;
import com.jjangtrio.project1_back.repository.Webtoon_LikeRepository;
import com.jjangtrio.project1_back.repository.Webtoon_SearchRepository;
import com.jjangtrio.project1_back.repository.Webtoon_TagRepository;
import com.jjangtrio.project1_back.repository.Webtoon_Tag_ListRepository;
import com.jjangtrio.project1_back.trie.Pair;
import com.jjangtrio.project1_back.trie.Trie;
import com.jjangtrio.project1_back.vo.PageVO;
import com.jjangtrio.project1_back.vo.WebtoonVO;

import jakarta.transaction.Transactional;

@Service
public class WebtoonService {

    @Autowired
    private WebtoonRepository webtoonRepository;

    @Autowired
    private Webtoon_AuthorsRepository webtoon_AuthorsRepository;

    @Autowired
    private Webtoon_Authors_ListRepository webtoon_Authors_ListRepository;

    @Autowired
    private Webtoon_LikeRepository webtoon_LikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Webtoon_Tag_ListRepository webtoon_Tag_ListRepository;

    @Autowired
    private Webtoon_TagRepository webtoon_TagRepository;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Webtoon_SearchRepository webtoon_SearchRepository;


    public Webtoon getWebtoon(@Param("webtoon_id") Long webtoon_id) {

        Webtoon webtoon = webtoonRepository.findById(webtoon_id)
                .orElseThrow(() -> new RuntimeException("해당 번호의 웹툰이 없습니다"));
        return webtoon;
    }

    public List<String> getWebtoonAuthors(Webtoon webtoon) {
        List<Webtoon_Authors> webtoon_Authors = webtoon_AuthorsRepository.findByWebtoon(webtoon);

        List<String> authors = new ArrayList<>();
        for (Webtoon_Authors author : webtoon_Authors) {
            authors.add(author.getWebtoonAuthorsListNum().getWebtoonAuthor());
        }
        return authors;
    }

    public Long getWebtoonCount() {

        return webtoonRepository.count();
    }

    public List<WebtoonVO> getWebtoons(PageVO pvo, Long tagNum) {
        System.out.println("tagNum =>" + tagNum);
        return (tagNum != 0L ? webtoonRepository.findWebtoonsWithinRangeWithCategory(pvo.getStartIndex(), pvo.getEndIndex(), tagNum) : webtoonRepository.findWebtoonsWithinRange(pvo.getStartIndex(), pvo.getEndIndex())).stream()
                .map(obj -> {
                    Integer webtoonId = obj[0] != null ? Integer.parseInt(obj[0].toString()) : null;
                    String webtoonTitle = obj[1] != null ? obj[1].toString() : null;
                    String webtoonDesc = obj[2] != null ? obj[2].toString() : null;
                    Integer webtoonIsend = obj[3] != null ? Integer.parseInt(obj[3].toString()) : null;
                    String webtoonUrl = obj[4] != null ? obj[4].toString() : null;

                    return WebtoonVO.builder()
                            .webtoonId(webtoonId)
                            .webtoonTitle(webtoonTitle)
                            .webtoonDesc(webtoonDesc)
                            .webtoonIsend(webtoonIsend)
                            .webtoonUrl(webtoonUrl)
                            .build();
                })
                .peek(vo -> System.out.println(vo))
                .toList();
    }

    public Long getWebtoon_LikeCount(Long webtoon_id) {
        return webtoon_LikeRepository.getLike(Webtoon.builder().webtoonId(webtoon_id).build());
    }

    public Long getWebtoon_DislikeCount(Long webtoon_id) {
        return webtoon_LikeRepository.getDislike(Webtoon.builder().webtoonId(webtoon_id).build());
    }

    @Transactional
    public boolean addOrDeleteLike(Long webtoon_id, Long user_id) {
        if (!webtoonRepository.existsById(webtoon_id) || !userRepository.existsById(user_id)) {
            return false;
        }
        Webtoon webtoon = webtoonRepository.findById(webtoon_id)
                .orElseThrow(() -> new RuntimeException("해당 웹툰이 없습니다"));
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("해당 사용자가 없습니다"));

        List<Webtoon_Like> existing = webtoon_LikeRepository.findByWebtoonAndUserAndWebtoonIsLike(webtoon, user);
        Webtoon_Like existingLike = null;

        if (!existing.isEmpty()) {
            if (existing.get(0).getWebtoonIsLike() == 1) {
                existingLike = existing.get(0);
            } else {
                return false;
            }
        }
        if (existingLike != null) {
            // 이미 좋아요가 존재하면 삭제
            webtoon_LikeRepository.delete(existingLike);
            return true;
        } else {
            // 좋아요 추가
            webtoon_LikeRepository.save(Webtoon_Like.builder()
                    .webtoon(webtoon)
                    .user(user)
                    .webtoonIsLike(1L)
                    .build());
            return true;
        }
    }

    @Transactional
    public boolean addOrDeleteDislike(Long webtoon_id, Long user_id) {
        if (!webtoonRepository.existsById(webtoon_id) || !userRepository.existsById(user_id)) {
            return false;
        }
        Webtoon webtoon = webtoonRepository.findById(webtoon_id)
                .orElseThrow(() -> new RuntimeException("해당 웹툰이 없습니다"));
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("해당 사용자가 없습니다"));

        List<Webtoon_Like> exsisting = webtoon_LikeRepository.findByWebtoonAndUserAndWebtoonIsLike(webtoon, user);
        Webtoon_Like existingDislike = null;
        if (!exsisting.isEmpty()) {
            if (exsisting.get(0).getWebtoonIsLike() == 1) {
                return false;
            } else {
                existingDislike = exsisting.get(0);
            }
        }
        if (existingDislike != null) {
            // 이미 싫어요가 존재하면 삭제
            webtoon_LikeRepository.delete(existingDislike);
            return true;
        } else {
            // 싫어요 추가
            webtoon_LikeRepository.save(Webtoon_Like.builder()
                    .webtoon(webtoon)
                    .user(user)
                    .webtoonIsLike(0L)
                    .build());
            return true;
        }
    }

    public List<Webtoon> getWebtoonsByWebtoonId(int webtoonId) {

        return webtoonRepository.findByWebtoonId(webtoonId);
    }

    public List<String> getTagList(Long webtoonId) {
        List<Webtoon_Tag> list = webtoon_TagRepository.findByWebtoon(Webtoon.builder().webtoonId(webtoonId).build());
        List<String> result = new ArrayList<>();
        for (Webtoon_Tag tag : list) {
            result.add(webtoon_Tag_ListRepository.findById(tag.getWebtoonTagListNum().getWebtoonTagListNum())
                    .orElseThrow(() -> new RuntimeException("알 수 없는 오류가 발생했습니다")).getWebtoonTagListTagn());
        }
        return result;
    }

    public List<Pair<String, Long>> getTagSearchList(String query) throws JsonProcessingException {
        return getOrMakeTagTrie().getWordsWithPrefix(query);
    }

    private Trie getOrMakeTagTrie() throws JsonProcessingException {
        if (redisTemplate.hasKey("tagTrie")) {
            Trie trie = objectMapper.readValue(redisTemplate.opsForValue().get("tagTrie").toString(), Trie.class);
            return trie;
        } else {
            Trie trie = new Trie();

            List<Webtoon_Tag_List> tagList = webtoon_Tag_ListRepository.findAll();

            for (Webtoon_Tag_List tag : tagList) {
                trie.insert(tag.getWebtoonTagListTagn(), tag.getWebtoonTagListNum());
            }
            redisTemplate.opsForValue().set("tagTrie", objectMapper.writeValueAsString(trie));
            return trie;
        }
    }

    public boolean addTag(Long userNum, Long tagId, Long webtoonId) {
        if (webtoon_TagRepository.findTags(Webtoon_Tag_List.builder().webtoonTagListNum(tagId).build(), Webtoon.builder().webtoonId(webtoonId).build()).size() > 0) {
            return false;
        }

        webtoon_TagRepository.save(Webtoon_Tag.builder().webtoonTagListNum(Webtoon_Tag_List.builder().webtoonTagListNum(tagId).build()).webtoon(Webtoon.builder().webtoonId(webtoonId).build()).user(User.builder().userNum(userNum).build()).build());
        return true;
    }

    public List<Map<Long, String>> getManyList() throws JsonProcessingException {
        List<Map<Long, String>> result;
        if (redisTemplate.hasKey("latestTopWebtoon")) {
            String json = (String) redisTemplate.opsForValue().get("latestTopWebtoon");
            result = objectMapper.readValue(json, new TypeReference<List<Map<Long, String>>>() {});
            return result;
        } else {
            result = new ArrayList<>();
    
            Date currentDate = new Date();
    
            // Calendar 객체 생성
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
    
            // 현재 날짜에서 7일을 빼기
            calendar.add(Calendar.DAY_OF_MONTH, -7);
    
            // 7일 전 날짜 가져오기
            Date sevenDaysAgo = calendar.getTime();
            List<Log> list = logRepository.findLogsFromLastSevenDays(sevenDaysAgo);
    
            Map<Long, Long> count = new HashMap<>(); // key가 webtoonId, Long은 조회수
    
            for (Log log : list) {
                count.put(log.getWebtoon().getWebtoonId(), count.getOrDefault(log.getWebtoon().getWebtoonId(), 0L) + 1);
            }
    
            List<Map.Entry<Long, Long>> entryList = new ArrayList<>(count.entrySet());
            entryList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
    
            // 상위 5개 항목 출력
            for (int i = 0; i < Math.min(5, entryList.size()); i++) {
                Map.Entry<Long, Long> entry = entryList.get(i);
                Map<Long, String> resultEmsi = new HashMap<>();
                Webtoon webtoon = webtoonRepository.findById(entry.getKey()).orElseThrow(() -> new RuntimeException("존재하지 않는 웹툰입니다"));
                resultEmsi.put(webtoon.getWebtoonId(), webtoon.getWebtoonUrl());
                result.add(resultEmsi);
            }
    
            String json = objectMapper.writeValueAsString(result);
            redisTemplate.opsForValue().set("latestTopWebtoon", json, 3600, TimeUnit.SECONDS);
            return result;
        }
    }

    public List<Map<String, Object>> getAuthorDetail(Long authorNum) {

        return webtoon_AuthorsRepository.getAuthorDetail(authorNum).stream()
        .map(obj -> {
            Long webtoonId = obj[0] != null ? ((BigDecimal) obj[0]).longValue() : null;
            String webtoonUrl = obj[1] != null ? (String) obj[1] : null;

            Map<String, Object> map = new HashMap<>();
            map.put("webtoonId",webtoonId);
            map.put("webtoonUrl", webtoonUrl);
            return map;
        })
        .peek(vo -> System.out.println(vo))
        .toList();
    }

    public Map<String, Long>  getAllTag() {
        List<Webtoon_Tag_List> tagList = webtoon_Tag_ListRepository.findAll();

        Map<String, Long> result = new HashMap<>();

        for(Webtoon_Tag_List tag : tagList) {
            result.put(tag.getWebtoonTagListTagn(), tag.getWebtoonTagListNum());
        }
        return result;
    }

    public Long getWebtoonCountWithCategory(Long category) {
        Long count = Long.valueOf(webtoonRepository.countByCategory(category).toString());

        return count;
    }

    public List<Map<String, Object>> getTop5Webtoon() {
        List<Map<String, Object>> result = new ArrayList<>();

        List<Object[]> top5Webtoon = webtoon_LikeRepository.findTop5ByOrderByViewCountDesc();

        for (Object[] webtoon : top5Webtoon) {
            Map<String, Object> map = new HashMap<>();
            map.put("webtoonId", ((BigDecimal) webtoon[0]).longValue());
            map.put("webtoonUrl", (String) webtoon[1]);
            result.add(map);
        }
        return result;
    }

    public String findWebtoonTitleByWebtoon(Long wevtoonId) {
        return webtoon_SearchRepository.findWebtoonTitleByWebtoon(wevtoonId);
    }
}