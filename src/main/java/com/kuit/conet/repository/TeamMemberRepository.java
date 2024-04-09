package com.kuit.conet.repository;

import com.kuit.conet.domain.member.Member;
import com.kuit.conet.domain.team.Team;
import com.kuit.conet.domain.team.TeamMember;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Getter
public class TeamMemberRepository {
    private final EntityManager em;

    public Long save(TeamMember teamMember) {
        em.persist(teamMember);
        return teamMember.getId();
    }

    public Boolean isBookmark(Long memberId, Long teamId) {
        return em.createQuery("select tm.bookMark from TeamMember tm where tm.team.id=:teamId and tm.member.id=:memberId", boolean.class)
                .setParameter("memberId", memberId)
                .setParameter("teamId", teamId)
                .getSingleResult();
    }

    public TeamMember findByTeamIdAndMemberId(Long teamId, Long memberId) {
        return em.createQuery("select tm from TeamMember tm where tm.team.id=:teamId and tm.member.id=:memberId", TeamMember.class)
                .setParameter("teamId", teamId)
                .setParameter("memberId", memberId)
                .getSingleResult();
    }


    public Long getCount(Long teamId) {
        return em.createQuery("select count(tm) from TeamMember tm where tm.team.id=:teamId", Long.class)
                .setParameter("teamId", teamId)
                .getSingleResult();
    }

    public boolean isTeamMember(Long teamId, Long memberId) {
        return em.createQuery("select count(tm)>0 from TeamMember tm where tm.team.id=:teamId and tm.member.id=:memberId", Boolean.class)
                .setParameter("memberId", memberId)
                .setParameter("teamId", teamId)
                .getSingleResult();
    }

    public void deleteTeamMemberByTeamId(Long teamId) {
        em.createQuery("delete from TeamMember tm where tm.team.id=:teamId")
                .setParameter("teamId", teamId)
                .executeUpdate();
    }

    public void bookmarkTeam(Long memberId, Long teamId) {
        em.createQuery("update TeamMember tm set tm.bookMark = CASE WHEN tm.bookMark = true THEN false ELSE true END " +
                        "where tm.member.id=:memberId and tm.team.id=:teamId")
                .setParameter("memberId", memberId)
                .setParameter("teamId", teamId)
                .executeUpdate();
    }

    public int deleteTeamMemberByMemberId(Long memberId) {
        int deletedTeamMemberCount = em.createQuery("delete from TeamMember tm where tm.member.id=:memberId")
                .setParameter("memberId", memberId)
                .executeUpdate();
        em.flush();
        return deletedTeamMemberCount;
    }

    public List<Member> findTeamMembersByTeamId(Team team) {
        return em.createQuery("select m from TeamMember tm left join tm.member m " +
                "where tm.team=:team", Member.class)
                .setParameter("team", team)
                .getResultList();
    }
}
