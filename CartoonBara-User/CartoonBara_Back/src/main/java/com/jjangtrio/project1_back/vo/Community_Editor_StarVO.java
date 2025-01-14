package com.jjangtrio.project1_back.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Community_Editor_StarVO {

    private Long communityEditorStarNum;
    private Community_EditorVO communityEditor;
    private Long communityEditorStar;
    private UserVO user;
}
