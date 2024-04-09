package com.kuit.conet.service;

import com.kuit.conet.auth.apple.AppleMemberProvider;
import com.kuit.conet.auth.kakao.KakaoMemberProvider;
import com.kuit.conet.domain.auth.Platform;
import com.kuit.conet.domain.member.Member;
import com.kuit.conet.dto.web.request.auth.LoginRequestDTO;
import com.kuit.conet.dto.web.request.auth.PutOptionTermAndNameRequestDTO;
import com.kuit.conet.dto.web.response.auth.TermAndNameResponseDTO;
import com.kuit.conet.dto.web.response.auth.MemberResponseDTO;
import com.kuit.conet.dto.web.response.auth.LoginResponseDTO;
import com.kuit.conet.repository.MemberRepository;
import com.kuit.conet.service.validator.MemberValidator;
import com.kuit.conet.utils.auth.JwtParser;
import com.kuit.conet.utils.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.kuit.conet.service.validator.AuthValidator.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final AppleMemberProvider appleMemberProvider;
    private final KakaoMemberProvider kakaoMemberProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtParser jwtParser;
    private static final int FIRST_INDEX = 0;

    @Value("${spring.user.default-image}")
    private String defaultMemberImg;

    public LoginResponseDTO appleLogin(LoginRequestDTO loginRequest, String clientIp) {
        MemberResponseDTO applePlatformMember = appleMemberProvider.getApplePlatformMember(loginRequest.getIdToken());
        return generateLoginResponse(Platform.APPLE, applePlatformMember.getEmail(), applePlatformMember.getPlatformId(), clientIp);
    }

    public LoginResponseDTO kakaoLogin(LoginRequestDTO loginRequest, String clientIp) {
        MemberResponseDTO kakaoPlatformMember = kakaoMemberProvider.getPayloadFromIdToken(loginRequest.getIdToken());
        return generateLoginResponse(Platform.KAKAO, kakaoPlatformMember.getEmail(), kakaoPlatformMember.getPlatformId(), clientIp);
    }

    private LoginResponseDTO generateLoginResponse(Platform platform, String email, String platformId, String clientIp) {
        List<Long> findMemberId = memberRepository.findByPlatformAndPlatformId(platform, platformId);

        //회원가입이 되어 있는 멤버
        if (!findMemberId.isEmpty()) {
            Member member = memberRepository.findById(findMemberId.get(FIRST_INDEX));
            MemberValidator.validateMemberExisting(member);

            return login(clientIp, member);
        }

        //회원가입이 필요한 멤버
        return signUp(platform, email, platformId, clientIp);
    }

    private LoginResponseDTO signUp(Platform platform, String email, String platformId, String clientIp) {
        // 회원가입이 필요한 멤버
        Member signUpMember = Member.createMember(email, defaultMemberImg, platform, platformId);
        memberRepository.save(signUpMember);

        log.info("회원가입 성공! 약관 동의 및 이름 입력이 필요합니다.");

        return getLoginResponse(signUpMember, clientIp, false);
    }

    private LoginResponseDTO login(String clientIp, Member member) {
        // 회원가입은 되어있는데, 필수 약관 동의 혹은 이름 입력이 되어있지 않은 유저
        if (!member.getServiceTerm() || member.getName() == null) {
            log.info("회원가입은 되어 있으나, 약관 동의 및 이름 입력이 필요합니다.");
            return getLoginResponse(member, clientIp, false);
        } else {
            // 이미 회원가입과 약관 동의 및 이름 입력이 모두 되어있는 유저
            log.info("로그인에 성공하였습니다.");
            return getLoginResponse(member, clientIp, true);
        }
    }

    private LoginResponseDTO getLoginResponse(Member targetMember, String clientIp, Boolean isRegistered) {
        String accessToken = jwtTokenProvider.createAccessToken(targetMember.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(targetMember.getId());
        // Redis 에 refresh token 저장
        redisTemplate.opsForValue().set(refreshToken, clientIp);

        return new LoginResponseDTO(targetMember.getEmail(), accessToken, refreshToken, isRegistered);
    }

    public TermAndNameResponseDTO agreeTermAndPutName(Long memberId, PutOptionTermAndNameRequestDTO nameRequest) {
        Member member = memberRepository.findById(memberId);
        member.agreeTermAndPutName(nameRequest.getName());

        return new TermAndNameResponseDTO(member);
    }

    public LoginResponseDTO regenerateToken(Long memberId, String refreshToken, String clientIp) {
        // Redis 에서 해당 refresh token 찾기
        String existingIp = redisTemplate.opsForValue().get(refreshToken);

        // 찾은 값의 validation 처리
        validateRefreshTokenExisting(existingIp);
        compareClientIpFromRedis(existingIp, clientIp);

        Member member = memberRepository.findById(memberId);

        return getLoginResponse(member, clientIp, true);
    }
}
