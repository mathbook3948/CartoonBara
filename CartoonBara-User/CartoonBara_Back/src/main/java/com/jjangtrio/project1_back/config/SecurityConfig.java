package com.jjangtrio.project1_back.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.jjangtrio.project1_back.security.JwtFilter;

@Configuration
@EnableWebSecurity
// 권한을 나눠주는 용도의 파일
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        // CORS 설정을 HttpSecurity 내에서 직접 처리
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.addAllowedOrigin("http://localhost:3009");
            config.addAllowedOrigin("http://localhost:3000");
            config.addAllowedOrigin("http://localhost:8891");
            config.addAllowedMethod("*");
            config.addAllowedHeader("*");
            config.setAllowCredentials(true);
            return config;
        }));

        http.sessionManagement((session) -> session.disable());

        http.authorizeHttpRequests((auth) -> {
            auth.requestMatchers("/*.png", "/*.jpg").permitAll();
            auth.requestMatchers(
                    "/api/community_comm/add",
                    "/api/community_comm/delete/{communityCommNum}",
                    "/api/community_comm//update/{communityCommNum}",
                    "/api/community/delete/{communityNum}",
                    "/api/community/detail/{communityNum}",
                    "/api/community/likechecks",
                    "/api/community/liketoggle",
                    "/api/community/update/{communityNum}",
                    "/api/community/upload",
                    "/api/community/uploads/{filename:.+}",
                    // ==============================
                    "/api/CommunityEditor/editor/detail",
                    "/api/CommunityEditor/startoggle/{communityEditorNum}",
                    "/api/communityEditorComm/add",
                    "/api/communityEditorComm/delete/{communityEditorCommNum}",
                    "/api/communityEditorComm/update/{communityEditorCommNum}",
                    // ===============================
                    "/api/qna",
                    "/api/singo",
                    "/api/survey/updateCount",
                    // ===============================
                    "/api/webtoon/addDislike",
                    "/api/webtoon/addLike",
                    "/api/webtoon/addTag",
                    "/api/webtoon/review/addBadLike",
                    "/api/webtoon/review/addGoodLike",
                    "/api/webtoon/review/otherReview",
                    "/api/webtoon/review/reviewadd",
                    "/api/webtoon/review/reviewdelete",
                    "/api/webtoon/review/reviewupdate",
                    "/api/webtoonReviewComm/delete",
                    "/api/webtoonReviewComm/update",
                    "/api/webtoonReviewComm/upload",
                    // ==============
                    "/api/user/changePwd",
                    "/api/user/changeQs",
                    "/api/user/delete/{userNum}",
                    "/api/user/getUsername",
                    "/api/user/imgUpdate",
                    "/api/user/introduce",
                    "/api/user/update",
                    "/api/user/oneUserNum",
                    // ===============
                    "/api/community/myPageCommu",
                    "/api/community/myPageCommu/{userNum}",
                    "/api/community/search/MyPage",
                    "/api/webtoon/review/otherReview/{userNum}",
                    "/api/user/oneuser/{userNum}",
                    "/api/user/userPasswordLessPermission/userNum",
                    "/api/user/handlePasswordLessCancel",
                    "/api/user/setNewPassword",
                    "/api/qna/oneByoneQnA",
                    "/api/community/getCommuLike"

            ).authenticated(); // 로그인 필요 기능

            auth.requestMatchers(
                    "/api/CommunityEditor/update/{communityEditorNum}",
                    "/api/CommunityEditor/delete/{communityEditorNum}",
                    "/api/CommunityEditor/upload",
                    "/api/CommunityEditor/upload/{filename:.+}").hasAnyAuthority("ROLE_USER","ROLE_EDITOR", "ROLE_ADMIN");

            auth.requestMatchers(HttpMethod.DELETE, "/api/qna/{id}").hasAuthority("ROLE_ADMIN");

            auth.requestMatchers(HttpMethod.GET, "/api/qna/{id}").authenticated();

            auth.requestMatchers(
                    "**_image**",
                    "**.png**",
                    "**.jpg**",
                    "/api/community_comm/all",
                    "/api/community_comm//count/{communityNum}",
                    "/api/community/best/likes",
                    "/api/community/category/{communityCategory}",
                    "/api/community//getAll",
                    "/api/community/getpage",
                    "/api/community/hit",
                    "/api/community/likecount",
                    "/api/community/search",
                    // ===============================
                    "/api/search/webtoon",
                    "/api/search/author",
                    // ===============================
                    "/api/CommunityEditor/average-star/{communityEditorNum}",
                    "/api/CommunityEditor/detail/{communityEditorNum}",
                    "/api/CommunityEditor/hit",
                    "/api/CommunityEditor/latestList",
                    "/api/CommunityEditor/next/{currentId}",
                    "/api/CommunityEditor/popularList",
                    "/api/CommunityEditor/previous/{currentId}",
                    "/api/CommunityEditor/search",
                    "/api/CommunityEditor/starCount/{communityEditorNum}",
                    "/api/CommunityEditor/totalstars/{communityEditorNum}",
                    "/api/communityEditorComm/commentsByEditor/{communityEditorNum}",
                    "/api/communityEditorComm/count/{communityEditorNum}",
                    // ===============================
                    "/api/qna/getpage",
                    "/api/qna/search",
                    // ===============================
                    "/api/webtoon/{webtoonId}",
                    "/api/webtoon/author",
                    "/api/webtoon/detail",
                    "/api/webtoon/getAllTag",
                    "/api/webtoon/getDislike",
                    "/api/webtoon/getLike",
                    "/api/webtoon/getManyLike",
                    "/api/webtoon/getTop5Webtoon",
                    "/api/webtoon/latestList",
                    "/api/webtoon/review/getReviewLike",
                    "/api/webtoon/review/listdetail/{webtoonId}",
                    "/api/webtoon/review/listdetailLike/{webtoonId}",
                    "/api/webtoon/review/getReviewLike",
                    "/api/webtoon/review/likeBad",
                    "/api/webtoon/review/likeGood",
                    "/api/webtoon/review/listdetail/{webtoonId}",
                    "/api/webtoon/review/listdetailLike/{webtoonId}",
                    "/api/webtoon/review/reviewlist/{webtoonId}",
                    "/api/webtoon/review/webtoonReviewLikeList/{webtoonId}",
                    "/api/webtoon/tag/search",
                    "/api/webtoon/tagList",
                    "/api/webtoon/review/listdetailLikePage/{webtoonId}",
                    "/api/user/emailcheck",
                    "/api/user/idcheck",
                    "/api/user/kakaoCheck",
                    "/api/user/login",
                    "/api/user/signup",
                    "/api/user/verifyCode",
                    "/api/community/latestCommunityList",
                    "/add/webtoon",
                    "/api/user/sendUserIdViaEmail",
                    "/api/user/sendEmailPasswordFind",
                    "/api/user/updateuserpwd",
                    "/api/user/userPasswordLessPermission").permitAll();
            auth.anyRequest().permitAll();

            /*
             * /user/{userNum}
             * /community/{communityNum}
             * /all
             * -----------------
             * /api/webtoon/review/likeBad
             * /api/webtoon/review/likeGood
             * -----------------
             * /api/user/listall
             * "/api/user/oneUserId"
             */
        });

        http.addFilterBefore(jwtFilter, BasicAuthenticationFilter.class);
        http.formLogin((formlogin) -> formlogin.disable());
        return http.build();
    }

}
