package com.jjangtrio.project1_back.security;

import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.http.ResponseEntity.HeadersBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjangtrio.project1_back.entity.Permission;
import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.repository.PermissionRepository;
import com.jjangtrio.project1_back.repository.UserRepository;
import com.jjangtrio.project1_back.service.UserService;
import com.jjangtrio.project1_back.vo.UserVO;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
public class LoginController {

    @Autowired
    private UserForLoginService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> map) {
        try {
            String username = map.get("username");
            String password = map.get("password");

            UsernamePasswordAuthenticationToken inputUser = new UsernamePasswordAuthenticationToken(username,
                    password);
            Authentication auth = authenticationManager.authenticate(inputUser);

            UserForLogin user = (UserForLogin) auth.getPrincipal();
            Permission permission = permissionRepository.findById(user.getUserNum())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            if (permission.getPermissionState() == 4) {
                throw new UsernameNotFoundException("해당 유저는 삭제 처리 중입니다");
            }

            SecurityContextHolder.getContext().setAuthentication(auth);

            String token = jwtService.createToken();
            System.out.println("token =>>>>>>>>" + token);
            return ResponseEntity.ok(token);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserVO vo) {
        try {
            System.out.println("이름 : " + vo.getUserName());
            System.out.println("아이디 : " + vo.getUserId());
            System.out.println("비밀번호 : " + vo.getUserPwd());
            System.out.println("이미지 : " + vo.getUserImage());
            System.out.println("레벨 : " + vo.getUserLevel());
            System.out.println("이메일 : " + vo.getUserEmail());
            System.out.println("전화번호 : " + vo.getUserPhone());
            System.out.println("생일 : " + vo.getUserBirth());
            System.out.println("닉네임 : " + vo.getUserNickname());
            System.out.println("성별 : " + vo.getUserGender());
            System.out.println("날짜 : " + vo.getUserDate());
            System.out.println("설명 : " + vo.getUserIntroduce());

            // 사용자 정보 등록
            User user = userService.insertUser(vo);

            Permission permission = new Permission();
            permission.setUserNum(user.getUserNum());
            permission.setPermissionMaster(1L);
            permission.setPermissionState(2L);
            permission.setIsPasswordLess(0L);

            System.out.println("Permission after setting: " + permission);
            permissionRepository.save(permission);

            return ResponseEntity.ok(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 비밀번호 업데이트
    @PostMapping("/updateuserpwd")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, Object> map) {

        String userEmail = map.get("userEmail").toString();
        String userPwd = map.get("userPwd").toString();
        System.out.println("비밀번호 : " + userPwd);
        try {
            if (userPwd != null) {
                userService.updateUserpwdAndUserEmail(userEmail, passwordEncoder.encode(userPwd));
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ((HeadersBuilder<BodyBuilder>) ResponseEntity.status(500).body("오류가 발생했습니다." + e.getMessage()))
                    .build();

        }
    }

    // 보안 질문 변경
    @PostMapping("/changeQs")
    public ResponseEntity<?> changeUserQs(@RequestBody Map<String, Object> map) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long user_num = Long.valueOf(auth.getPrincipal().toString());
        try {
            String userSecurityQuestion = map.get("userSecurityQuestion").toString();
            String userSecurityAnswer = map.get("userSecurityAnswer").toString();
            User user = userService.getDetail(user_num)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

            if (userSecurityQuestion != null)
                user.setUserSecurityQuestion(userSecurityQuestion);
            // 보안 질문 답변 변경
            if (userSecurityAnswer != null)
                user.setUserSecurityAnswer(userSecurityAnswer);
            userRepository.save(user);
            return ResponseEntity.ok("성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("업데이트 실패: " + e.getMessage());
        }
    }

    // 유저 비밀번호 변경
    @PostMapping("/changePwd")
    public ResponseEntity<?> changeUserPwd(@RequestBody Map<String, Object> map) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long user_num = Long.valueOf(auth.getPrincipal().toString());
        try {
            String userPwd = map.get("userPwd").toString();
            String newPwd = map.get("newPwd").toString();

            User user = userService.getDetail(user_num)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));
            // 현재 비밀번호 검증
            // 현재 비밀번호 검증
            if (!passwordEncoder.matches(userPwd, user.getUserPwd())) {
                return ResponseEntity.status(202).body("현재 비밀번호가 일치하지 않습니다.");
            }

            // 비밀번호 변경
            if (newPwd != null)
                user.setUserPwd(passwordEncoder.encode(newPwd));
            userRepository.save(user);
            return ResponseEntity.ok("성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("업데이트 실패: " + e.getMessage());
        }
    }

    // 유저 탈퇴하기
    @PostMapping("/delete/{userNum}")
    public ResponseEntity<?> deleteUser(@RequestBody Map<String, Object> map) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long user_num = Long.valueOf(auth.getPrincipal().toString());
            String userPwd = map.get("userPwd").toString();
            User user = userService.findByUserNum(user_num)
                    .orElseThrow(() -> new UsernameNotFoundException("유저가 없습니다"));

            if (!passwordEncoder.matches(userPwd, user.getUserPwd())) {
                return ResponseEntity.status(401).body("현재 비밀번호가 일치하지 않습니다.");
            } else {
                // DeleteService를 호출하여 권한 변경
                passwordEncoder.encode(userPwd);
                userService.deleteUserByUserNum(user_num);
                System.out.println("인코딩 된 입력한 패스워드" + user.getUserPwd());
                // 성공 응답
                return ResponseEntity.ok(user_num + " 번 유저가 탈퇴하였습니다.");
            }
        } catch (IllegalArgumentException e) {
            // 권한 정보가 없을 때
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            // 기타 예외 처리
            return ResponseEntity.internalServerError().body("An unexpected error occurred: " + e.getMessage());
        }
    }

}
