package com.kuit.conet.repository;

import com.kuit.conet.domain.plan.Plan;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlanMemberRepository {
    private final EntityManager em;

    public int deleteOnPlan(Plan plan) {
        return em.createQuery("delete from PlanMember pm where pm.plan=:plan")
                .setParameter("plan", plan)
                .executeUpdate();
    }

    public void deleteByTeamId(Long teamId) {
        em.createQuery("delete from PlanMember pm where pm.plan.id in " +
                        "(select p.id from Plan p where p.team.id=:teamId)")
                .setParameter("teamId", teamId)
                .executeUpdate();
    }

    public int deleteOnPlanByMemberId(Long memberId) {
        int deletedPlanMemberCount = em.createQuery("delete from PlanMember pm where pm.member.id=:memberId")
                .setParameter("memberId", memberId)
                .executeUpdate();
        em.flush();
        return deletedPlanMemberCount;
    }
}
