package com.waither.userservice.service.commandService;

import com.waither.userservice.converter.*;
import com.waither.userservice.dto.request.UserReqDto;
import com.waither.userservice.dto.response.KakaoResDto;
import com.waither.userservice.entity.*;
import com.waither.userservice.entity.enums.Season;
import com.waither.userservice.global.exception.CustomException;
import com.waither.userservice.global.jwt.dto.JwtDto;
import com.waither.userservice.global.jwt.userdetails.PrincipalDetails;
import com.waither.userservice.global.jwt.util.JwtUtil;
import com.waither.userservice.global.response.ErrorCode;
import com.waither.userservice.global.util.RedisUtil;
import com.waither.userservice.kafka.KafkaConverter;
import com.waither.userservice.kafka.KafkaDto;
import com.waither.userservice.kafka.KafkaService;
import com.waither.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.waither.userservice.service.commandService.SurveyService.getCurrentSeason;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final EmailService emailService;
    private final KafkaService kafkaService;

    private final RedisUtil redisUtil;
    private final JwtUtil jwtUtil;

    private static final String AUTH_CODE_PREFIX = "AuthCode_";
    private static final String VERIFIED_PREFIX = "Verified_";

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    // 회원가입
    public void signup(UserReqDto.SignUpRequestDto requestDto) {
        // Todo : 다시 풀기
//        if (!verifiedAccounts(requestDto.email())) {
//            throw new CustomException(ErrorCode.INVALID_Account);
//        }
        User newUser = UserConverter.toUser(requestDto, passwordEncoder);
        Region newRegion = Region.createRegion();
        Setting newSetting = Setting.createSetting();
        List<UserData> userDataList = UserData.createUserDataList(newUser);
        List<UserMedian> userMedianList = UserMedian.createUserMedianList(userDataList, newUser);

        // 연관관계 설정
        newSetting.setRegion(newRegion);
        newUser.setSetting(newSetting);
        newUser.setUserData(userDataList);
        newUser.setUserMedian(userMedianList);

//        // 초기값 Kafka 전송
        KafkaDto.InitialDataDto initialDataDto = KafkaConverter.toInitialData(newUser, newSetting, userMedianList);
        kafkaService.sendInitialData(initialDataDto);
        userRepository.save(newUser);
    }

    // 회원가입 (카카오)
    public void signup(KakaoResDto.UserInfoResponseDto userInfo) {

        User newUser = UserConverter.toUser(userInfo);
        Region newRegion = Region.createRegion();
        Setting newSetting = Setting.createSetting();
        List<UserData> userDataList = UserData.createUserDataList(newUser);
        List<UserMedian> userMedianList = UserMedian.createUserMedianList(userDataList, newUser);

        // 연관관계 설정
        newSetting.setRegion(newRegion);
        newUser.setSetting(newSetting);
        newUser.setUserData(userDataList);
        newUser.setUserMedian(userMedianList);

        // 초기값 Kafka 전송
        KafkaDto.InitialDataDto initialDataDto = KafkaConverter.toInitialData(newUser, newSetting, userMedianList);
        kafkaService.sendInitialData(initialDataDto);
        userRepository.save(newUser);
    }

    // OAuth용 토큰 발급
    public JwtDto provideTokenForOAuth(String email) {
        PrincipalDetails principalDetails = new PrincipalDetails(email, null, "ROLE_USER");
        return new JwtDto(
                jwtUtil.createJwtAccessToken(principalDetails),
                jwtUtil.createJwtRefreshToken(principalDetails));
    }

    // 재발급
    public JwtDto reissueToken(String refreshToken) {
        jwtUtil.isRefreshToken(refreshToken);
        return jwtUtil.reissueToken(refreshToken);
    }

    // 인증 번호 전송
    public void sendAuthCodeToEmail(String email) {
        this.checkDuplicatedEmail(email);

        String authCode = this.createAuthCode();
        String title = "[☀️Waither☀️] 이메일 인증 번호 : {" + authCode + "}";
        emailService.sendEmail(email, title, "authEmail", authCode);

        log.info(email);
        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
        redisUtil.save(AUTH_CODE_PREFIX + email,
                authCode, authCodeExpirationMillis, TimeUnit.MILLISECONDS);
    }

    // 임시 비밀번호 보내기
    public String sendTempPassword(String email) {
        // 전송
        String tempPassword = this.createTemporaryPassword();
        String title = "[☀️Waither☀️] 임시 비밀번호 : {" + tempPassword + "}";
        emailService.sendEmail(email, title, "tempPasswordEmail", tempPassword);

        return tempPassword;
    }

    // 회원가입 하려는 이메일로 이미 가입한 회원이 있는지 확인하는 메서드.
    // 만약 해당 이메일을 가진 회원이 존재하면 예외를 발생.
    private void checkDuplicatedEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXIST);
        }
    }
    public boolean isUserRegistered(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    // 회원 존재하는 지 확인
    public void checkUserExists(String email) {
        if (!userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }

    // 인증 번호 생성하는 메서드
    public String createAuthCode() {
        int lenth = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < lenth; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            log.debug("MemberService.createCode() exception occur");
            throw new CustomException(ErrorCode.NO_SUCH_ALGORITHM);
        }
    }

    //랜덤함수로 임시비밀번호 구문 만들기
    public String createTemporaryPassword() {
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        Random random = new Random();

        // 문자 세트에서 랜덤으로 10개의 문자를 선택하여 하나의 문자열로 결합
        return random.ints(10, 0, charSet.length)
                .mapToObj(idx -> String.valueOf(charSet[idx]))
                .collect(Collectors.joining());
    }

    // 인증 코드를 검증하는 메서드.
    public void verifyCode(String email, String authCode) {
        this.checkDuplicatedEmail(email);

        // 이메일 인증 요청하지 않았거나, 이메일 인증 요청한 후 유효기간 경과한 경우
        if (!(redisUtil.hasKey(AUTH_CODE_PREFIX + email))) {
            throw new CustomException(ErrorCode.AUTH_CODE_EXPIRED);
        }
        // 인증 번호 일치
        else if (redisUtil.get(AUTH_CODE_PREFIX + email).toString().equals(authCode)) {
            // 인증 성공 시 인증된 상태로 변경
            redisUtil.save(VERIFIED_PREFIX + email,
                    "verifiedAccounts", authCodeExpirationMillis, TimeUnit.MILLISECONDS);
        }
        // 그 외의 경우, 코드 잘못됨.
        else {
            throw new CustomException(ErrorCode.INVALID_CODE);
        }
    }

    // signup 과정 전에 인증된 email인지 확인하는 메서드
    public boolean verifiedAccounts(String email) {
        // 인증하지 않았거나, 인증 완료까지는 했지만 너무 시간이 경과한 경우
        if(!redisUtil.hasKey(VERIFIED_PREFIX + email)) {
            throw new CustomException(ErrorCode.AUTH_CODE_EXPIRED);
        }
        // hasKey(VERIFIED_PREFIX + email) 만 통과 하면 -> 인증 완료한 것
        return true;
    }

    // 임시 비밀번호로 비밀번호를 변경
    public void changeToTempPassword(String email, String tempPassword) {
        // 이메일로 사용자 조회
        userRepository.findByEmail(email).get().setPassword(passwordEncoder.encode(tempPassword));
    }

    // 현재 비밀번호 체크
    public void checkPassword(User user, String currentPassword) {
        // 현재 비밀번호가 일치하는지 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new CustomException(ErrorCode.CURRENT_PASSWORD_NOT_EQUAL);
        }
    }

    // 비밀번호 변경
    public void updatePassword(User user, String newPassword) {
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new CustomException(ErrorCode.CURRENT_PASSWORD_EQUAL);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 닉네임 변경
    public void updateNickname(User user, String nickanme) {
        user.setNickname(nickanme);

        // Kafka 전송
        KafkaDto.UserSettingsDto settingDto = KafkaConverter.toSettingDto(user, "nickanme", nickanme);
        kafkaService.sendUserSettings(settingDto);

        userRepository.save(user);
    }

    // 회원 삭제
    public void deleteUser(User user){
        userRepository.delete(user);
    }

}
