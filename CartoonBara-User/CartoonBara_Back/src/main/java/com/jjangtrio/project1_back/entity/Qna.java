package com.jjangtrio.project1_back.entity;

import java.util.Date;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "qna")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Qna {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "qna_seq")
    @SequenceGenerator(name = "qna_seq", sequenceName = "qna_seq", allocationSize = 1)
    @Column(name = "qna_num")
    private Long qnaNum;

    @ManyToOne
    @JoinColumn(name = "user_num", nullable = false)
    private User user;

    @ManyToOne // 엔티티와 다대일 관계
    @JoinColumn(name = "permission_num", nullable = false)
    private Permission permission;

    @Column(name = "qna_category", nullable = false)
    private Long qnaCategory;

    @Column(name = "qna_title", nullable = false)
    private String qnaTitle;

    @Column(name = "qna_content", nullable = false)
    private String qnaContent;

    @Column(name = "qna_answer")
    private String qnaAnswer;

    @Column(name = "qna_image")
    private String qnaImage;

    @ColumnDefault(value = "sysdate")
    @Column(name = "qna_question_date", nullable = false)
    private Date qnaQuestionDate;

    @Column(name = "qna_answer_date")
    private Date qnaAnswerDate;
}
