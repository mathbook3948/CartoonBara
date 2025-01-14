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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.jjangtrio.project1_back.entity.Community;
import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.repository.CommunityRepository;
import com.jjangtrio.project1_back.repository.Community_LikeRepository;
import com.jjangtrio.project1_back.repository.PermissionRepository;
import com.jjangtrio.project1_back.repository.UserRepository;
import com.jjangtrio.project1_back.vo.CommunityVO;
import com.jjangtrio.project1_back.vo.PageVO;
import com.jjangtrio.project1_back.vo.UserVO;

import io.lettuce.core.dynamic.annotation.Param;

@Service
public class CommunityService {

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private Community_LikeRepository communityLikeRepository;

    public List<CommunityVO> mapToCommunityVO(PageVO pvo, Integer category, String title) {
        if (category == -1) {

            List<Object[]> communityList = communityRepository.findCommunityWithinRange(pvo.getStartIndex(),
                    pvo.getEndIndex());
            if (communityList.isEmpty()) {
                return new ArrayList<>(); // 빈 List<CommunityVO> 반환
            }

            return communityRepository.findCommunityWithinRange(pvo.getStartIndex(), pvo.getEndIndex()).stream()
                    .filter(obj -> {
                        // 카테고리가 5인 경우 필터링
                        Long communityCategory = obj[5] != null ? Long.parseLong(obj[5].toString()) : null;
                        return communityCategory == null || communityCategory != 5; // 카테고리 5 제외
                    })
                    .map(obj -> {
                        Long communityNum = obj[0] != null ? Long.parseLong(obj[0].toString()) : null;
                        String communityTitle = obj[1] != null ? obj[1].toString() : null;
                        String communityContent = obj[2] != null ? obj[2].toString() : null;
                        Date communityDate = obj[3] != null ? (Date) obj[3] : null;
                        Long communityHit = obj[4] != null ? Long.parseLong(obj[4].toString()) : null;
                        Long communityCategory = obj[5] != null ? Long.parseLong(obj[5].toString()) : null;
                        String community_image = obj[6] != null ? obj[6].toString() : null;
                        Long userNum = ((BigDecimal) obj[7]).longValue();
                        String userNickname = obj[8] != null ? obj[8].toString() : null;
                        String userImage = obj[9] != null ? obj[9].toString() : null;

                        UserVO userVO = null;
                        if (userNum != null) {
                            userVO = UserVO.builder()
                                    .userNum(userNum)
                                    .userNickname(userNickname)
                                    .userImage(userImage)
                                    .build();
                        }

                        return CommunityVO.builder()
                                .communityNum(communityNum)
                                .communityTitle(communityTitle)
                                .communityContent(communityContent)
                                .communityDate(communityDate)
                                .communityHit(communityHit)
                                .communityCategory(communityCategory)
                                .communityImage(community_image)
                                .user(userVO)
                                .build();
                    })
                    .peek(vo -> System.out.println(vo))
                    .toList();

        } else {

            return communityRepository
                    .findCommunityWithinRangeWithCategory(pvo.getStartIndex(), pvo.getEndIndex(), category.longValue())
                    .stream()
                    .map(obj -> {
                        Long communityNum = obj[0] != null ? Long.parseLong(obj[0].toString()) : null;
                        String communityTitle = obj[1] != null ? obj[1].toString() : null;
                        String communityContent = obj[2] != null ? obj[2].toString() : null;
                        Date communityDate = obj[3] != null ? (Date) obj[3] : null;
                        Long communityHit = obj[4] != null ? Long.parseLong(obj[4].toString()) : null;
                        Long communityCategory = obj[5] != null ? Long.parseLong(obj[5].toString()) : null;
                        String community_image = obj[6] != null ? obj[6].toString() : null;
                        Long userNum = ((BigDecimal) obj[7]).longValue();
                        String userNickname = obj[8] != null ? obj[8].toString() : null;
                        String userImage = obj[9] != null ? obj[9].toString() : null;

                        UserVO userVO = null;
                        if (userNum != null) {
                            userVO = UserVO.builder()
                                    .userNum(userNum)
                                    .userNickname(userNickname)
                                    .userImage(userImage)
                                    .build();
                        }

                        return CommunityVO.builder()
                                .communityNum(communityNum)
                                .communityTitle(communityTitle)
                                .communityContent(communityContent)
                                .communityDate(communityDate)
                                .communityHit(communityHit)
                                .communityCategory(communityCategory)
                                .communityImage(community_image)
                                .user(userVO)
                                .build();
                    })
                    .peek(vo -> System.out.println(vo))
                    .toList();
        }

    }

    public Long getCommunityCount(Integer category) {

        if (category == -1) {
            // return communityRepository.count();
            return communityRepository.countByCommunity();
        } else {
            return communityRepository.countByCommunityCategory(category.longValue());
        }
    }

    public List<Community> getCommunitiesWithMinLikes(Long minLikes) {
        // 익명 커뮤니티를 제외한 목록을 가져옴
        List<Community> allCommunities = communityRepository.findTop10ByOrderByCommunityNumDesc();

        // 조건에 맞는 커뮤니티만 필터링
        List<Community> filteredCommunities = new ArrayList<>();
        for (Community community : allCommunities) {
            // 해당 커뮤니티의 좋아요 갯수 계산
            Long likeCount = communityLikeRepository
                    .countByCommunityAndCommunityLikeIslikesss(community);

            // 좋아요 갯수가 minLikes 이상인 커뮤니티만 필터링
            if (likeCount >= minLikes) {
                filteredCommunities.add(community);
            }
        }

        return filteredCommunities;
    }

    public List<Community> getCommunitiesWithMinLikesCategory(Long minLikes) {
        // 익명 커뮤니티를 가져옴
        List<Community> allCommunities = communityRepository.findAllOrderByCommunity();

        // 조건에 맞는 커뮤니티만 필터링
        List<Community> filteredCommunities = new ArrayList<>();
        for (Community community : allCommunities) {
            // 해당 커뮤니티의 좋아요 갯수 계산
            Long likeCount = communityLikeRepository.countByCommunityAndCommunityLikeIslike(community);

            // 좋아요 갯수가 minLikes 이상인 커뮤니티만 필터링
            if (likeCount >= minLikes) {
                filteredCommunities.add(community);
            }
        }

        return filteredCommunities;
    }

    public Optional<Community> getDetail(Long communityNum) {
        return communityRepository.findCommunityWithUser(communityNum);
    }

    public void updateHit(Long communityNum) {
        communityRepository.updateHit(communityNum);
    }

    public List<Community> findByAllOrederByCommunityNumDesc() {
        return communityRepository.findAll(Sort.by(Sort.Direction.DESC, "communityNum"));
    }

    public void deleteCommunity(Long communityNum) {
        try {
            communityRepository.deleteById(communityNum);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Community not found with id: " + communityNum);
        }
    }

    public void updateCommunity(Long communityNum, CommunityVO communityVO) {
        Community community = communityRepository.findById(communityNum)
                .orElseThrow(() -> new RuntimeException("수정할 게시글이 존재하지 않습니다."));
        community.setCommunityTitle(communityVO.getCommunityTitle());
        community.setCommunityContent(communityVO.getCommunityContent());
        community.setCommunityCategory(communityVO.getCommunityCategory());
        community.setCommunityImage(communityVO.getCommunityImage());
        communityRepository.save(community);
    }

    public Community createCommunity(CommunityVO vo) throws IOException {
        if (vo.getUser() == null) {
            throw new IllegalArgumentException("User 정보가 누락되었습니다.");
        }
        Community community = new Community();
        community.setUser(User.builder().userNum(vo.getUser().getUserNum()).build());
        community.setCommunityCategory(vo.getCommunityCategory());
        community.setCommunityTitle(vo.getCommunityTitle());
        community.setCommunityHit(0L);
        community.setCommunityContent(vo.getCommunityContent());
        community.setCommunityIp(vo.getCommunityIp());
        community.setCommunityDate(new Date());
        community.setCommunitySingoFlag(0L); // ================================================================================>
                                             // 김채린이 추가
        community.setCommunityImage(
                Optional.ofNullable(vo.getCommunityImage()).orElse(""));

        return communityRepository.save(community);
    }

    public List<Community> findAll() {
        return communityRepository.findAllWithUser();
    }

    public List<Community> findByCommunityCategory(Long communityCategory) {
        return communityRepository.findByCommunityCategory(communityCategory);
    }

    public List<Community> searchCommunityTitles(String communityTitle) {
        return communityRepository.findByCommunityTitleContaining(communityTitle);
    }

    public List<Community> searchCommunityCategory(String communityTitle) {
        return communityRepository.findByCommunityTitleContainingForCategory(communityTitle);
    }

    // 아더페이지 페이징처리
    public Long getCommunityCount(Long userNum) {

        if (userNum == -1) {
            return communityRepository.count();
        } else {
            return communityRepository.countByCommunityCategory(userNum.longValue());
        }
    }

    public List<CommunityVO> getCommunityList(PageVO pvo, Long user_num) {
        return communityRepository.findCommunityListotherPage(
                pvo.getStartIndex(), pvo.getEndIndex(), user_num).stream()
                .map(obj -> {
                    Long communityNum = obj[0] != null ? Long.parseLong(obj[0].toString()) : null;
                    String communityTitle = obj[1] != null ? obj[1].toString() : null;
                    String communityContent = obj[2] != null ? obj[2].toString() : null;
                    Date communityDate = obj[3] != null ? (Date) obj[3] : null;
                    Long communityHit = obj[4] != null ? Long.parseLong(obj[4].toString()) : null;
                    Long communityCategory = obj[5] != null ? Long.parseLong(obj[5].toString()) : null;
                    String community_image = obj[6] != null ? obj[6].toString() : null;
                    Long userNum = ((BigDecimal) obj[7]).longValue();
                    String userNickname = obj[8] != null ? obj[8].toString() : null;

                    UserVO userVO = null;
                    if (userNum != null) {
                        userVO = UserVO.builder()
                                .userNum(userNum)
                                .userNickname(userNickname)
                                .build();
                    }
                    return CommunityVO.builder()
                            .communityNum(communityNum)
                            .communityTitle(communityTitle)
                            .communityContent(communityContent)
                            .communityDate(communityDate)
                            .communityHit(communityHit)
                            .communityCategory(communityCategory)
                            .communityImage(community_image)
                            .user(userVO)
                            .build();
                })
                .peek(vo -> System.out.println(vo))
                .toList();

    }

    public List<Community> findByUserNum(Long userNum) {
        return communityRepository.findAllByUser_UserNum(userNum);
    }

    // 마이페이지 검색기능
    public List<Community> findByCommunityTitleAndUser_UserNum(String communityTitle, Long user_num) {
        return communityRepository.findByCommunityTitleContaining(communityTitle);
    }

    public CommunityService(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    // ================================================================================================================
    // 김채린이 추가함 오류나면 불러

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    public Boolean updateSingoFlag(Map<String, Object> map) {
        Boolean result = false;

        Optional<Community> community = communityRepository
                .findById(Long.parseLong(map.get("primarynumber").toString()));

        if (community.isPresent()) {
            User reported = User.builder().userNum(community.get().getUser().getUserNum()).build();

            if (communityRepository.IsSingoFlagActive(Long.parseLong(map.get("primarynumber").toString())) != null) {
                System.out.println(" =========================================> community | 이미 신고처리 됨");
                result = true;
            } else {
                if (communityRepository.updateSingoFlag(Long.parseLong(map.get("primarynumber").toString())) != null) {
                    System.out.println("=======================================> community | 신고처리 완료");
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

    // 생성자 주입
    @Autowired
    public CommunityService(CommunityRepository communityRepository, Community_LikeRepository communityLikeRepository) {
        this.communityRepository = communityRepository;
        this.communityLikeRepository = communityLikeRepository;
    }

    public List<Community> findLikedCommunitiesByUser(Long userNum) {
        return communityRepository.findLikedCommunitiesByUser(userNum);
    }

    // 마이페이지 커뮤니티 카운트 페이징처리
    public Long getCommCount(@Param("userNum") Long userNum) {
        return communityRepository.countCommunityByUserNum(userNum);
    }

    // 마이페이지 커뮤니티 페이징처리
    public List<CommunityVO> getcommuList(PageVO pvo, Long user_num) {

        return communityRepository.findCommunityListmyPage(pvo.getStartIndex(), pvo.getEndIndex(), user_num).stream()
                .map(obj -> {
                    Long communityNum = obj[0] != null ? Long.parseLong(obj[0].toString()) : null;
                    String communityTitle = obj[1] != null ? obj[1].toString() : null;
                    String communityContent = obj[2] != null ? obj[2].toString() : null;
                    Date communityDate = obj[3] != null ? (Date) obj[3] : null;
                    Long communityHit = obj[4] != null ? Long.parseLong(obj[4].toString()) : null;
                    Long communityCategory = obj[5] != null ? Long.parseLong(obj[5].toString()) : null;
                    String community_image = obj[6] != null ? obj[6].toString() : null;
                    Long userNum = ((BigDecimal) obj[7]).longValue();
                    String userNickname = obj[8] != null ? obj[8].toString() : null;

                    UserVO userVO = null;
                    if (userNum != null) {
                        userVO = UserVO.builder()
                                .userNum(userNum)
                                .userNickname(userNickname)
                                .build();
                    }
                    return CommunityVO.builder()
                            .communityNum(communityNum)
                            .communityTitle(communityTitle)
                            .communityContent(communityContent)
                            .communityDate(communityDate)
                            .communityHit(communityHit)
                            .communityCategory(communityCategory)
                            .communityImage(community_image)
                            .user(userVO)
                            .build();
                })
                .peek(vo -> System.out.println(vo))
                .toList();
    }

    public List<Map<String, Object>> getLatestCommunityList() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Community> latestCommunityList = communityRepository.findTop10ByOrderByCommunityNumDesc();
        for (Community community : latestCommunityList) {
            Map<String, Object> map = new HashMap<>();
            map.put("communityNum", community.getCommunityNum());
            map.put("communityTitle", community.getCommunityTitle());
            map.put("communityHit", community.getCommunityHit());
            map.put("communityCategory", community.getCommunityCategory());
            result.add(map);
        }
        return result;
    }
}