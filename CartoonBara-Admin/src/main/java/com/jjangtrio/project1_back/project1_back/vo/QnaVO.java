package com.jjangtrio.project1_back.project1_back.vo;

import java.util.Date;

import org.apache.ibatis.type.Alias;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Alias("qnavo")
@Setter
@Getter
public class QnaVO {
    
    private Long userNum; //UserVO 객체를 참조

    private Long permissionNum; // PermissionVO 객체를 참조

    private Long qnaNum;
    private Long qnaCategory; //1. 신고 2 문의
    private Long qnaCategory2; //1. 광고성글 2. 욕설 3. 
    private String qnaTitle;
    private String qnaContent;
    private String qnaAnswer;
    private String qnaImage;

    private MultipartFile image;
    
    private Date qnaQuestionDate;
    private Date qnaAnswerDate;
}
