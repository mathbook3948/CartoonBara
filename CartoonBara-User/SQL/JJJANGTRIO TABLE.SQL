-- 전체 시퀀스
CREATE SEQUENCE user_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE qna_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE notice_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE singo_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE webtoon_authors_list_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE webtoon_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE webtoon_authors_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE webtoon_like_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE webtoon_tag_list_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE webtoon_tag_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE webtoon_review_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE webtoon_review_like_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE webtoon_review_comm_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE webtoon_search_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE user_interest_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE community_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE community_like_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE community_comm_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE Community_Comm_Like_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE community_editor_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE community_editor_star_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE community_editor_comm_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE survey_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE log_seq INCREMENT BY 1 START WITH 1;

-- User table
CREATE TABLE "user" (
	user_num NUMBER,
  user_name VARCHAR2(100) NOT NULL,
  user_id VARCHAR2(200) NOT NULL,
  user_pwd VARCHAR2(100) NOT NULL,
  user_security_question VARCHAR2(100) NOT NULL,
  user_security_answer VARCHAR2(100) NOT NULL,
  user_image VARCHAR2(255),
  user_level NUMBER DEFAULT 1,
  user_email VARCHAR2(100) NOT NULL,
  user_phone VARCHAR2(100) NOT NULL,
  user_birth DATE NOT NULL,
  user_nickname VARCHAR2(100) NOT NULL,
  user_gender NUMBER NOT NULL,
  user_date DATE DEFAULT SYSDATE NOT NULL,
  user_introduce VARCHAR2(1000),
  user_singo_accumulated NUMBER,
  CONSTRAINT user_pk PRIMARY KEY (user_num),
  CONSTRAINT user_id_uq UNIQUE (user_id),
  CONSTRAINT user_nickname_uq UNIQUE (user_nickname),
  CONSTRAINT user_email_uq UNIQUE (user_email),
  CONSTRAINT user_phone_uq UNIQUE (user_phone),
  CONSTRAINT user_gender_ck CHECK (user_gender IN (0, 1))
);

-- Permission table
CREATE TABLE "permission" (
  user_num NUMBER PRIMARY KEY,
  permission_master NUMBER(3) NOT NULL,
  permission_state NUMBER  NOT NULL,
  is_password_less NUMBER(1) NOT NULL DEFAULT 0,
  CONSTRAINT permission_num_fk FOREIGN KEY (user_num) REFERENCES "user" (user_num) -- 외래 키 제약
);

-- QNA table
CREATE TABLE qna (
  qna_num NUMBER,
  user_num NUMBER NOT NULL,
  permission_num NUMBER NOT NULL,
  qna_category NUMBER NOT NULL,
  qna_title VARCHAR2(100) NOT NULL,
  qna_content VARCHAR2(255) NOT NULL,
  qna_answer VARCHAR2(255),
  qna_image VARCHAR2(255),
  qna_question_date DATE DEFAULT SYSDATE NOT NULL,
  qna_answer_date DATE,
  CONSTRAINT qna_pk PRIMARY KEY (qna_num),
  CONSTRAINT qna_user_fk FOREIGN KEY (user_num) REFERENCES "user" (user_num) ON DELETE CASCADE,
  CONSTRAINT qna_permission_num_fk FOREIGN KEY (permission_num) REFERENCES "permission" (user_num) ON DELETE CASCADE,
  CONSTRAINT qna_category_ck CHECK (qna_category IN (1, 2, 3))
);

-- Notice table
CREATE TABLE NOTICE (
  notice_num NUMBER,
  permission_num NUMBER NOT NULL,
  notice_title VARCHAR2(100) NOT NULL,
  notice_content VARCHAR2(255) NOT NULL,
  notice_image VARCHAR2(255),
  notice_date DATE DEFAULT SYSDATE NOT NULL,
  CONSTRAINT notice_pk PRIMARY KEY (notice_num),
  CONSTRAINT notice_permission_fk FOREIGN KEY (permission_num) REFERENCES "permission" (user_num) ON DELETE CASCADE
);

CREATE TABLE singo (
	singo_num NUMBER PRIMARY KEY,
	user_num NUMBER,
	singo_date DATE NOT NULL,
	singo_category NUMBER,
	primarynumber NUMBER,
	singo_current_url VARCHAR2(255),
	CONSTRAINT singo_num_fk FOREIGN KEY (user_num) REFERENCES "user" (user_num)
);

-- Webtoon authors list table
CREATE TABLE webtoon_authors_list (
  webtoon_authors_list_num NUMBER,
  webtoon_author VARCHAR2(100) NOT NULL,
  CONSTRAINT webtoon_authors_list_pk PRIMARY KEY (webtoon_authors_list_num)
);

-- Webtoon table
CREATE TABLE webtoon (
  webtoon_id NUMBER,
  webtoon_title VARCHAR2(100) NOT NULL,
  webtoon_desc VARCHAR2(1500),
  webtoon_isend NUMBER NOT NULL,
  webtoon_url VARCHAR2(255) NOT NULL,
  CONSTRAINT webtoon_pk PRIMARY KEY (webtoon_id),
  CONSTRAINT webtoon_isend_ck CHECK (webtoon_isend IN (0, 1)),
  CONSTRAINT webtoon_url_uq UNIQUE (webtoon_url)
);

-- LOG 테이블 생성
CREATE TABLE LOG (
    LOG_NUM NUMBER PRIMARY KEY,
    USER_NUM NUMBER,
    LOG_WEBTOON_ID NUMBER NOT NULL,
    LOG_IP VARCHAR2(20) NOT NULL,
    LOG_DATE DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT LOG_USER_FK FOREIGN KEY (USER_NUM) REFERENCES "user" (USER_NUM),
    CONSTRAINT LOG_WEBTOON_FK FOREIGN KEY (LOG_WEBTOON_ID) REFERENCES WEBTOON (WEBTOON_ID)
);

-- Webtoon authors table
CREATE TABLE webtoon_authors (
  webtoon_authors_num NUMBER,
  webtoon_id NUMBER NOT NULL,
  webtoon_authors_list_num NUMBER NOT NULL,
  CONSTRAINT webtoon_authors_pk PRIMARY KEY (webtoon_authors_num),
  CONSTRAINT wa_webtoon_fk FOREIGN KEY (webtoon_id) REFERENCES webtoon (webtoon_id) ON DELETE CASCADE,
  CONSTRAINT wa_authors_list_fk FOREIGN KEY (webtoon_authors_list_num)
    REFERENCES webtoon_authors_list (webtoon_authors_list_num) ON DELETE CASCADE
);

-- Webtoon like table
CREATE TABLE webtoon_like (
  webtoon_like_num NUMBER,
  webtoon_islike NUMBER NOT NULL,
  user_num NUMBER NOT NULL,
  webtoon_id NUMBER NOT NULL,
  CONSTRAINT webtoon_like_pk PRIMARY KEY (webtoon_like_num),
  CONSTRAINT wl_user_fk FOREIGN KEY (user_num) REFERENCES "user" (user_num) ON DELETE CASCADE,
  CONSTRAINT wl_webtoon_fk FOREIGN KEY (webtoon_id) REFERENCES WEBTOON (webtoon_id) ON DELETE CASCADE,
  CONSTRAINT webtoon_islike_ck CHECK (webtoon_islike IN (0, 1))
);

-- Webtoon tag list table
CREATE TABLE webtoon_tag_list (
  webtoon_tag_list_num NUMBER,
  webtoon_tag_list_tagn VARCHAR2(50) NOT NULL,
  CONSTRAINT webtoon_tag_list_pk PRIMARY KEY (webtoon_tag_list_num)
);

-- Webtoon tag table
CREATE TABLE webtoon_tag (
  webtoon_tag_num NUMBER,
  user_num NUMBER NOT NULL,
  webtoon_id NUMBER NOT NULL,
  webtoon_tag_list_num NUMBER NOT NULL,
  CONSTRAINT webtoon_tag_pk PRIMARY KEY (webtoon_tag_num),
  CONSTRAINT webtoon_tag_user_num_fk FOREIGN KEY (user_num) REFERENCES "user" (user_num) ON DELETE CASCADE,
  CONSTRAINT wt_webtoon_fk FOREIGN KEY (webtoon_id) REFERENCES webtoon (webtoon_id) ON DELETE CASCADE,
  CONSTRAINT wt_tag_list_fk FOREIGN KEY (webtoon_tag_list_num) REFERENCES webtoon_tag_list (webtoon_tag_list_num) ON DELETE CASCADE
);

-- Webtoon review table
CREATE TABLE webtoon_review (
  webtoon_review_num NUMBER,
  webtoon_id NUMBER NOT NULL,
  user_num NUMBER NOT NULL,
  user_nickname VARCHAR2(100),
  webtoon_review_review VARCHAR2(1000) NOT NULL,
  webtoon_review_date DATE DEFAULT SYSDATE NOT NULL,
  webtoon_review_ip VARCHAR2(255) NOT NULL,
  webtoon_review_singo_flag NUMBER,
  CONSTRAINT webtoon_singo_flag_ck CHECK (webtoon_review_singo_flag IN (0, 1)),
  CONSTRAINT webtoon_review_pk PRIMARY KEY (webtoon_review_num),
  CONSTRAINT wr_user_fk FOREIGN KEY (user_num) REFERENCES "user" (user_num) ON DELETE CASCADE,
  CONSTRAINT wr_webtoon_fk FOREIGN KEY (webtoon_id) REFERENCES WEBTOON (webtoon_id) ON DELETE CASCADE
  );

-- Webtoon review like table
CREATE TABLE webtoon_review_like (
  webtoon_review_like_num NUMBER,
  webtoon_review_num NUMBER NOT NULL,
  webtoon_review_like_islike NUMBER NOT NULL,
  user_num NUMBER NOT NULL,
  CONSTRAINT webtoon_review_like_pk PRIMARY KEY (webtoon_review_like_num),
  CONSTRAINT wrl_review_fk FOREIGN KEY (webtoon_review_num) REFERENCES webtoon_review (webtoon_review_num) ON DELETE CASCADE,
  CONSTRAINT wrl_user_fk FOREIGN KEY (user_num) REFERENCES "user" (user_num) ON DELETE CASCADE,
  CONSTRAINT webtoon_review_like_islike_ck CHECK (webtoon_review_like_islike IN (0, 1))
);

-- Webtoon review comment table
CREATE TABLE webtoon_review_comm (
  webtoon_review_comm_num NUMBER,
  user_num NUMBER NOT NULL,
  webtoon_review_num NUMBER NOT NULL,
  webtoon_review_nickname VARCHAR2(255) NOT NULL,
  webtoon_review_comm_content VARCHAR2(255) NOT NULL,
  webtoon_review_comm_date DATE DEFAULT SYSDATE NOT NULL,
  webtoon_review_comm_ip VARCHAR2(255) NOT NULL,
  webtoon_review_comm_singo_flag NUMBER,
  CONSTRAINT webtoon_comm_singo_flag_ck CHECK (webtoon_review_comm_singo_flag IN (0, 1)),
  CONSTRAINT webtoon_review_comm_pk PRIMARY KEY (webtoon_review_comm_num),
  CONSTRAINT wrc_user_fk FOREIGN KEY (user_num) REFERENCES "user" (user_num) ON DELETE CASCADE,
  CONSTRAINT wrc_review_fk FOREIGN KEY (webtoon_review_num) REFERENCES webtoon_review (webtoon_review_num) ON DELETE CASCADE
);

-- Webtoon search table
CREATE TABLE webtoon_search (
  webtoon_search_id NUMBER,
  webtoon_id NUMBER NOT NULL,
  webtoon_title_choseong VARCHAR2(300) NOT NULL,
  CONSTRAINT webtoon_search_pk PRIMARY KEY (webtoon_search_id),
  CONSTRAINT ws_webtoon_fk FOREIGN KEY (webtoon_id) REFERENCES webtoon (webtoon_id) ON DELETE CASCADE
);

-- User interest table
CREATE TABLE user_interest (
  user_interest_num NUMBER,
  user_num NUMBER NOT NULL,
  webtoon_tag_list_num NUMBER NOT NULL,
  CONSTRAINT user_interest_pk PRIMARY KEY (user_interest_num),
  CONSTRAINT ui_user_fk FOREIGN KEY (user_num) REFERENCES "user" (user_num) ON DELETE CASCADE,
  CONSTRAINT ui_tag_list_fk FOREIGN KEY (webtoon_tag_list_num)
    REFERENCES webtoon_tag_list (webtoon_tag_list_num) ON DELETE CASCADE
);

-- Community table
CREATE TABLE community (
  community_num NUMBER,
  user_num NUMBER NOT NULL,
  community_category NUMBER NOT NULL,
  community_title VARCHAR2(100) NOT NULL,
  community_date DATE DEFAULT SYSDATE,
  community_hit NUMBER NOT NULL,
  community_image VARCHAR2(255),
  community_content VARCHAR2(255) NOT NULL,
  community_ip VARCHAR2(255) NOT NULL,
  community_singo_flag NUMBER,
  CONSTRAINT community_singo_flag_ck CHECK (community_singo_flag IN (0, 1)),
  CONSTRAINT community_pk PRIMARY KEY (community_num),
  CONSTRAINT community_user_fk FOREIGN KEY (user_num) REFERENCES "user" (user_num) ON DELETE CASCADE
);

-- Community like table
CREATE TABLE community_like (
  community_like_num NUMBER,
  community_num NUMBER NOT NULL,
  community_like_islike NUMBER NOT NULL,
  user_num NUMBER NOT NULL,
  CONSTRAINT community_like_pk PRIMARY KEY (community_like_num),
  CONSTRAINT cl_community_fk FOREIGN KEY (community_num) REFERENCES community (community_num) ON DELETE CASCADE,
  CONSTRAINT cl_user_fk FOREIGN KEY (user_num) REFERENCES "user" (user_num) ON DELETE CASCADE,
  CONSTRAINT community_like_islike_ck CHECK (community_like_islike IN (0, 1))
);

-- Community comment table
CREATE TABLE community_comm (
  community_comm_num NUMBER PRIMARY KEY,
  user_num NUMBER NOT NULL,
  community_num NUMBER NOT NULL,
  community_comm_content VARCHAR2(255) NOT NULL,
  community_comm_date DATE DEFAULT SYSDATE NOT NULL,
  community_comm_ip VARCHAR2(255) NOT NULL,
  community_comm_singo_flag NUMBER,
  CONSTRAINT community_comm_singo_flag_ck CHECK (community_comm_singo_flag IN (0, 1)),
  CONSTRAINT community_comm_user_fk FOREIGN KEY (user_num) REFERENCES "user" (user_num) ON DELETE CASCADE,
  CONSTRAINT comm_community_fk FOREIGN KEY (community_num) REFERENCES community (community_num) ON DELETE CASCADE
);

-- Community comment like table
CREATE TABLE community_comm_like (
  community_comm_like_num NUMBER PRIMARY KEY,
  user_num NUMBER NOT NULL,
  community_comm_num NUMBER NOT NULL,
  community_comm_like_islike NUMBER NOT NULL,
  CONSTRAINT ccl_comm_fk FOREIGN KEY (community_comm_num) REFERENCES community_comm (community_comm_num) ON DELETE CASCADE,
  CONSTRAINT ccl_user_fk FOREIGN KEY (user_num) REFERENCES "user" (user_num) ON DELETE CASCADE,
  CONSTRAINT community_comm_like_islike_ck CHECK (community_comm_like_islike IN (0, 1))
);

-- Community editor table
CREATE TABLE community_editor (
  community_editor_num NUMBER,
  user_num NUMBER NOT NULL,
  community_editor_category NUMBER NOT NULL,
  community_editor_title VARCHAR2(100) NOT NULL,
  community_editor_date DATE DEFAULT SYSDATE NOT NULL,
  community_editor_hit NUMBER NOT NULL,
  community_editor_content VARCHAR2(255) NOT NULL,
  community_editor_ip VARCHAR2(255) NOT NULL,
  community_editor_image VARCHAR2(255),
  CONSTRAINT community_editor_pk PRIMARY KEY (community_editor_num),
  CONSTRAINT editor_user_num_fk FOREIGN KEY (user_num) REFERENCES "user" (user_num) ON DELETE CASCADE
);

CREATE TABLE community_editor_star (
  community_editor_star_num NUMBER,
  community_editor_num NUMBER,
  community_editor_star NUMBER,
  user_num NUMBER NOT NULL,
  community_editor_like NUMBER,
  CONSTRAINT community_editor_star_pk PRIMARY KEY (community_editor_star_num),
  CONSTRAINT star_user_num_fk FOREIGN KEY (user_num) REFERENCES "user" (user_num) ON DELETE CASCADE,
  CONSTRAINT community_editor_star_fk FOREIGN KEY (community_editor_num) REFERENCES community_editor (community_editor_num) ON DELETE CASCADE
);

CREATE TABLE community_editor_comm (
  community_editor_comm_num NUMBER,
   user_num NUMBER NOT NULL,
  community_editor_num NUMBER NOT NULL,
  community_editor_comm_content VARCHAR2(255) NOT NULL,
  community_editor_comm_date DATE DEFAULT SYSDATE NOT NULL,
  CONSTRAINT editor_comm_user_num_fk FOREIGN KEY (user_num) REFERENCES "user" (user_num) ON DELETE CASCADE,
  CONSTRAINT community_editor_comm_pk PRIMARY KEY (community_editor_comm_num),
  CONSTRAINT community_editor_comm_fk FOREIGN KEY (community_editor_num) REFERENCES community_editor (community_editor_num) ON DELETE CASCADE
);

-- Survey 테이블
CREATE TABLE survey (
    num NUMBER PRIMARY KEY,
    code NUMBER,
    sdate DATE,
    sub VARCHAR2(255),
    permission_num NUMBER,
    CONSTRAINT survey_permission_num_FK FOREIGN KEY (permission_num) REFERENCES "permission"(user_num)
);

-- SurveyContent 테이블 (Survey 테이블의 항목들을 저장)
CREATE TABLE surveycontent (
    subcode NUMBER(19) NOT NULL,
    surveycnt NUMBER(10),
    surveytitle VARCHAR2(255),
		surveytype VARCHAR2(255),
    CONSTRAINT surveycontent_surveycontent_FK FOREIGN KEY (subcode) REFERENCES survey(num)  -- 외래 키 제약조건
);

COMMIT;
