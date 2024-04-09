package com.kuit.conet.service.validator;

import com.kuit.conet.common.exception.FileException;
import com.kuit.conet.common.exception.TeamException;
import com.kuit.conet.domain.team.Team;
import com.kuit.conet.repository.TeamMemberRepository;
import com.kuit.conet.repository.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
public class TeamValidator {

    public static boolean validateDuplicateInviteCode(TeamRepository teamRepository, String inviteCode) {
        //inviteCode 써도되면 true, 중복되면 false 반환
        return !teamRepository.isExistInviteCode(inviteCode);
    }

    public static void validateInviteCodeExisting(TeamRepository teamRepository, String inviteCode) {
        if (!teamRepository.isExistInviteCode(inviteCode)) {
            log.error(NOT_FOUND_INVITE_CODE.getMessage());
            throw new TeamException(NOT_FOUND_INVITE_CODE);
        }
    }

    public static void validateNewMemberInTeam(TeamRepository teamRepository, Long memberId, Team team) {
        if (teamRepository.isExistMember(team.getId(), memberId)) {
            log.error(EXIST_MEMBER_IN_TEAM.getMessage());
            throw new TeamException(EXIST_MEMBER_IN_TEAM);
        }
    }

    public static void compareInviteCodeAndRequestTime(Team team) {
        LocalDateTime joinRequestTime = LocalDateTime.now();
        LocalDateTime generatedTime = team.getCodeGeneratedTime();
        LocalDateTime expirationDateTime = generatedTime.plusDays(1);

        //log.info("Team invite code generated time: {}", generatedTime);
        log.info("Team invite code expiration date time: {}", expirationDateTime);
        log.info("Team participation requested time: {}", joinRequestTime);

        if (joinRequestTime.isAfter(expirationDateTime)) {
            // 초대 코드 생성 시간으로부터 1일이 지났으면 exception
            log.error("유효 기간 만료: {}", EXPIRED_INVITE_CODE.getMessage());
            throw new TeamException(EXPIRED_INVITE_CODE);
        }
    }

    public static boolean isNewTeam(Team team) {
        log.info("{}", team.getName());
        LocalDateTime createdAt = team.getCreatedAt();
        LocalDateTime now = LocalDateTime.now();

        return !(now.minusDays(3).isAfter(createdAt));
    }

    public static void validateTeamExisting(Team team) {
        if (team == null) {
            log.error(NOT_FOUND_TEAM.getMessage());
            throw new TeamException(NOT_FOUND_TEAM);
        }
    }

    public static void validateMemberIsTeamMember(TeamMemberRepository teamMemberRepository, Long teamId, Long memberId) {
        if (!teamMemberRepository.isTeamMember(teamId, memberId)) {
            log.error(NOT_TEAM_MEMBER.getMessage());
            throw new TeamException(NOT_TEAM_MEMBER);
        }
    }

    public static void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            log.error(NOT_FOUND_FILE.getMessage());
            throw new FileException(NOT_FOUND_FILE);
        }
    }
}
