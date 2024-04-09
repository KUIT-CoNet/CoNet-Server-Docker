package com.kuit.conet.service;

import com.kuit.conet.dto.web.request.member.NameRequestDTO;
import com.kuit.conet.dto.web.response.member.StorageImgResponseDTO;
import com.kuit.conet.dto.web.request.team.TeamIdRequestDTO;
import com.kuit.conet.dto.web.response.member.MemberResponseDTO;
import com.kuit.conet.dto.web.response.team.GetTeamResponseDTO;
import com.kuit.conet.domain.member.Member;
import com.kuit.conet.domain.storage.StorageDomain;
import com.kuit.conet.repository.*;
import com.kuit.conet.service.validator.TeamValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.kuit.conet.service.validator.MemberValidator.validateActiveMember;
import static com.kuit.conet.service.validator.MemberValidator.validateMemberExisting;
import static com.kuit.conet.service.validator.TeamValidator.validateTeamExisting;
import static com.kuit.conet.service.StorageService.getFileName;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final TeamMemberRepository teamMemberRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final StorageService storageService;
    private final PlanMemberRepository planMemberRepository;
    private final PlanMemberTimeRepository planMemberTimeRepository;

    @Value("${spring.user.default-image}")
    private String defaultMemberImg;

    public StorageImgResponseDTO updateImg(Long memberId, MultipartFile file) {
        Member member = memberRepository.findById(memberId);
        validateMemberExisting(member);
        validateActiveMember(member);

        storageService.deletePreviousImage(memberId);

        // 저장할 파일명 만들기
        // 새로운 이미지 S3에 업로드
        String imgUrl = storageService.uploadToS3(file, getFileName(file, StorageDomain.MEMBER));

        // 변경감지로 update
        member.updateImgUrl(imgUrl);
        return memberRepository.getImgUrlResponse(memberId);
    }

    public void updateName(Long memberId, NameRequestDTO nameRequest) {
        Member member = memberRepository.findById(memberId);
        validateMemberExisting(member);
        validateActiveMember(member);

        member.updateName(nameRequest.getName());
    }

    public MemberResponseDTO getMember(Long memberId) {
        Member member = memberRepository.findById(memberId);
        validateMemberExisting(member);
        validateActiveMember(member);

        String fileName = storageService.getFileNameFromUrl(member.getImgUrl());
        // S3에 없는 객체에 대한 유효성 검사
        if (!storageService.isExistImage(fileName)) {
            log.warn("S3 버킷에 존재하지 않는 이미지입니다. 기본 이미지로 변경하겠습니다.");
            // 변경감지로 update
            member.updateImgUrl(defaultMemberImg);
        }

        return new MemberResponseDTO(member);
    }

    public List<GetTeamResponseDTO> getBookmarks(Long memberId) {
        Member member = memberRepository.findById(memberId);
        validateMemberExisting(member);
        validateActiveMember(member);

        List<GetTeamResponseDTO> teamResponses = memberRepository.getBookmarks(memberId);

        return teamResponses;
    }

    public String bookmarkTeam(Long memberId, TeamIdRequestDTO request) {
        Member member = memberRepository.findById(memberId);
        validateMemberExisting(member);
        validateActiveMember(member);

        Long teamId = request.getTeamId();
        validateTeamExisting(teamRepository.findById(teamId));

        // 유저가 팀에 참가 중인지 검사
        TeamValidator.validateMemberIsTeamMember(teamMemberRepository, teamId, memberId);

        teamMemberRepository.bookmarkTeam(memberId, teamId);

        if (teamMemberRepository.isBookmark(memberId, teamId)) {
            return "모임을 즐겨찾기에 추가하였습니다.";
        } else {
            return "모임을 즐겨찾기에서 삭제하였습니다.";
        }
    }

    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId);
        validateMemberExisting(member);
        validateActiveMember(member);

        // S3 에서 프로필 이미지 객체 삭제
        storageService.deletePreviousImage(memberId);

        int deletedTeamMemberCount = teamMemberRepository.deleteTeamMemberByMemberId(memberId);
        planMemberTimeRepository.deleteOnPlanByMemberId(memberId);
        int deletedPlanMemberCount = planMemberRepository.deleteOnPlanByMemberId(memberId);

        log.info(deletedTeamMemberCount + "");
        log.info(deletedPlanMemberCount + "");

        memberRepository.deleteMember(memberId);
    }
}
