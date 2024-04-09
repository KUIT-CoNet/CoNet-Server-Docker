package com.kuit.conet.controller;

import com.kuit.conet.annotation.MemberId;
import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.web.request.member.NameRequestDTO;
import com.kuit.conet.dto.web.response.member.StorageImgResponseDTO;
import com.kuit.conet.dto.web.request.team.TeamIdRequestDTO;
import com.kuit.conet.dto.web.response.member.MemberResponseDTO;
import com.kuit.conet.dto.web.response.team.GetTeamResponseDTO;
import com.kuit.conet.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    @GetMapping
    public BaseResponse<MemberResponseDTO> getMember(@MemberId Long memberId) {
        MemberResponseDTO response = memberService.getMember(memberId);
        return new BaseResponse<>(response);
    }

    @PostMapping("/image")
    public BaseResponse<StorageImgResponseDTO> updateImg(@MemberId Long memberId, @RequestPart(value = "file") MultipartFile file) {
        StorageImgResponseDTO response = memberService.updateImg(memberId, file);
        return new BaseResponse<>(response);
    }

    @PostMapping("/name")
    public BaseResponse<String> updateName(@MemberId Long memberId, @RequestBody @Valid NameRequestDTO nameRequest) {
        memberService.updateName(memberId, nameRequest);
        return new BaseResponse<>("이름 변경에 성공하였습니다.");
    }

    @GetMapping("/bookmark")
    public BaseResponse<List<GetTeamResponseDTO>> getBookmark(@MemberId Long memberId) {
        List<GetTeamResponseDTO> responses = memberService.getBookmarks(memberId);
        return new BaseResponse<>(responses);
    }

    @PostMapping("/bookmark")
    public BaseResponse<String> bookmarkTeam(@MemberId Long memberId, @RequestBody @Valid TeamIdRequestDTO request) {
        return new BaseResponse<>(memberService.bookmarkTeam(memberId, request));
    }

    @DeleteMapping
    public BaseResponse<String> deleteMember(@MemberId Long memberId) {
        memberService.deleteMember(memberId);
        return new BaseResponse<>("유저 탈퇴에 성공하였습니다.");
    }
}