package com.jjangtrio.project1_back.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jjangtrio.project1_back.entity.Permission;
import com.jjangtrio.project1_back.entity.User;
import com.jjangtrio.project1_back.repository.PermissionRepository;
import com.jjangtrio.project1_back.repository.UserRepository;
import com.jjangtrio.project1_back.security.JwtService;
import com.jjangtrio.project1_back.security.PermissionRole;
import com.jjangtrio.project1_back.security.UserForLogin;
import com.jjangtrio.project1_back.security.UserRole;
import com.jjangtrio.project1_back.vo.PageVO;
import com.jjangtrio.project1_back.vo.UserVO;
import com.jjangtrio.project1_back.vo.WebtoonVO;
import com.jjangtrio.project1_back.vo.Webtoon_ReviewVO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private JwtService jwtService;

    private String passcode; // 회원가입 인증번호
    private String passcodeForPasswordReset; // 비밀번호 찾기 인증번호

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // userNum 으로 회원 1명 출력하기
    public User getUserByUserNum(Long userNum) {
        return userRepository.findById(userNum).orElseThrow(() -> new UsernameNotFoundException("해당 유저가 없습니다"));
    }

    // 회원 오름차순 가져오기
    public List<User> getAllUsersOrderedByUserNumAsc() {
        return userRepository.findByOrderByUserNumAsc();
    }

    // userId로 회원한명 출력
    public List<User> getUserByUserId(String userId) {

        return userRepository.findByUserId(userId);
    }

    // id체크
    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    // 닉네임으로 사용자 존재 여부 확인
    public boolean existsByUserNickname(String nickname) {
        return userRepository.existsByUserNickname(nickname);
    }

    // 전화번호로 사용자 존재 여부 확인
    public boolean existsByUserPhone(String phone) {
        return userRepository.existsByUserPhone(phone);
    }

    public boolean verifyPasscode(String userEmail, String inputCode) {
        // 현재 서비스 내 저장된 passcode와 비교
        return inputCode != null && inputCode.equals(this.passcode);
    }

    // 회원가입
    public User insertUser(UserVO vo) {
        User user = new User();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (vo.getUserBirth() != null && !vo.getUserBirth().toString().isEmpty()) {
                Date userBirthDate = sdf.parse(vo.getUserBirth().toString());
                user.setUserBirth(userBirthDate);
            }
            user.setUserName(vo.getUserName());
            System.out.println("이름 : " + vo.getUserName());
            user.setUserId(vo.getUserId());
            user.setUserPwd(passwordEncoder.encode(vo.getUserPwd()));
            user.setUserLevel(1L);
            user.setUserEmail(vo.getUserEmail());
            user.setUserPhone(vo.getUserPhone());
            user.setUserSecurityQuestion(vo.getUserSecurityQuestion());
            user.setUserSecurityAnswer(vo.getUserSecurityAnswer());
            user.setUserImage("user.png");
            user.setUserNickname(vo.getUserNickname());
            user.setUserDate(new java.util.Date());
            user.setUserIntroduce("");
            user.setUserSingoAccumulated(0L); // =========================================================> 초기화
            if (vo.getUserGender() == 0) {
                user.setUserGender(0L);
            } else {
                user.setUserGender(1L);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return userRepository.save(user);
    }

    // 이메일 체크및 전송
    public void existsByUserEmail(String userEmail) {
        // 이메일이 이미 존재하면 인증코드를 생성하지 않음
        if (userRepository.existsByUserEmail(userEmail)) {
            this.passcode = null; // 이메일이 존재하면 인증 코드 생성 안 함
        } else {
            // 이메일이 없으면 인증코드 생성
            int length = 6; // 인증코드를 6자로 설정
            StringBuilder authCode = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < length; i++) {
                int type = random.nextInt(3); // 0, 1, 2
                switch (type) { // 스위치문으로 랜덤한 숫자, 문자를 생성
                    case 0:
                        authCode.append(random.nextInt(10)); // 정수 0 ~ 9
                        break;
                    case 1:
                        authCode.append((char) (random.nextInt(26) + 65)); // A 알파벳 대문자
                        break;
                    case 2:
                        authCode.append((char) (random.nextInt(26) + 97)); // a 알파벳 소문자 생성
                        break;
                }
            }
            this.passcode = authCode.toString(); // 인증코드를 생성하여 설정
        }
    }

    public Map<String, String> sendEmail(String userEmail) {
        // 이메일 존재 여부 확인 및 인증 코드 생성
        existsByUserEmail(userEmail);

        Map<String, String> response = new HashMap<>();
        response.put("userEmail", userEmail);

        if (this.passcode != null) {
            // passcode가 존재하면 이메일 전송
            MimeMessage message = mailSender.createMimeMessage();
            try {
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom("leejungwoo00000@naver.com");
                helper.setTo(userEmail);
                helper.setSubject("ICTStudy의 X팀의 회원가입 인증번호 발송");

                String body = "<html>" + "<body>" + "<h1>장트리오웹툰의 인증번호 발송!</h1>"
                        + "<p>회원가입을 완료하기 위해 인증코드를 입력해주세요.</p>"
                        + "<p>인증코드: <strong>" + this.passcode + "</strong></p>"
                        + "</body>" + "</html>";

                helper.setText(body, true);
                mailSender.send(message);
                // userEmailDAO.saveUserEmail(userEmail, this.passcode); // 이메일과 인증코드 저장 로직 필요시
                // 활성화

                response.put("passcode", this.passcode); // 인증 코드 포함
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        } else {
            // 이메일이 이미 존재하면 passcode는 null로 설정
            response.put("passcode", null);
        }
        return response;
    }

    // 아이디 찾기_유저의 이름과 이메일을 받아서 이메일로 아이디를 전송
    public Map<String, String> sendUserIdViaEmail(String userName, String userEmail) {
        // 사용자 정보 조회
        User vo = userRepository.findByUserNameAndUserEmail(userName, userEmail);
        Map<String, String> response = new HashMap<>();
        // 사용자 정보가 존재하는지 확인
        if (vo != null) {
            // 사용자 아이디(ID) 발송
            String userId = vo.getUserId();
            sendUserIdEmail(userEmail, userId);

            // 결과 반환
            response.put("userName", userName);
            response.put("userEmail", userEmail);
            return response;
        } else {
            // 사용자 정보가 존재하지 않는지 확인 없으면 null을 전송
            response.put("userName", null);
            response.put("userEmail", null);
            return response;
        }
    }

    // 아이디를 이메일로 전송하는 메서드
    private void sendUserIdEmail(String toEmail, String userId) {
        try {
            // 이메일 메시지 생성
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // 이메일 설정
            helper.setFrom("leejungwoo00000@naver.com");
            helper.setTo(toEmail);
            helper.setSubject("아이디 찾기 결과");
            helper.setText("안녕하세요,\n\n귀하의 아이디는 '" + userId + "' 입니다.\n\n감사합니다.", true);

            // 이메일 전송
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("이메일 전송 중 오류가 발생했습니다.");
        }
    }

    // 비밀번호 찾기_인증번호 전송
    public Map<String, String> sendEmailPasswordReset(String userEmail, String userId) {
        Map<String, String> response = new HashMap<>();

        // DB에서 이메일과 아이디로 사용자 조회 및 존재 여부 확인
        if (!userRepository.existsByUserEmailAndUserId(userEmail, userId)) {
            response.put("message", "등록되지 않은 이메일 또는 아이디입니다.");
            System.out.println("No user found with email: " + userEmail + " and ID: " + userId);
            return response;
        }

        // 인증코드 생성
        String passcode = generatePasscodeForPasswordReset();
        passcodeForPasswordReset = passcode; // passcodeForPasswordReset에 인증번호 저장
        redisTemplate.opsForValue().set(userId, passcode);

        // 인증코드 이메일 전송 로직
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("leejungwoo00000@naver.com");
            helper.setTo(userEmail);
            helper.setSubject("Password Reset Code");
            String body = "<html>" + "<body>" + "<h1>비밀번호 초기화를 위한 인증번호</h1>"
                    + "<p>비밀번호 재설정을 완료하기 위해 아래의 인증코드를 입력해주세요.</p>"
                    + "<p>인증코드: <strong>" + passcode + "</strong></p>"
                    + "</body>" + "</html>";
            helper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Passcode sent to email: " + userEmail);
        response.put("passcode", passcode); // 인증 코드 포함
        return response;
    }

    // 비밀번호 찾기_인증번호 전송
    private String generatePasscodeForPasswordReset() {
        StringBuilder authCode = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int type = random.nextInt(3);
            switch (type) {
                case 0:
                    authCode.append(random.nextInt(10));
                    break;
                case 1:
                    authCode.append((char) (random.nextInt(26) + 65));
                    break;
                case 2:
                    authCode.append((char) (random.nextInt(26) + 97));
                    break;
            }
        }
        return authCode.toString();
    }

    // 인증번호 검증
    public boolean verifyPasscodeForPasswordReset(String inputCode) {
        System.out.println("입력된 인증번호: " + inputCode);
        System.out.println("저장된 인증번호: " + passcodeForPasswordReset);
        return inputCode != null && inputCode.equals(passcodeForPasswordReset);
    }

    public boolean updatePassword(UserVO vo, String inputCode) {
        User user = userRepository.findByUserEmail(vo.getUserEmail());
        if (user == null) {
            System.out.println("사용자를 찾을 수 없습니다: " + vo.getUserEmail());
            return false;
        }

        // 입력된 인증번호와 서버에 저장된 인증번호 비교
        if (inputCode != null && inputCode.equals(this.passcodeForPasswordReset)) {
            user.setUserPwd(vo.getUserPwd()); // VO에서 새로운 비밀번호 가져오기
            userRepository.save(user); // DB에 저장
            System.out.println("비밀번호가 업데이트되었습니다: " + vo.getUserPwd());
            return true;
        } else {
            System.out.println("인증번호가 일치하지 않습니다.");
            return false;
        }
    }

    // 유저 정보 수정
    public User updateUser(UserVO vo) {
        User user = new User();
        user.setUserName(vo.getUserName());
        user.setUserNickname(vo.getUserNickname());
        user.setUserPhone(vo.getUserPhone());
        user.setUserImage(vo.getUserImage());
        return userRepository.save(user);
    }

    public Optional<User> getDetail(Long userNum) {
        return userRepository.findById(userNum);
    }

    // 마이페이지에서 한줄소개 작성
    public User introUser(UserVO vo) {
        User user = new User();
        user.setUserIntroduce(vo.getUserIntroduce());
        return userRepository.save(user);
    }

    public String findByUserEmailAndReturnToken(String email) {
        User user = userRepository.findByUserEmail(email);

        Permission permission = permissionRepository.findById(user.getUserNum())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(PermissionRole.getRole(permission.getPermissionMaster())));
        authorities.add(new SimpleGrantedAuthority(UserRole.getRole(permission.getPermissionState())));

        UserForLogin ufl = UserForLogin.builder().userNum(user.getUserNum()).username(user.getUserNickname())
                .authorities(authorities).build();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(ufl, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(auth);

        String token = jwtService.createToken();

        return token;
    }

    // 이메일에 맞는 유저를 찾아서 비밀번호 업데이트
    public User updateUserpwdAndUserEmail(String userEmail, String userPwd) {
        User user = userRepository.findByUserEmail(userEmail);
        user.setUserPwd(userPwd);
        return userRepository.save(user);
    }

    // ===========================================================editor 관련
    public User getUser(Long userNum) {
        User user = userRepository.findById(userNum)
                .orElseThrow(() -> new RuntimeException("해당 번호의 USER가 없습니다"));
        return user;
    }

    // 전체 에디터 조회
    public Long getUserCount() {
        return userRepository.count();
    }

    public List<User> getAllUser(Long EditorNum) {
        return userRepository.findAll();
    }

    public List<User> getAllUsersOrderByUserNumAsc() {
        return userRepository.findAllByOrderByUserNumAsc();
    }

    public String getStoredPassword(Long userNum) {
        return userRepository.findPasswordByUserNum(userNum); // 해당 userNum에 해당하는 비밀번호 조회
    }

    public void deleteUserByUserNum(Long userNum) throws UsernameNotFoundException {

        // 권한 조회
        Permission permission = permissionRepository.findById(userNum)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found for userNum: " + userNum));

        // 권한 업데이트
        permission.setPermissionState(4L); // 권한 상태를 4번으로 설정
        permissionRepository.save(permission);
        System.out.println("Permission updated to ROLE_DELETED for userNum: " + userNum);
    }

    public Optional<User> findByUserNum(Long userNum) {
        return userRepository.findById(userNum);
    }

    public Long getPasswordLessPermission(String userId) {
        User user = userRepository.findByUserId(userId).get(0);

        Permission permission = permissionRepository.findById(user.getUserNum())
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저 없음"));
        return permission.getIsPasswordLess();
    }

    // 김진헌
    public Long getPasswordLessPermissionUserNum(Long userNum) {
        User user = userRepository.findById(userNum)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없음"));
        Permission permission = permissionRepository.findById(user.getUserNum())
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저 없음"));
        return permission.getIsPasswordLess();
    }

    public void handlePasswordLessCancel(Long userNum) {
        User user = userRepository.findById(userNum)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없음"));
        Permission permission = permissionRepository.findById(user.getUserNum())
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저 없음"));
        if (permission.getIsPasswordLess() != 1L) {
            throw new RuntimeException("패스워드리스 해지 대상이 아닙니다.");
        }
        permission.setIsPasswordLess(0L);
        permissionRepository.save(permission);
    }

    public void setNewPassword(String pwd, Long userNum) {
        User user = userRepository.findById(userNum)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없음"));

        user.setUserPwd(passwordEncoder.encode(pwd));

        userRepository.save(user);
    }
}
