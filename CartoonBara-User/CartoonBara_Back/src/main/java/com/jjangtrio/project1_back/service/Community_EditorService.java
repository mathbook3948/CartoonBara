package com.jjangtrio.project1_back.service;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.jjangtrio.project1_back.entity.Community_Editor;
import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.entity.Webtoon;
import com.jjangtrio.project1_back.entity.Webtoon_Authors;
import com.jjangtrio.project1_back.repository.Community_EditorRepository;
import com.jjangtrio.project1_back.repository.Webtoon_AuthorsRepository;
import com.jjangtrio.project1_back.vo.Community_EditorVO;
import com.jjangtrio.project1_back.vo.PageVO;
import com.jjangtrio.project1_back.vo.UserVO;

@Service
public class Community_EditorService {

    @Autowired
    private Community_EditorRepository communityEditorRepository;

    @Autowired
    private Webtoon_AuthorsRepository webtoon_AuthorsRepository;

    // public Long getCommunity_EditorStar(Long communityEditorNum) {
    // return communityEditorStarRepository
    // .getStar(Community_Editor.builder().communityEditorNum(communityEditorNum).build());
    // }

    // 게시물 번호로 게시물을 조회
    public Community_Editor getCommunity_Editor(@Param("communityEditorNum") Long communityEditorNum) {
        Community_Editor communityEditor = communityEditorRepository.findById(communityEditorNum)
                .orElseThrow(() -> new RuntimeException("해당 번호의 게시물이 없습니다."));
        return communityEditor;
    }

    // 웹툰 작가 목록 조회
    public List<String> getWebtoonAuthors(Webtoon webtoon) {
        List<Webtoon_Authors> webtoon_Authors = webtoon_AuthorsRepository.findByWebtoon(webtoon);

        List<String> authors = new ArrayList<>();
        for (Webtoon_Authors author : webtoon_Authors) {
            authors.add(author.getWebtoonAuthorsListNum().getWebtoonAuthor());
        }
        return authors;
    }

    // 전체 게시글 조회
    public Long getCommunity_EditorCount() {
        return communityEditorRepository.count();
    }

    // 커뮤니티 기자 1명 출력
    public List<Community_Editor> getEditorsDetail(Long communityEditorNum) {
        return communityEditorRepository.findByCommunityEditorNum(communityEditorNum);
    }

    // 게시글 디테일
    public Optional<Community_Editor> getDetail(Long communityEditorNum) {
        return communityEditorRepository.findById(communityEditorNum);
    }

    public List<Community_EditorVO> getCommunity_Editors(PageVO pvo, Long category, String title) {
        return communityEditorRepository.findCommunityEditorsWithNumbered(pvo.getStartIndex(), pvo.getEndIndex())
            .stream()
            .map(obj -> {
                Long communityEditorNum = obj[0] != null ? Long.parseLong(obj[0].toString()) : null; 
                Long userNum = obj[1] != null ? ((BigDecimal) obj[1]).longValue() : null;
                Long communityEditorCategory = obj[2] != null ? Long .parseLong(obj[2].toString()) : null;
                String communityEditorTitle = obj[3] != null ? obj[3].toString() : null;
                Date communityEditorDate = obj[4] != null ? (Date) obj[4] : null;
                Long communityEditorHit = obj[5] != null ? Long.parseLong(obj[5].toString()) : null;
                String communityEditorContent = obj[6] != null ? obj[6].toString() : null;
                String communityEditorIp = obj[7] != null ? obj[7].toString() : null;
                String communityEditorImage = obj[8] != null ? obj[8].toString() : null;
                String userNickname = obj[9] != null ? obj[9].toString() : null;
                String userImage = obj[10] != null ? obj[10].toString() : null;

                UserVO userVO = null;
                if (userNum != null) {
                    userVO = UserVO.builder()
                        .userNum(userNum)
                        .userNickname(userNickname)
                        .userImage(userImage)
                        .build();
                }
    
                return Community_EditorVO.builder()
                    .communityEditorNum(communityEditorNum)
                    .user(userVO)
                    .communityEditorCategory(communityEditorCategory)
                    .communityEditorTitle(communityEditorTitle)
                    .communityEditorDate(communityEditorDate)
                    .communityEditorHit(communityEditorHit)
                    .communityEditorContent(communityEditorContent)
                    .communityEditorIp(communityEditorIp)
                    .communityEditorImage(communityEditorImage)
                    .build();
            })
            .peek(vo -> System.out.println("vo.getCommunityEditorNum() : " + vo.getCommunityEditorNum()))
            .toList();
    }
    
    public Community_Editor createCommunityEditor(Community_EditorVO vo) {

        if (vo.getUser().getUserNum() == null) {
            throw new IllegalArgumentException("해당 userNum이 존재하지 않습니다: " + vo.getUser().getUserNum());
        }

        Community_Editor communityEditor = new Community_Editor();

        communityEditor.setCommunityEditorTitle(vo.getCommunityEditorTitle());
        communityEditor.setCommunityEditorContent(vo.getCommunityEditorContent());
        communityEditor.setCommunityEditorCategory(vo.getCommunityEditorCategory());
        communityEditor.setCommunityEditorHit(0L);
        communityEditor.setUser(User.builder().userNum(vo.getUser().getUserNum()).build());
        communityEditor.setCommunityEditorDate(new Date());
        communityEditor.setCommunityEditorIp(vo.getCommunityEditorIp());
        communityEditor.setCommunityEditorImage(vo.getCommunityEditorImage());

        System.out.println("vo.getCommunityEditortitle() : " + vo.getCommunityEditorTitle());
        System.out.println("vo.getCommunityEditorContent() : " + vo.getCommunityEditorContent());
        System.out.println("vo.getCommunityEditorCategory() : " + vo.getCommunityEditorCategory());
        System.out.println("vo.getEditor().getEditorNum() : " + vo.getUser().getUserNum());
        System.out.println("vo.getCommunityEditorIp() : " + vo.getCommunityEditorIp());
        System.out.println("vo.getCommunityEditorDate() : " + communityEditor.getCommunityEditorDate());
        System.out.println("vo.getCommunityEditorImage() : " + communityEditor.getCommunityEditorImage());

        return communityEditorRepository.save(communityEditor);

    }

    public Community_Editor updateCommunityEditor(Long communityEditorNum, Community_EditorVO vo) {

        Community_Editor existingEntity = communityEditorRepository.findById(communityEditorNum)
                .orElseThrow(() -> new RuntimeException("Community Editor not found with id: " + communityEditorNum));

        // 수정할 필드들 업데이트
        existingEntity.setCommunityEditorTitle(vo.getCommunityEditorTitle());
        existingEntity.setCommunityEditorContent(vo.getCommunityEditorContent());
        existingEntity.setCommunityEditorCategory(vo.getCommunityEditorCategory());
        existingEntity.setCommunityEditorDate(new Date());

        // 데이터베이스에 저장 (업데이트)
        communityEditorRepository.save(existingEntity);

        // 수정된 객체 반환
        return existingEntity;

        // if (vo.getCommunityEditorImage() != null) {
        // entity.setCommunityEditorImage(vo.getCommunityEditorImage());
        // }

        // // 변경된 엔티티 저장
        // communityEditorRepository.save(entity);
    }

    public void deleteCommunityEditor(Long communityEditorNum) {
        try {
            communityEditorRepository.deleteById(communityEditorNum);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Community Editor not found with id: " + communityEditorNum);
        }
    }

    public void updateHit(Long communityNum) {
        communityEditorRepository.updateHit(communityNum);
    }

    public Optional<Community_Editor> getPreviousPost(Long currentId) {
        return communityEditorRepository.findPreviousPost(currentId);
    }

    public Optional<Community_Editor> getNextPost(Long currentId) {
        return communityEditorRepository.findNextPost(currentId);
    }

    public Long getCommunityEditorCount(Long category) {

        if (category == -1) {
            return communityEditorRepository.count();
        } else {
            return communityEditorRepository.countByCommunityEditorCategory(category.longValue());
        }
    }
    public List<Community_Editor> searchCommunityEditorTitles(String communityEditorTitle) {
        return communityEditorRepository.findByCommunityEditorTitleContaining(communityEditorTitle);
    }
}