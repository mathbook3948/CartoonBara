package com.jjangtrio.project1_back.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.entity.Webtoon;
import com.jjangtrio.project1_back.entity.Webtoon_Review;
import com.jjangtrio.project1_back.entity.Webtoon_Review_Like;
import com.jjangtrio.project1_back.repository.PermissionRepository;
import com.jjangtrio.project1_back.repository.UserRepository;
import com.jjangtrio.project1_back.repository.Webtoon_ReviewRepository;
import com.jjangtrio.project1_back.repository.Webtoon_Review_LikeRepository;
import com.jjangtrio.project1_back.vo.PageVO;
import com.jjangtrio.project1_back.vo.UserVO;
import com.jjangtrio.project1_back.vo.WebtoonVO;
import com.jjangtrio.project1_back.vo.Webtoon_ReviewVO;

import io.lettuce.core.dynamic.annotation.Param;

@Service
public class Webtoon_ReviewService {

        @Autowired
        private Webtoon_ReviewRepository webtoon_ReviewRepository;

        @Autowired
        private Webtoon_Review_LikeRepository webtoon_Review_LikeRepository;

        // 회원 번호와 웹툰 아이디로 웹툰 리뷰 입력
        public Webtoon_Review addWebtoon_Review(Map<String, Object> map) {

                // Webtoon 객체 생성
                Webtoon webtoon = Webtoon.builder().webtoonId(Long.parseUnsignedLong(map.get("webtoonId").toString()))
                                .build();
                // User 객체 생성
                User user = User.builder().userNum(Long.parseUnsignedLong(map.get("userNum").toString())).build();
                // Webtoon_Review 엔티티 생성
                Webtoon_Review wrvo = Webtoon_Review
                                .builder()
                                .user(user)
                                .webtoon(webtoon)
                                .userNickname(map.get("userNickname") != null ? map.get("userNickname").toString()
                                                : null)
                                .webtoonReviewReview(map.get("webtoonReviewReview").toString())
                                .webtoonReviewDate(new Date())
                                .webtoonReviewIp(map.get("webtoonReviewIp").toString())
                                .build();
                // Repository에 저장
                return webtoon_ReviewRepository.save(wrvo);
        }

        public List<Map<String, Object>> getReviewsByWebtoonId(Map<String, Object> map) {

                // 웹툰 객체를 빌더 패턴으로 생성
                Webtoon webtoon = Webtoon.builder()
                                .webtoonId(Long.parseUnsignedLong(map.get("webtoonId").toString()))
                                .build();

                // webtoonId로 리뷰 목록을 조회
                List<Webtoon_Review> reviews = webtoon_ReviewRepository
                                .findByWebtoonWebtoonIdOrderByWebtoonReviewDateDesc(webtoon.getWebtoonId());

                // Map 형태로 리뷰 정보를 저장할 리스트
                List<Map<String, Object>> reviewMaps = new ArrayList<>();

                // 각 리뷰를 Map으로 변환하여 리스트에 추가
                for (Webtoon_Review review : reviews) {
                        Map<String, Object> reviewMap = new HashMap<>();
                        reviewMap.put("webtoonReviewNum", review.getWebtoonReviewNum());
                        reviewMap.put("userNickname", review.getUserNickname());
                        reviewMap.put("webtoonReviewReivew", review.getWebtoonReviewReview());

                        // Date형을 string yyyy-dd-mm-hh-mm-ss로 변환
                        String date = review.getWebtoonReviewDate().toString();
                        String[] dateArr = date.split(" ");
                        String[] dateArr2 = dateArr[0].split("-");
                        String[] dateArr3 = dateArr[1].split(":");
                        String dateStr = dateArr2[0] + "-" + dateArr2[1] + "-" + dateArr2[2] + " " + dateArr3[0] + ":"
                                        + dateArr3[1];

                        reviewMap.put("webtoonReviewDate", dateStr);
                        // 필요한 추가 필드를 Map에 넣을 수 있습니다.

                        reviewMaps.add(reviewMap);
                }

                // 리뷰 정보가 담긴 리스트 반환
                return reviewMaps;
        }

        public Long getWebtoonReviewCount() {
                return webtoon_ReviewRepository.count();
        }

        // 웹툰리뷰 삭제
        public void deleteWebtoonReview(Long webtoonReviewNum) {
                webtoon_ReviewRepository.deleteById(webtoonReviewNum);
        }

        // 웹툰리뷰수정
        public void updateWebtoonReview(Long userNum, Long webtoonId, Long webtoonReviewNum, String wetoonReviewReview,
                        String webtoonReviewIp) {
                try {
                        Webtoon_Review wrvo = webtoon_ReviewRepository.findById(webtoonReviewNum)
                                        .orElseThrow(() -> new RuntimeException("잘못된 리뷰 id"));
                        wrvo.setUser(User.builder().userNum(userNum).build());
                        wrvo.setWebtoon(Webtoon.builder().webtoonId(webtoonId).build());
                        wrvo.setWebtoonReviewReview(wetoonReviewReview);
                        wrvo.setWebtoonReviewIp(webtoonReviewIp);
                        webtoon_ReviewRepository.save(wrvo);
                } catch (Exception e) {
                        System.out.println(e.getMessage());
                }
        }

        // 웹툰 리뷰 리스트(마이페이지)
        public List<Map<String, Object>> getReviewsByUserNum(Long userNum) {
                List<Webtoon_Review> reviews = webtoon_ReviewRepository
                                .findByUser(User.builder().userNum(userNum).build());
                List<Map<String, Object>> reviewMaps = new ArrayList<>();
                for (Webtoon_Review review : reviews) {
                        Map<String, Object> map = new HashMap<>();
                        System.out.println(review.getWebtoonReviewReview());
                        map.put("webtoonReviewNum", review.getWebtoonReviewNum());
                        map.put("webtoonReviewReview", review.getWebtoonReviewReview());
                        map.put("webtoonTitle", review.getWebtoon().getWebtoonTitle());
                        map.put("webtoonId", review.getWebtoon().getWebtoonId().toString());
                        map.put("userNum", userNum);
                        map.put("webtoonId", review.getWebtoon().getWebtoonId().toString());
                        // Date 형식을 yyyy-MM-dd HH:mm:ss 형식으로 변환
                        String date = review.getWebtoonReviewDate().toString();
                        String[] dateArr = date.split(" ");
                        String[] dateArr2 = dateArr[0].split("-");
                        String[] dateArr3 = dateArr[1].split(":");
                        String dateStr = dateArr2[0] + "-" + dateArr2[1] + "-" + dateArr2[2] + " " + dateArr3[0] + ":"
                                        + dateArr3[1];
                        map.put("webtoonReviewDate", dateStr);
                        reviewMaps.add(map);
                }
                return reviewMaps;
        }

        // 웹툰 리뷰 리스트(아더페이지)
        public List<Map<String, Object>> getReviewListByUserNum(Long userNum) {
                List<Webtoon_Review> reviews = webtoon_ReviewRepository
                                .findByUser(User.builder().userNum(userNum).build());
                List<Map<String, Object>> reviewMaps = new ArrayList<>();
                for (Webtoon_Review review : reviews) {
                        Map<String, Object> map = new HashMap<>();
                        System.out.println(review.getWebtoonReviewReview());
                        map.put("webtoonReviewNum", review.getWebtoonReviewNum());
                        map.put("webtoonReviewReview", review.getWebtoonReviewReview());
                        map.put("webtoonTitle", review.getWebtoon().getWebtoonTitle());
                        map.put("webtoonId", review.getWebtoon().getWebtoonId().toString());
                        map.put("userNum", userNum);
                        map.put("webtoonId", review.getWebtoon().getWebtoonId().toString());
                        // Date 형식을 yyyy-MM-dd HH:mm:ss 형식으로 변환
                        String date = review.getWebtoonReviewDate().toString();
                        String[] dateArr = date.split(" ");
                        String[] dateArr2 = dateArr[0].split("-");
                        String[] dateArr3 = dateArr[1].split(":");
                        String dateStr = dateArr2[0] + "-" + dateArr2[1] + "-" + dateArr2[2] + " " + dateArr3[0] + ":"
                                        + dateArr3[1];
                        map.put("webtoonReviewDate", dateStr);
                        reviewMaps.add(map);
                }
                return reviewMaps;
        }
        // ================================================================================================================
        // 김채린이 추가함 오류나면 불러

        @Autowired
        private PermissionRepository permissionRepository;

        @Autowired
        private UserRepository userRepository;

        public Boolean updateSingoFlag(Map<String, Object> map) {
                Boolean result = false;

                Optional<Webtoon_Review> webtoonReview = webtoon_ReviewRepository
                                .findById(Long.parseLong(map.get("primarynumber").toString()));
                if (webtoonReview.isPresent()) {
                        User reported = User.builder().userNum(webtoonReview.get().getUser().getUserNum()).build();

                        if (webtoon_ReviewRepository.IsSingoFlagActive(
                                        Long.parseLong(map.get("primarynumber").toString())) != null) {
                                System.out.println(
                                                " =========================================> webtoon_Review | 이미 신고처리 됨");
                                result = true;
                        } else {
                                if (webtoon_ReviewRepository.updateSingoFlag(
                                                Long.parseLong(map.get("primarynumber").toString())) != null) {
                                        System.out.println(
                                                        "=======================================> webtoon_Review | 신고처리 완료");
                                        result = true;
                                }
                        }

                        if (reported != null) {
                                if (permissionRepository.IsSingoFlagActive(reported.getUserNum()) != null) {
                                        System.out.println(
                                                        "===========================================> User |  이미 신고처리 됨");
                                        result = true;
                                } else {
                                        if (permissionRepository.updateSingoFlag(reported.getUserNum()) != null) {
                                                if (userRepository.updateSingoAccumulated(
                                                                reported.getUserNum()) != null) {
                                                        System.out.println(
                                                                        "=======================================> User | 신고처리 완료");
                                                        result = true;
                                                }
                                        }
                                }
                        } else {
                                System.out.println("===========================================> User | 정보 없음");
                        }
                }
                return result;
        }

        // ================================================================================================================

        // webtoonReviewNum 으로 조회
        public Webtoon_Review getWebtoonReviewByNum(Long webtoonReviewNum) {
                return webtoon_ReviewRepository.findByWebtoonReviewNum(webtoonReviewNum);
        }

        //
        @SuppressWarnings("unchecked")
        public List<Map<String, Object>> getWebtoonReviewAndWebtoonReviewLikeList(Long webtoonReviewNumber) {

                return (List<Map<String, Object>>) webtoon_ReviewRepository
                                .getWebtoonReviewAndWebtoonReviewLikeList(webtoonReviewNumber).stream()
                                .map(obj -> {
                                        Long webtoonReviewNum = Long.valueOf(
                                                        obj[0] != null ? Long.parseLong(obj[0].toString()) : null);
                                        Long userNum = Long.valueOf(
                                                        obj[1] != null ? Long.parseLong(obj[1].toString()) : null);
                                        String userNickname = obj[2] != null ? obj[2].toString() : null;
                                        String webtoonReviewReview = obj[3] != null ? obj[3].toString() : null;
                                        Date webtoonReviewDate = obj[4] != null ? (Date) obj[4] : null;
                                        Long like_count = Long.valueOf(
                                                        obj[5] != null ? Long.parseLong(obj[5].toString()) : null);
                                        Long disLike_count = Long.valueOf(
                                                        obj[6] != null ? Long.parseLong(obj[6].toString()) : null);
                                        // Date를 yyyy-MM-dd HH:mm:ss 형식으로 변환
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        String formattedDate = sdf.format(webtoonReviewDate);

                                        Map<String, Object> map = new HashMap<>();
                                        map.put("webtoonReviewNum", webtoonReviewNum);
                                        map.put("userNum", userNum);
                                        map.put("userNickname", userNickname);
                                        map.put("webtoonReviewReview", webtoonReviewReview);
                                        map.put("webtoonReviewDate", formattedDate);
                                        map.put("like_count", like_count);
                                        map.put("disLike_count", disLike_count);
                                        return map;
                                })
                                .peek(vo -> System.out.println(vo))
                                .toList();
        }

        // 최신순 페이징 처리
        public List<Map<String, Object>> getWebtoon_Reviews(PageVO pvo, Long webtoonId) {
                List<Map<String, Object>> reviewVOs = new ArrayList<>();
                try {
                        // DB에서 페이징된 리뷰 목록을 가져오기
                        List<Object[]> reviews = webtoon_ReviewRepository
                                        .getWebtoonReviewAndWebtoonReviewLikeListWithPaging(webtoonId,
                                                        pvo.getStartIndex(), pvo.getEndIndex());

                        // 날짜 포맷터 설정
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        System.out.println(reviews.size());
                        if (reviews != null && !reviews.isEmpty()) {
                                for (Object[] obj : reviews) {
                                        Long webtoonReviewNum = obj[0] instanceof BigDecimal
                                                        ? ((BigDecimal) obj[0]).longValue()
                                                        : null;
                                        Long userNum = obj[1] instanceof BigDecimal ? ((BigDecimal) obj[1]).longValue()
                                                        : null;
                                        String userNickname = obj[2] instanceof String ? (String) obj[2] : null;
                                        String webtoonReviewReview = obj[3] instanceof String ? (String) obj[3] : null;
                                        Date webtoonReviewDate = obj[4] instanceof Date ? (Date) obj[4] : null;
                                        Long like_count = obj[5] instanceof BigDecimal
                                                        ? ((BigDecimal) obj[5]).longValue()
                                                        : null;
                                        Long disLike_count = obj[6] instanceof BigDecimal
                                                        ? ((BigDecimal) obj[6]).longValue()
                                                        : null;
                                        Long row_num = obj[7] instanceof BigDecimal ? ((BigDecimal) obj[7]).longValue()
                                                        : null;

                                        // 날짜가 null일 경우 처리
                                        String formattedDate = (webtoonReviewDate != null)
                                                        ? sdf.format(webtoonReviewDate)
                                                        : "";

                                        // Map에 데이터를 넣어서 반환
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("webtoonReviewNum", webtoonReviewNum);
                                        map.put("userNum", userNum);
                                        map.put("userNickname", userNickname);
                                        map.put("webtoonReviewReview", webtoonReviewReview);
                                        map.put("webtoonReviewDate", formattedDate); // 포맷된 날짜 문자열
                                        map.put("like_count", like_count);
                                        map.put("disLike_count", disLike_count);
                                        map.put("row_num", row_num);

                                        // 리스트에 추가
                                        reviewVOs.add(map);
                                }
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return reviewVOs;
        }

        // 좋아요순 페이징 처리
        public List<Map<String, Object>> getWebtoon_ReviewsLike(PageVO pvo, Long webtoonId) {
                List<Map<String, Object>> reviewVOs = new ArrayList<>();
                try {
                        // DB에서 페이징된 리뷰 목록을 가져오기
                        List<Object[]> reviews = webtoon_ReviewRepository
                                        .getWebtoonReviewAndWebtoonReviewLikeDescLike(webtoonId,
                                                        pvo.getStartIndex(), pvo.getEndIndex());

                        // 날짜 포맷터 설정
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        System.out.println(reviews.size());
                        if (reviews != null && !reviews.isEmpty()) {
                                for (Object[] obj : reviews) {
                                        Long webtoonReviewNum = obj[0] instanceof BigDecimal
                                                        ? ((BigDecimal) obj[0]).longValue()
                                                        : null;
                                        Long userNum = obj[1] instanceof BigDecimal ? ((BigDecimal) obj[1]).longValue()
                                                        : null;
                                        String userNickname = obj[2] instanceof String ? (String) obj[2] : null;
                                        String webtoonReviewReview = obj[3] instanceof String ? (String) obj[3] : null;
                                        Date webtoonReviewDate = obj[4] instanceof Date ? (Date) obj[4] : null;
                                        Long like_count = obj[5] instanceof BigDecimal
                                                        ? ((BigDecimal) obj[5]).longValue()
                                                        : null;
                                        Long disLike_count = obj[6] instanceof BigDecimal
                                                        ? ((BigDecimal) obj[6]).longValue()
                                                        : null;
                                        Long row_num = obj[7] instanceof BigDecimal ? ((BigDecimal) obj[7]).longValue()
                                                        : null;

                                        // 날짜가 null일 경우 처리
                                        String formattedDate = (webtoonReviewDate != null)
                                                        ? sdf.format(webtoonReviewDate)
                                                        : "";

                                        // Map에 데이터를 넣어서 반환
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("webtoonReviewNum", webtoonReviewNum);
                                        map.put("userNum", userNum);
                                        map.put("userNickname", userNickname);
                                        map.put("webtoonReviewReview", webtoonReviewReview);
                                        map.put("webtoonReviewDate", formattedDate); // 포맷된 날짜 문자열
                                        map.put("like_count", like_count);
                                        map.put("disLike_count", disLike_count);
                                        map.put("row_num", row_num);

                                        // 리스트에 추가
                                        reviewVOs.add(map);
                                }
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return reviewVOs;
        }

        public Long getWebtoonIdCount(Long webtoonId) {
                return webtoon_ReviewRepository.getWebtoonReviewCount(webtoonId);
        }

        public void Webtoon_ReviewService(Webtoon_ReviewRepository webtoon_ReviewRepository) {
                this.webtoon_ReviewRepository = webtoon_ReviewRepository;
        }

        // 좋아요한 웹툰 리뷰 가져오기
        public List<Webtoon_Review> findLikedWebtoonsByUser(Long userNum) {
                return webtoon_ReviewRepository.findLikedWebtoonsByUser(userNum);
        }

        // 모든 웹툰 리뷰 가져오기
        public List<Webtoon_Review> findAll() {
                System.out.println("서비스 3번째꺼");
                return webtoon_ReviewRepository.findAllWithUser();
        }

        // 아더페이지 페이징처리
        public Long getReviewCount(Long webtoonReviewNum) {
                if (webtoonReviewNum == -1) {
                        return webtoon_ReviewRepository.count();
                } else {
                        return webtoon_ReviewRepository.countByWebtoonReview(webtoonReviewNum.longValue());
                }
        }

        public List<Webtoon_ReviewVO> getReviewList(PageVO pvo, Long userNum) {
                return webtoon_ReviewRepository.findWebtoonReviewListotherPage(
                                pvo.getStartIndex(), pvo.getEndIndex(), userNum).stream()
                                .map(obj -> {
                                        Long webtoonReviewNum = obj[0] != null ? Long.parseLong(obj[0].toString())
                                                        : null;
                                        String userNickname = obj[1] != null ? obj[1].toString() : null;
                                        String webtoonReviewReview = obj[2] != null ? obj[2].toString() : null;
                                        Date webtoonReviewDate = obj[3] != null ? (Date) obj[3] : null;
                                        Integer webtoonId = obj[4] != null ? Integer.valueOf(obj[4].toString()) : null;
                                        String webtoonTitle = obj[5] != null ? obj[5].toString() : null;
                                        Long user_num = obj[6] != null ? ((Number) obj[6]).longValue() : null;

                                        UserVO userVO = null;
                                        if (user_num != null) {
                                                userVO = UserVO.builder()
                                                                .userNum(user_num)
                                                                .userNickname(userNickname)
                                                                .build();

                                        }
                                        WebtoonVO webtoonVO = null;
                                        if (webtoonId != null) {
                                                webtoonVO = WebtoonVO.builder()
                                                                .webtoonId(webtoonId)
                                                                .webtoonTitle(webtoonTitle)
                                                                .build();

                                        }
                                        return Webtoon_ReviewVO.builder()
                                                        .webtoonReviewNum(webtoonReviewNum)
                                                        .userNickname(userNickname)
                                                        .webtoonReviewReview(webtoonReviewReview)
                                                        .webtoonReviewDate(webtoonReviewDate)
                                                        .webtoon(webtoonVO)
                                                        .user(userVO)
                                                        .build();
                                })
                                .peek(vo -> System.out.println(vo))
                                .toList();
        }

        // 마이페이지 리뷰리스트 카운트 페이징처리
        public Long getReivewCount(@Param("userNum") Long userNum) {
                return webtoon_ReviewRepository.countWebtoonRiviewByUserNum(userNum);
        }

        // 마이페이지 리뷰리스트 페이징처리
        public List<Webtoon_ReviewVO> getMyReviewList(PageVO pvo, Long userNum) {
                return webtoon_ReviewRepository
                                .findWebtoonReviewListWithTitle(pvo.getStartIndex(), pvo.getEndIndex(), userNum)
                                .stream()
                                .map(obj -> {
                                        Long webtoonReviewNum = obj[0] != null ? Long.parseLong(obj[0].toString())
                                                        : null;
                                        String userNickname = obj[1] != null ? obj[1].toString() : null;
                                        String webtoonReviewReview = obj[2] != null ? obj[2].toString() : null;
                                        Date webtoonReviewDate = obj[3] != null ? (Date) obj[3] : null;
                                        Long webtoonId = obj[4] != null ? ((BigDecimal) obj[4]).longValue() : null;
                                        String webtoonTitle = obj[5] != null ? obj[5].toString() : null;
                                        Long user_num = (obj.length > 6 && obj[6] != null)
                                                        ? ((BigDecimal) obj[6]).longValue()
                                                        : null;

                                        UserVO userVO = null;
                                        if (user_num != null) {
                                                userVO = UserVO.builder()
                                                                .userNum(user_num)
                                                                .userNickname(userNickname)
                                                                .build();
                                        }

                                        // WebtoonVO 객체를 새로 생성하여 설정
                                        WebtoonVO webtoonVO = WebtoonVO.builder()
                                                        .webtoonId(webtoonId.intValue())
                                                        .webtoonTitle(webtoonTitle)
                                                        .build();

                                        return Webtoon_ReviewVO.builder()
                                                        .webtoonReviewNum(webtoonReviewNum)
                                                        .userNickname(userNickname)
                                                        .webtoonReviewReview(webtoonReviewReview)
                                                        .webtoonReviewDate(webtoonReviewDate)
                                                        .webtoon(webtoonVO) // WebtoonVO 객체 설정
                                                        .user(userVO)
                                                        .build();
                                })
                                .peek(vo -> System.out.println(vo))
                                .toList();
        }

}
