package com.jjangtrio.project1_back.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.entity.Webtoon_Review;
import com.jjangtrio.project1_back.entity.Webtoon_Review_Comm;
import com.jjangtrio.project1_back.repository.PermissionRepository;
import com.jjangtrio.project1_back.repository.UserRepository;
import com.jjangtrio.project1_back.repository.Webtoon_Review_CommRepository;
import com.jjangtrio.project1_back.vo.Webtoon_Review_CommVO;

import jakarta.persistence.EntityNotFoundException;

@Service
public class Webtoon_Review_CommService {
    // ================================================================================================================
    // 김채린이 추가함 오류나면 불러
    @Autowired
    private Webtoon_Review_CommRepository webtoonReview_CommRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    public Boolean updateSingoFlag(Map<String, Object> map) {
        Boolean result = false;

        Optional<Webtoon_Review_Comm> WebtoonReviewComm = webtoonReview_CommRepository
                .findById(Long.parseLong(map.get("primarynumber").toString()));

        if (WebtoonReviewComm.isPresent()) {
            User reported = User.builder().userNum(WebtoonReviewComm.get().getUser().getUserNum()).build();

            if (webtoonReview_CommRepository
                    .IsSingoFlagActive(Long.parseLong(map.get("primarynumber").toString())) != null) {
                System.out.println(" =========================================> webtoonReview_Comm | 이미 신고처리 됨");
                result = true;
            } else {
                if (webtoonReview_CommRepository
                        .updateSingoFlag(Long.parseLong(map.get("primarynumber").toString())) != null) {
                    System.out.println("=======================================> webtoonReview_Comm | 신고처리 완료");
                    result = true;
                }
            }

            if (reported != null) {
                if (permissionRepository.IsSingoFlagActive(reported.getUserNum()) != null) {
                    System.out.println("===========================================> User |  이미 신고처리 됨");
                    result = true;
                } else {
                    if (permissionRepository.updateSingoFlag(reported.getUserNum()) != null) {
                        if (permissionRepository.updateSingoFlag(reported.getUserNum()) != null) {
                            if (userRepository.updateSingoAccumulated(reported.getUserNum()) != null) {
                                System.out.println("=======================================> User | 신고처리 완료");
                                result = true;
                            }
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
    // 이 밑은 정우

    // 업로드
    public Webtoon_Review_Comm uploadWebtoonReviewComm(Long webtoonReviewNum, Long userNum,
            String webtoonReviewCommContet, String webtoonReviewNickname, String webtoonReviewCommIp) {

        Webtoon_Review_Comm wrc = new Webtoon_Review_Comm();
        wrc.setUser(User.builder().userNum(userNum).build());
        wrc.setWebtoonReviewCommContent(webtoonReviewCommContet);
        wrc.setWebtoonReviewNickname(webtoonReviewNickname);
        wrc.setWebtoonReview(Webtoon_Review.builder().webtoonReviewNum(webtoonReviewNum).build());
        wrc.setWebtoonReviewCommDate(new Date());
        wrc.setWebtoonReviewCommSingoFlag(0L);
        wrc.setWebtoonReviewCommIp(webtoonReviewCommIp);

        return webtoonReview_CommRepository.save(wrc);
    }

    // 업데이트
    public Webtoon_Review_Comm updateWebtoonReviewComm(Long webtoonReviewCommNum, String webtoonReviewCommContent) {
        Webtoon_Review_Comm wrc = webtoonReview_CommRepository.findById(webtoonReviewCommNum)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));
        wrc.setWebtoonReviewCommContent(webtoonReviewCommContent);
        return webtoonReview_CommRepository.save(wrc);
    }

    // 삭제
    public void deleteWebtoonReviewComm(Long webtoonReviewCommNum) {
        webtoonReview_CommRepository.deleteById(webtoonReviewCommNum);
    }

}
