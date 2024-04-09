package com.kuit.conet.service;

import com.kuit.conet.domain.storage.StorageDomain;
import com.kuit.conet.domain.team.Team;
import com.kuit.conet.domain.team.TeamMember;
import com.kuit.conet.dto.web.request.team.CreateTeamRequestDTO;
import com.kuit.conet.dto.web.request.team.JoinTeamRequestDTO;
import com.kuit.conet.dto.web.request.team.TeamIdRequestDTO;
import com.kuit.conet.dto.web.request.team.UpdateTeamRequestDTO;
import com.kuit.conet.dto.web.response.member.StorageImgResponseDTO;
import com.kuit.conet.dto.web.response.team.*;
import com.kuit.conet.domain.member.Member;
import com.kuit.conet.repository.*;
import com.kuit.conet.service.validator.MemberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.kuit.conet.service.validator.TeamValidator.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TeamService {
    static final int ASCII_NUMBER_NINE = 57;
    static final int ASCII_UPPERCASE_A = 65;
    static final int ASCII_UPPERCASE_Z = 90;
    static final int ASCII_LOWERCASE_A = 97;
    private final int LEFT_LIMIT = 48;
    private final int RIGHT_LIMIT = 122;
    private final int TARGET_STRING_LENGTH = 8;
    private final String SUCCESS_LEAVE_TEAM = "모임 탈퇴에 성공하였습니다.";
    private final String SUCCESS_DELETE_TEAM = "모임 삭제에 성공하였습니다.";
    private final StorageService storageService;
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final PlanMemberRepository planMemberRepository;
    private final PlanMemberTimeRepository planMemberTimeRepository;
    private final TeamMemberRepository teamMemberRepository;

    public CreateTeamResponseDTO createTeam(CreateTeamRequestDTO teamRequest, Long memberId, MultipartFile file) {
        // 초대 코드 생성 및 코드 중복 확인
        String inviteCode = getRandomInviteCode();

        // 이미지 파일 존재 여부 확인
        validateFile(file);

        // 모임 생성 시간 찍기
        LocalDateTime codeGeneratedTime = LocalDateTime.now();

        // 팀 만든 멤버 정보 추출
        Member teamCreator = memberRepository.findById(memberId);
        MemberValidator.validateMemberExisting(teamCreator);

        // 이미지 s3 업로드
        String imgUrl = updateTeamImg(file);

        // team 생성
        Team newTeam = Team.createTeam(teamRequest.getTeamName(), inviteCode, codeGeneratedTime, teamCreator, imgUrl);
        teamRepository.save(newTeam);

        return new CreateTeamResponseDTO(newTeam);
    }

    public JoinTeamResponseDTO joinTeam(JoinTeamRequestDTO teamRequest, Long memberId) {
        // 필요한 정보 조회
        String inviteCode = getInviteCodeFromRequest(teamRequest);
        Member member = memberRepository.findById(memberId);
        Team team = teamRepository.findByInviteCode(inviteCode);

        // 모임에 이미 존재하는 회원인지 확인
        validateNewMemberInTeam(teamRepository, memberId, team);

        // 초대 코드 생성 시간과 모임 참가 요청 시간 비교
        compareInviteCodeAndRequestTime(team);

        // team에 teamMember 추가 (변경 감지)
        TeamMember.createTeamMember(team, member);

        return new JoinTeamResponseDTO(team, member);
    }

    public List<GetTeamResponseDTO> getTeam(Long memberId) {
        List<Team> teams = teamRepository.findByMemberId(memberId);

        return generateTeamReturnResponse(teams, memberId);
    }


    public String leaveTeam(TeamIdRequestDTO teamRequest, Long memberId) {
        Team team = teamRepository.findById(teamRequest.getTeamId());

        // 모임 존재 여부 확인
        validateTeamExisting(team);

        // 팀에 존재하는 멤버인지 확인
        validateMemberIsTeamMember(teamMemberRepository, team.getId(), memberId);

        //변경 감지 이용
        TeamMember teamMember = teamMemberRepository.findByTeamIdAndMemberId(team.getId(), memberId);
        team.deleteMember(teamMember);

        return SUCCESS_LEAVE_TEAM;
    }

    public String deleteTeam(Long teamId, Long memberId) {
        Team team = teamRepository.findById(teamId);

        // 모임 존재 여부 확인
        validateTeamExisting(team);

        // 모임 삭제 권한이 있는지 확인
        validateMemberIsTeamMember(teamMemberRepository, teamId, memberId);

        //image 삭제
        deleteImage(teamId);

        teamMemberRepository.deleteTeamMemberByTeamId(teamId);
        planMemberTimeRepository.deleteByTeamId(teamId);
        planMemberRepository.deleteByTeamId(teamId);
        planRepository.deletePlanByTeamId(teamId);
        teamRepository.deleteTeam(teamId);

        return SUCCESS_DELETE_TEAM;
    }

    public StorageImgResponseDTO updateTeam(UpdateTeamRequestDTO teamRequest, Long memberId, MultipartFile file) {
        String fileName = storageService.getFileName(file, StorageDomain.TEAM);
        Team team = teamRepository.findById(teamRequest.getTeamId());
        validateTeamExisting(team);
        validateMemberIsTeamMember(teamMemberRepository, teamRequest.getTeamId(), memberId);

        // 새로운 이미지 S3에 업로드
        String newImgUrl = storageService.uploadToS3(file, fileName);
        String oldImgUrl = teamRepository.getTeamImgUrl(teamRequest.getTeamId());

        team.updateTeam(teamRequest, storageService, oldImgUrl, newImgUrl);

        return new StorageImgResponseDTO(team);
    }

    public RegenerateCodeResponseDTO regenerateCode(TeamIdRequestDTO teamRequest) {
        // 코드 재발급 및 생성 시간 찍기
        String newInviteCode = getRandomInviteCode();
        LocalDateTime codeGeneratedTime = LocalDateTime.now();
        Team team = teamRepository.findById(teamRequest.getTeamId());

        // 모임 존재 여부 확인
        validateTeamExisting(team);

        // 초대 코드, 생성시간 update
        team.updateCode(newInviteCode, codeGeneratedTime);

        // 응답 생성
        LocalDateTime codeDeadline = codeGeneratedTime.plusDays(1);
        String codeDeadlineStr = codeDeadline.format(DateTimeFormatter.ofPattern("yyyy. MM. dd. HH:mm"));

        return new RegenerateCodeResponseDTO(teamRequest.getTeamId(), newInviteCode, codeDeadlineStr);
    }

    public List<GetTeamMemberResponseDTO> getTeamMembers(Long teamId, Long memberId) {
        //팀 구성원인지 확인
        validateMemberIsTeamMember(teamMemberRepository, teamId, memberId);

        return memberRepository.getMembersByTeamId(teamId);
    }

    public GetTeamResponseDTO getTeamDetail(Long teamId, Long memberId) {
        // 유저가 팀에 참가 중인지 검사
        validateMemberIsTeamMember(teamMemberRepository, teamId, memberId);

        GetTeamResponseDTO getTeamResponse = teamRepository.getTeamDetail(teamId, memberId);
        return getTeamResponse;
    }

    private void deleteImage(Long teamId) {
        String imgUrl = teamRepository.getTeamImgUrl(teamId);

        if (!imgUrl.equals("")) {
            String deleteFileName = storageService.getFileNameFromUrl(imgUrl);
            storageService.deleteImage(deleteFileName);
        }
    }

    private String getRandomInviteCode() {
        String inviteCode = generateInviteCode();
        while (true) {
            if (validateDuplicateInviteCode(teamRepository, inviteCode))
                break;
            inviteCode = generateInviteCode();
        }
        return inviteCode;
    }

    private String updateTeamImg(MultipartFile file) {
        // 새로운 이미지 S3에 업로드
        String fileName = storageService.getFileName(file, StorageDomain.TEAM);
        String imgUrl = storageService.uploadToS3(file, fileName);
        return imgUrl;
    }

    public String generateInviteCode() {
        Random random = new Random();

        String generatedString = random.ints(LEFT_LIMIT, RIGHT_LIMIT + 1)
                .filter(i -> (i <= ASCII_NUMBER_NINE || i >= ASCII_UPPERCASE_A) && (i <= ASCII_UPPERCASE_Z || i >= ASCII_LOWERCASE_A))
                .limit(TARGET_STRING_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    private String getInviteCodeFromRequest(JoinTeamRequestDTO teamRequest) {
        String inviteCode = teamRequest.getInviteCode();
        validateInviteCodeExisting(teamRepository, inviteCode);
        return inviteCode;
    }

    private List<GetTeamResponseDTO> generateTeamReturnResponse(List<Team> teams, Long memberId) {
        // 모임의 created_at 시간 비교해서 3일 안지났으면 isNew 값 true, 지났으면 false로 반환
        List<GetTeamResponseDTO> teamReturnResponses = new ArrayList<>();

        for (Team team : teams) {
            GetTeamResponseDTO teamResponse;
            if (!isNewTeam(team)) {
                teamResponse = generateTeamResponse(team, memberId, false);
            } else {
                teamResponse = generateTeamResponse(team, memberId, true);
            }
            teamReturnResponses.add(teamResponse);
        }
        return teamReturnResponses;
    }

    private GetTeamResponseDTO generateTeamResponse(Team team, Long memberId, boolean b) {
        return new GetTeamResponseDTO(team.getId(), team.getName(), team.getImgUrl(), teamMemberRepository.getCount(team.getId()),
                false, teamMemberRepository.isBookmark(memberId, team.getId()));
    }
}
