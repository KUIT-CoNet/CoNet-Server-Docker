package com.kuit.conet.domain.team;

import com.kuit.conet.dto.web.request.team.UpdateTeamRequestDTO;
import com.kuit.conet.domain.member.Member;
import com.kuit.conet.domain.plan.Plan;
import com.kuit.conet.service.StorageService;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Column(name = "team_name", length = 100)
    private String name;

    @Column(name = "team_image_url", length = 500)
    private String imgUrl;

    @Column(length = 20)
    private String inviteCode;

    private LocalDateTime codeGeneratedTime;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    // 다대다(다대일, 일대다) 양방향 연관 관계 / 연관 관계 주인의 반대편
    private List<Plan> plans = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    // 다대다(다대일, 일대다) 단방향 연관 관계 / 연관 관계 주인의 반대편
    private List<TeamMember> teamMembers = new ArrayList<>();

    //==생성 메서드==//
    public static Team createTeam(String teamName, String inviteCode, LocalDateTime codeGeneratedTime, Member teamCreator, String imgUrl) {
        Team team = new Team();
        team.name = teamName;
        team.inviteCode = inviteCode;
        team.codeGeneratedTime = codeGeneratedTime;
        team.imgUrl = imgUrl;

        TeamMember teamMember = TeamMember.createTeamMember(team, teamCreator);

        return team;
    }

    public void updateTeam(UpdateTeamRequestDTO teamRequest, StorageService storageService, String oldImgUrl, String newImgUrl) {
        if (oldImgUrl != null) {
            String deleteFileName = storageService.getFileNameFromUrl(oldImgUrl);
            storageService.deleteImage(deleteFileName);
        }
        this.name = teamRequest.getTeamName();
        this.imgUrl = newImgUrl;
    }

    public void updateCode(String newInviteCode, LocalDateTime codeGeneratedTime) {
        this.inviteCode = newInviteCode;
        this.codeGeneratedTime = codeGeneratedTime;
    }

    public void addPlan(Plan plan) {
        plans.add(plan);
    }

    //== 연관관계 편의 메서드 ==//
    public void addTeamMember(TeamMember teamMember) {
        teamMembers.add(teamMember);
    }

    public void deleteMember(TeamMember teamMember) {
        teamMembers.remove(teamMember);
    }
}
