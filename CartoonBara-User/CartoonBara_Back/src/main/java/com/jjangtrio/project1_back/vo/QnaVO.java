package com.jjangtrio.project1_back.vo;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QnaVO {

    private Long qnaNum; 
    private UserVO user; 
    private Long permissionNum; 
    private Long qnaCategory;
    private String qnaTitle; 
    private String qnaContent; 
    private String qnaAnswer; 
    private String qnaImage; 
    private Date qnaQuestionDate; 
    private Date qnaAnswerDate;

}
