package com.jjangtrio.project1_back.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Community_Editor_CommVO {

    private Long communityEditorCommNum;
    private Long communityEditor;
    private String communityEditorCommContent;
    private Date communityEditorCommDate;
    private Long userNum;

}
