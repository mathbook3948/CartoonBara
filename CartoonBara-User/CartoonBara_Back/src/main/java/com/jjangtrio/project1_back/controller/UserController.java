package com.jjangtrio.project1_back.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jjangtrio.project1_back.entity.Permission;
import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.repository.PermissionRepository;
import com.jjangtrio.project1_back.repository.UserRepository;
import com.jjangtrio.project1_back.service.UserService;
import com.jjangtrio.project1_back.vo.UserVO;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads");

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRepository permissionRepository;
    
    // otherpage 유저 프로필 보기
    @GetMapping("/oneuser/{userNum}")
    public ResponseEntity<?> getUserByUserNum(@PathVariable Long userNum) {
        try {
            User user = userService.getUserByUserNum(userNum);
            return ResponseEntity.ok(user); // 정상적으로 조회된 사용자 목록을 200 OK로 반환
        } catch (Exception e) {
            if (e instanceof UsernameNotFoundException) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(500).build();
            }
        }
    }

    @PostMapping("/oneUserNum") // 사용자 한명 출력
    public ResponseEntity<?> getUserByUserNum() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long user_num = Long.valueOf(auth.getPrincipal().toString());
            User user = userService.getUserByUserNum(user_num);
            return ResponseEntity.ok(user); // 정상적으로 조회된 사용자 목록을 200 OK로 반환
        } catch (Exception e) {
            if (e instanceof UsernameNotFoundException) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(500).build();
            }
        }
    }

    // 사용자 모두 출력
    @PostMapping("/listall")
    public List<User> getAllUsers() {
        return userService.getAllUsersOrderedByUserNumAsc();
    }

    // userId로 회원한명 출력
    @PostMapping("/oneUserId")
    public List<User> getUserByUserId(@RequestBody UserVO vo) {
        System.out.println("아이디 : " + vo.getUserId());
        return userService.getUserByUserId(vo.getUserId());
    }

    // id체크
    @PostMapping("/idcheck")
    public boolean existsByUserId(@RequestBody UserVO vo) {
        System.out.println("아이디 : " + vo.getUserId());
        return userService.existsByUserId(vo.getUserId());
    }

    // 사용자 등록
    @PostMapping("/insert")
    public ResponseEntity<?> insertUser(@RequestBody UserVO vo) {
        System.out.println("UserVO: " + vo); // 여기에 추가

        Map<String, Boolean> conflictMap = new HashMap<>();

        // 닉네임 중복 확인
        if (userService.existsByUserNickname(vo.getUserNickname())) {
            conflictMap.put("nicknameConflict", true);
        } else {
            conflictMap.put("nicknameConflict", false);
        }

        // 전화번호 중복 확인
        if (userService.existsByUserPhone(vo.getUserPhone())) {
            conflictMap.put("phoneConflict", true);
        } else {
            conflictMap.put("phoneConflict", false);
        }

        // 중복이 있다면 409 상태와 함께 상세 정보 반환
        if (conflictMap.get("nicknameConflict") || conflictMap.get("phoneConflict")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(conflictMap);
        }
        // 사용자 정보 등록
        User user = userService.insertUser(vo);
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
        if (vo.getUserEmail() == null || !vo.getUserEmail()
                .matches("[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}")) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다. 영어만 입력해주세요");
        }
        if (vo.getUserPwd().length() < 8) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
        }

        // 정상 처리
        return ResponseEntity.ok(user);
    }

    // 이메일 체크 or 전송
    @PostMapping("/emailcheck")
    public ResponseEntity<Map<String, String>> sendEmail(@RequestBody Map<String, String> requestBody) {
        String userEmail = requestBody.get("userEmail"); // 이메일 추출
        // 이메일을 받아서 인증 코드 발송 서비스 호출
        Map<String, String> response = userService.sendEmail(userEmail);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }

    // 회원가입 - 인증번호 확인 코드
    @PostMapping("/verifyCode")
    public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody Map<String, String> requestBody) {
        String userEmail = requestBody.get("userEmail");
        String inputCode = requestBody.get("code");

        Map<String, Object> response = new HashMap<>();

        // UserService에서 passcode를 검증
        if (!userService.verifyPasscode(userEmail, inputCode)) {
            response.put("valid", false);
            response.put("message", "인증번호가 일치하지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("valid", true);
        response.put("message", "인증번호가 확인되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 사용자에게 이메일로 아이디 전송
    @PostMapping("/sendUserIdViaEmail")
    public ResponseEntity<Map<String, String>> sendUserIdViaEmail(@RequestBody Map<String, String> requestBody) {
        String userName = requestBody.get("userName"); // 사용자 이름 추출
        String userEmail = requestBody.get("userEmail"); // 이메일 추출

        // 이메일을 받아서 아이디 발송 서비스 호출
        Map<String, String> response = userService.sendUserIdViaEmail(userName, userEmail);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/sendEmailPasswordFind")
    public ResponseEntity<?> sendEmailPasswordFind(@RequestBody Map<String, String> requestBody) {
        String userEmail = requestBody.get("userEmail");
        String userId = requestBody.get("userId");

        if (!userRepository.existsByUserEmail(userEmail) || !userRepository.existsByUserId(userId)) {
            return ResponseEntity.status(400).body("이메일 또는 아이디가 찾을 수 없습니다.");
        }

        // 이메일과 아이디가 모두 유효하면 인증번호 생성 및 전송
        Map<String, String> response = userService.sendEmailPasswordReset(userEmail, userId);
        return ResponseEntity.ok(response.get("passcode"));
    }

    // 사용자 정보 수정
    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserVO vo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long user_num = Long.valueOf(auth.getPrincipal().toString());
        try {
            User user = userService.getDetail(user_num)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));
            // 유저 정보 수정
            if (vo.getUserName() != null)
                user.setUserName(vo.getUserName());
            if (vo.getUserNickname() != null)
                user.setUserNickname(vo.getUserNickname());
            if (vo.getUserIntroduce() != null)
                user.setUserIntroduce(vo.getUserIntroduce());

            userRepository.save(user);
            return ResponseEntity.ok("성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("업데이트 실패: " + e.getMessage());
        }
    }

    @PostMapping("/imgUpdate")
    public ResponseEntity<?> imgUpdate(@RequestParam("userNum") String userNum,
            @RequestParam("file") MultipartFile file) {

        if (userNum == null) {
            return ResponseEntity.badRequest().body("userNum is required.");
        }
        Long userNumLong = Long.parseLong(userNum);
        try {
            User user = userService.getDetail(userNumLong)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

            // 프로필 사진 업로드
            if (file != null && !file.isEmpty()) {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String fileName = userNum + "_" + "image.png";
                Path filePath = uploadPath.resolve(fileName);
                file.transferTo(filePath.toFile());
                user.setUserImage(fileName);
            }

            userRepository.save(user);
            return ResponseEntity.ok("성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("업데이트 실패: " + e.getMessage());
        }
    }

    @PostMapping("/getUsername")
    public ResponseEntity<?> getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        try {
            User user = userRepository.findById(Long.parseLong(auth.getPrincipal().toString()))
                    .orElseThrow(() -> new RuntimeException("알 수 없는 오류가 발생했습니다"));

            Permission permission = permissionRepository.findById(user.getUserNum())
                    .orElseThrow(() -> new RuntimeException("알 수 없는 오류가 발생했습니다"));
            Map<String, Object> result = new HashMap<>();

            result.put("username", user.getUserNickname());
            result.put("userNum", user.getUserNum());
            result.put("permission", permission.getPermissionMaster());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/kakaoCheck")
    public ResponseEntity<?> kakaoCheck(@RequestBody Map<String, Object> map) {
        try {
            String email = map.get("userEmail").toString();
            return ResponseEntity.ok(userService.findByUserEmailAndReturnToken(email));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(202).build();
        }
    }

    // 마이페이지에서 한줄소개 작성
    @PostMapping("/introduce")
    public ResponseEntity<?> introUser(@RequestBody UserVO vo) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long user_num = Long.valueOf(auth.getPrincipal().toString());
        try {
            System.out.println(vo.getUserIntroduce());
            User user = userService.getDetail(user_num).orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));
            user.setUserIntroduce(vo.getUserIntroduce());
            userRepository.save(user);
            return ResponseEntity.ok("한줄소개 작성이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/userPasswordLessPermission")
    public ResponseEntity<?> userPasswordLessPermission(@RequestParam String userId) {
        try {
            return ResponseEntity.ok(userService.getPasswordLessPermission(userId));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // 김진헌
    @GetMapping("/userPasswordLessPermission/userNum")
    public ResponseEntity<?> userPasswordLessPermissionUserNum() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long user_num = Long.valueOf(auth.getPrincipal().toString());
        try {
            return ResponseEntity.ok(userService.getPasswordLessPermissionUserNum(user_num));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/handlePasswordLessCancel")
    public ResponseEntity<?> handlePasswordLessCancel() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long user_num = Long.valueOf(auth.getPrincipal().toString());
        try {
            userService.handlePasswordLessCancel(user_num);
            return ResponseEntity.ok("성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).build();

        }
    }

    @PostMapping("/setNewPassword")
    public ResponseEntity<?> setNewPassword(@RequestBody Map<String, Object> map) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long user_num = Long.valueOf(auth.getPrincipal().toString());

        try {
            userService.setNewPassword((String) map.get("pwd"), user_num);

            return ResponseEntity.ok("성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    // ======================================================================

}
