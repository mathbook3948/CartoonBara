package com.jjangtrio.project1_back.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Community_EditorVO {

    private Long communityEditorNum;
    private UserVO user;
    private Long communityEditorCategory;
    private String communityEditorTitle;

    private Date communityEditorDate;

    private Long communityEditorHit;
    private String communityEditorContent;
    private String communityEditorIp;
    private String communityEditorImage;

}
