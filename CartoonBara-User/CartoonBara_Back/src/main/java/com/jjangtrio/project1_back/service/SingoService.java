package com.jjangtrio.project1_back.service;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jjangtrio.project1_back.entity.Singo;
import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.repository.SingoRepository;




@Service    
public class SingoService {

    @Autowired
    private SingoRepository singoRepository;

    @Autowired
    private Webtoon_ReviewService webtoonReviewService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private Community_CommService communityCommService;

    /*
     * front에서 데이터가 넘어오는 형식
     * {
            singoCategory: number;                // 1.웹툰 리뷰, 2.웹툰 리뷰 댓글, 3.커뮤니티, 4.커뮤니티 댓글
            userNum: number;                      // 피신고자의 userNum
            primarynumber: number;                // 테이블이 갖는 primaryKey
            singoCurrentUrl: string;              // 신고당한 페이지의 url
     * }
     */

    public Singo insertSingo(Map<String, Object> map) {
        Singo singo = new Singo();

        // User 객체 생성 (피신고자)
        User reporter = User.builder().userNum(Long.parseUnsignedLong(map.get("userNum").toString())).build();

        singo.setSingoCategory(Long.parseUnsignedLong((map.get("singoCategory").toString())));
        singo.setUser(reporter);
        singo.setSingoDate(new Date());
        singo.setPrimarynumber(Long.parseUnsignedLong(map.get("primarynumber").toString()));
        singo.setSingoCurrentUrl(map.get("singoCurrentUrl").toString());
        return singoRepository.save(singo);
    }

    // 신고 내역 조회
    public boolean existSingoByLog(Map<String, Object> map) {
      Long singoCategory = Long.parseLong(map.get("singoCategory").toString());
      Long userNum = Long.parseLong(map.get("userNum").toString());
      Long primaryNumber = Long.parseLong(map.get("primarynumber").toString());

      boolean result = true; // 신고 기록 없음
      if(singoRepository.existSingoByLog(singoCategory, userNum, primaryNumber) != null) {
        result = false; // 신고 기록 이미 존재
      }
      return result;
    }
    
    // 유저 및 글 신고처리
    public Boolean updateSingoFlag(Map<String, Object> map) {

      Boolean result = false;

      switch (map.get("singoCategory").toString()) {
        case "1": // webtoon_review
          if(webtoonReviewService.updateSingoFlag(map)){
            System.out.println("=======================================> Webtoon_Review && User | singoFlag 처리 완료");
            result = true;
          }
          break;
        case "2": // webtoon_review_comment
          break;
        case "3": // community
          if(communityService.updateSingoFlag(map)){
            System.out.println("=======================================> community && User | singoFlag 처리 완료");
            result = true;
          }
          break;      
        case "4": // community_comment
          if(communityCommService.updateSingoFlag(map)){
            System.out.println("=======================================> community_Comm && User | singoFlag 처리 완료");
            result = true;
          }
          break;
      }
      return result;
    }
}
