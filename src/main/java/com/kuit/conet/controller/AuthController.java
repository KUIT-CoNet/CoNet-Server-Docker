package com.kuit.conet.controller;

import com.kuit.conet.annotation.ClientIp;
import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.web.request.auth.LoginRequestDTO;
import com.kuit.conet.dto.web.response.auth.LoginResponseDTO;
import com.kuit.conet.jpa.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    // 애플 로그인
    @PostMapping("/login/apple")
    public BaseResponse<LoginResponseDTO> loginApple(@RequestBody @Valid LoginRequestDTO loginRequest, @ClientIp String clientIp) {
        LoginResponseDTO response = authService.appleLogin(loginRequest, clientIp);
        return new BaseResponse<LoginResponseDTO>(response);
    }

    // 카카오 로그인
    @PostMapping("/login/kakao")
    public BaseResponse<LoginResponseDTO> loginKakao(@RequestBody @Valid LoginRequestDTO loginRequest, @ClientIp String clientIp) {
        LoginResponseDTO response = authService.kakaoLogin(loginRequest, clientIp);
        return new BaseResponse<LoginResponseDTO>(response);
    }

/*    @PostMapping("/regenerate-token")
    public BaseResponse<LoginResponseDTO> regenerateToken(HttpServletRequest httpRequest, @ClientIp String clientIp) {
        LoginResponseDTO response = authService.regenerateToken((String) httpRequest.getAttribute("token"), clientIp);
        return new BaseResponse<LoginResponseDTO>(response);
    }*/

/*    // 이용 약관 동의 및 이름 입력 DB 업데이트
    @PostMapping("/term-and-name")
    public BaseResponse<AgreeTermAndPutNameResponseDTO> agreeTermAndPutName(@RequestBody @Valid PutOptionTermAndNameRequestDTO nameRequest, @UserId Long userId, @ClientIp String clientIp) {
        AgreeTermAndPutNameResponseDTO response = authService.agreeTermAndPutName(nameRequest, userId, clientIp);
        return new BaseResponse<>(response);
    }

    @PostMapping("/option-term")
    public BaseResponse<String> updateOptionTerm(@RequestBody @Valid OptionTermRequestDTO optionTermRequest, @UserId Long userId) {
        authService.updateOptionTerm(optionTermRequest, userId);
        return new BaseResponse<>("선택 약관의 선택 여부 변경을 성공하였습니다.");
    }*/
}