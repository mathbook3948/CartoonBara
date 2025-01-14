package com.jjangtrio.project1_back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jjangtrio.project1_back.entity.Community;
import com.jjangtrio.project1_back.entity.Community_Comm;
import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.repository.Community_CommRepository;
import com.jjangtrio.project1_back.repository.PermissionRepository;
import com.jjangtrio.project1_back.repository.CommunityRepository;
import com.jjangtrio.project1_back.repository.UserRepository;
import com.jjangtrio.project1_back.vo.CommunityVO;
import com.jjangtrio.project1_back.vo.Community_CommVO;
import com.jjangtrio.project1_back.vo.UserVO;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class Community_CommService {

    @Autowired
    private Community_CommRepository community_CommRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommunityRepository communityRepository;

    // 댓글 추가
    public void addComment(Community_CommVO community_CommVO) {
        // User 엔티티를 DB에서 조회
        User user = userRepository.findById(community_CommVO.getUser().getUserNum())
                .orElseThrow(() -> new RuntimeException("사용자가 존재하지 않습니다."));

        // Community 엔티티를 DB에서 조회
        Community community = communityRepository.findById(community_CommVO.getCommunity().getCommunityNum())
                .orElseThrow(() -> new RuntimeException("커뮤니티가 존재하지 않습니다."));

        // Community_Comm 객체 생성
        Community_Comm community_Comm = Community_Comm.builder()
                .user(user) // User 엔티티 설정
                .community(community) // Community 엔티티 설정
                .communityCommContent(community_CommVO.getCommunityCommContent())
                .communityCommDate(new Date()) // 댓글 작성 날짜
                .communityCommIp(community_CommVO.getCommunityCommIp()) // IP 주소
                .communityCommSingoFlag(0L) //================================================================================> 김채린이 추가
                .build();

        // 댓글 저장
        community_CommRepository.save(community_Comm);
    }

    // 특정 커뮤니티의 댓글 조회
    public List<Community_CommVO> getCommentsByCommunity(Long communityNum) {
        List<Community_Comm> comments = community_CommRepository.findAllByCommunityCommunityNum(communityNum);
        return comments.stream().map(this::toVO).collect(Collectors.toList());
    }

    // 특정 유저의 댓글 조회
    public List<Community_CommVO> getCommentsByUser(Long userNum) {
        List<Community_Comm> comments = community_CommRepository.findAllByUserUserNum(userNum);
        return comments.stream().map(this::toVO).collect(Collectors.toList());
    }

    // 댓글 삭제
    public void deleteComment(Long communityCommNum) {
        community_CommRepository.deleteById(communityCommNum);
    }

    // 댓글 수정
    public void updateComment(Long communityCommNum, Community_CommVO community_CommVO) {
        Community_Comm community_Comm = community_CommRepository.findById(communityCommNum)
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));
        community_Comm.setCommunityCommContent(community_CommVO.getCommunityCommContent());
        community_Comm.setCommunityCommDate(community_CommVO.getCommunityCommDate());
        community_CommRepository.save(community_Comm);
    }

    // 댓글 전체
    public List<Community_CommVO> getAllComments() {
        List<Community_Comm> comments = community_CommRepository.findAll();
        return comments.stream().map(this::toVO).collect(Collectors.toList());
    }

    // 엔티티를 VO로 변환
    private Community_CommVO toVO(Community_Comm community_Comm) {
        // UserVO 객체로 변환
        UserVO userVO = UserVO.builder()
                .userNum(community_Comm.getUser().getUserNum())
                .userNickname(community_Comm.getUser().getUserNickname()) // User 엔티티에서 userNum 추출
                .build();

        // CommunityNum 추출
        Long communityNum = community_Comm.getCommunity().getCommunityNum(); // Community 엔티티에서 communityNum 추출

        return Community_CommVO.builder()
                .communityCommNum(community_Comm.getCommunityCommNum())
                .user(userVO) // UserVO 객체에 nickname 포함
                .community(CommunityVO.builder().communityNum(communityNum).build())
                .communityCommContent(community_Comm.getCommunityCommContent())
                .communityCommDate(community_Comm.getCommunityCommDate())
                .build();
    }

     // 특정 커뮤니티의 댓글 갯수 조회
     public Long getCommentCountByCommunity(Long communityNum) {
        return community_CommRepository.countCommentsByCommunityNum(communityNum);
    }
//================================================================================================================
 // 김채린이 추가함 오류나면 불러

 @Autowired
 private PermissionRepository permissionRepository;

 public Boolean updateSingoFlag(Map<String, Object> map) {
     Boolean result = false;

     Optional<Community_Comm> communityComm = community_CommRepository.findById(Long.parseLong(map.get("primarynumber").toString()));
      if(communityComm.isPresent()) {
          User reported = User.builder().userNum(communityComm.get().getUser().getUserNum()).build();

          if(community_CommRepository.IsSingoFlagActive(Long.parseLong(map.get("primarynumber").toString())) != null) {
              System.out.println(" =========================================> community_Comm | 이미 신고처리 됨");
              result = true;
          }else{
              if(community_CommRepository.updateSingoFlag(Long.parseLong(map.get("primarynumber").toString())) != null) {
                  System.out.println("=======================================> community_Comm | 신고처리 완료");
                  result = true;
              }
          } 

          if(reported != null){
              if(permissionRepository.IsSingoFlagActive(reported.getUserNum()) != null) {
                  System.out.println("===========================================> User |  이미 신고처리 됨");
                  result = true;
              } else {
                  if(permissionRepository.updateSingoFlag(reported.getUserNum()) != null ){
                      if(permissionRepository.updateSingoFlag(reported.getUserNum()) != null ){
                          if(userRepository.updateSingoAccumulated(reported.getUserNum()) != null ){
                              System.out.println("=======================================> User | 신고처리 완료");
                              result = true;
                          }   
                      }
                  }   else{
                          System.out.println("===========================================> User | 정보 없음");
                  }
              }
          }
      }
      return result;
  }

//================================================================================================================
}
