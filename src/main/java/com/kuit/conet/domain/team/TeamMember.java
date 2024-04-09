package com.kuit.conet.domain.team;

import com.kuit.conet.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id") // 다대다(다대일, 일대다) 양방향 연관 관계 / 연관 관계의 주인
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // 다대다(다대일, 일대다) 단방향 연관 관계 / 연관 관계의 주인
    private Member member;

    @Column(columnDefinition = "boolean default false")
    private boolean bookMark;

    //==생성 메서드==//
    public static TeamMember createTeamMember(Team team, Member member) {
        TeamMember teamMember = new TeamMember();
        teamMember.member = member;
        teamMember.team = team;

        team.addTeamMember(teamMember);

        return teamMember;
    }

}
