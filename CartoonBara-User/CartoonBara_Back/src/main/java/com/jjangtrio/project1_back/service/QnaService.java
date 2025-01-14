package com.jjangtrio.project1_back.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jjangtrio.project1_back.entity.Community;
import com.jjangtrio.project1_back.entity.Permission;
import com.jjangtrio.project1_back.entity.Qna;
import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.repository.QnaRepository;
import com.jjangtrio.project1_back.vo.PageVO;
import com.jjangtrio.project1_back.vo.QnaVO;
import com.jjangtrio.project1_back.vo.UserVO;
import com.jjangtrio.project1_back.vo.WebtoonVO;

import io.lettuce.core.dynamic.annotation.Param;

@Service
public class QnaService {
    @Autowired
    private QnaRepository qnaRepository;

    public boolean saveQna(QnaVO vo, Long userNum) throws IOException {

        Boolean result = false;

        User user = User.builder().userNum(userNum).build();
        Permission permission = Permission.builder().userNum(userNum).build();

        if (user != null && permission != null) {
            Qna qna = new Qna();

            if (vo.getQnaImage() != null) {
                qna.setQnaImage(vo.getQnaImage().toString());
            }
            qna.setQnaCategory(vo.getQnaCategory());
            qna.setQnaTitle(vo.getQnaTitle().toString());
            qna.setQnaContent(vo.getQnaContent());
            qna.setQnaQuestionDate(new Date()); // PrePersist로 설정되지만 명시적으로 설정
            qna.setUser(user);
            qna.setPermission(permission);

            qnaRepository.save(qna);
            result = true;
        }
        return result;
    }

    // 읽기 (Read) _list
    public List<Qna> getQnaList() {
        return qnaRepository.findAllByOrderByQnaNumDesc();
    }

    // 읽기 (Read) _detail
    public Optional<Qna> getQnaByQnaNum(Long id) {
        return qnaRepository.findById(id);
    }

    // 삭제 (Delete)
    public void deleteQna(Long id) {
        Qna entity = qnaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("QnA가 존재하지 않습니다."));
        qnaRepository.delete(entity);
    }

    // 페이징 처리
    public List<QnaVO> getQna(PageVO pvo) {
        return qnaRepository.findQnaWithinRange(pvo.getStartIndex(), pvo.getEndIndex()).stream()
                .map(obj -> {
                    Long qnaNum = obj[0] != null ? Long.parseLong(obj[0].toString()) : null;
                    Long userNum = ((BigDecimal) obj[1]).longValue();
                    Long qnaCategory = obj[2] != null ? Long.parseLong(obj[2].toString()) : null;
                    String qnaTitle = obj[3] != null ? obj[3].toString() : null;
                    String qnaContent = obj[4] != null ? obj[4].toString() : null;
                    String qnaAnswer = obj[5] != null ? obj[5].toString() : null;
                    String qnaImage = obj[6] != null ? obj[6].toString() : null;
                    Date qnaQuestionDate = obj[7] != null ? (Date) obj[7] : null;
                    Date qnaAnswerDate = obj[8] != null ? (Date) obj[8] : null;
                    String userNickname = obj[9] != null ? obj[9].toString() : null;

                    UserVO userVO = null;
                    if (userNum != null) {
                        userVO = UserVO.builder()
                                .userNum(userNum)
                                .userNickname(userNickname)
                                .build();
                    }

                    return QnaVO.builder()
                            .qnaNum(qnaNum)
                            .user(userVO)
                            .qnaCategory(qnaCategory)
                            .qnaTitle(qnaTitle)
                            .qnaContent(qnaContent)
                            .qnaAnswer(qnaAnswer)
                            .qnaImage(qnaImage)
                            .qnaQuestionDate(qnaQuestionDate)
                            .qnaAnswerDate(qnaAnswerDate)
                            .build();
                })
                .peek(vo -> System.out.println(vo))
                .toList();
    }

    public Long countByQnaNUm() {
        return qnaRepository.countByQnaNum();
    }

    public List<Qna> searchQnaTitles(String QnaTitle) {
        return qnaRepository.findByQnaTitleContaining(QnaTitle);
    }

    // 마이페이지 1대1 문의 리스트
    public List<Map<String, Object>> getAllByUserNum(Long userNum) {
        List<Qna> qnas = qnaRepository.findAllByUser(User.builder().userNum(userNum).build());
        List<Map<String, Object>> qnaMaps = new ArrayList<>();
        for (Qna qna : qnas) {
            Map<String, Object> map = new HashMap<>();
            System.out.println(qna.getQnaNum());
            map.put("userNum", userNum);
            map.put("qnaCategory", qna.getQnaCategory());
            map.put("qnaTitle", qna.getQnaTitle());
            map.put("qnaQuestionDate", qna.getQnaQuestionDate());
            map.put("qnaAnswerDate", qna.getQnaAnswerDate());
            map.put("qnaNum", qna.getQnaNum());
            qnaMaps.add(map);
        }
        return qnaMaps;
    }

    // 마이페이지 1대1문의 페이징처리 카운트
    public Long getQnaCount(@Param("userNum") Long userNum) {
        return qnaRepository.countQnaByUserNum(userNum);
    }

    // 마이페이지 1대1문의 페이징처리중
    public List<QnaVO> getQnaList(PageVO pvo, Long userNum) {
        return qnaRepository.findQnaListWithTitle(pvo.getStartIndex(), pvo.getEndIndex(), userNum).stream()
                .map(obj -> {
                    Long qnaNum = obj[0] != null ? Long.parseLong(obj[0].toString()) : null;
                    Long qnaCategory = obj[1] != null ? Long.parseLong(obj[1].toString()) : null;
                    String qnaTitle = obj[2] != null ? obj[2].toString() : null;
                    Date qnaQuestionDate = obj[3] != null ? (Date) obj[3] : null;
                    Date qnaAnswerDate = obj[4] != null ? (Date) obj[4] : null;
                    Long user_num = ((BigDecimal) obj[5]).longValue();

                    UserVO userVO = null;
                    if (user_num != null) {
                        userVO = UserVO.builder()
                                .userNum(user_num)
                                .build();
                    }
                    return QnaVO.builder()
                            .qnaNum(qnaNum)
                            .qnaCategory(qnaCategory)
                            .qnaTitle(qnaTitle)
                            .qnaQuestionDate(qnaQuestionDate)
                            .qnaAnswerDate(qnaAnswerDate)
                            .user(userVO)
                            .build();
                })
                .peek(vo -> System.out.println(vo))
                .toList();
    }

}
