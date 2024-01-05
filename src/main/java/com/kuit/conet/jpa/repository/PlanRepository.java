package com.kuit.conet.jpa.repository;

import com.kuit.conet.dto.plan.SideMenuFixedPlan;
import com.kuit.conet.dto.plan.WaitingPlan;
import com.kuit.conet.dto.plan.FixedPlanOnDay;
import com.kuit.conet.jpa.domain.plan.Plan;
import com.kuit.conet.jpa.domain.plan.PlanStatus;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Getter
public class PlanRepository {
    private final EntityManager em;

    public Long save(Plan plan) {
        em.persist(plan);
        return plan.getId();
    }

    public Plan findWithMembersById(Long planId) {
        return em.createQuery("select p from Plan p left join fetch p.planMembers pm left join fetch pm.member " +
                        "where p.id=:planId", Plan.class)
                .setParameter("planId", planId)
                .getSingleResult();
    }

    public List<FixedPlanOnDay> getFixedPlansOnDay(Long teamId, String searchDate) {
        return em.createQuery("select new com.kuit.conet.dto.plan.FixedPlanOnDay(p.id, p.name, p.fixedTime) " +
                "from Plan p join p.team t on t.id=:teamId " +
                "where p.status=:status " +
                "and FUNCTION('DATE_FORMAT', p.fixedDate, '%Y-%m-%d')=:searchDate " +
                "order by p.fixedTime", FixedPlanOnDay.class)
                .setParameter("teamId", teamId)
                .setParameter("status", PlanStatus.FIXED)
                .setParameter("searchDate", searchDate)
                .getResultList();
    }

    public List<Date> getFixedPlansInMonth(Long teamId, String searchMonth) {
        return em.createQuery("select distinct p.fixedDate " +
                        "from Plan p join p.team t on t.id=:teamId " +
                        "where p.status=:status " +
                        "and FUNCTION('DATE_FORMAT', p.fixedDate, '%Y-%m')=:searchMonth " +
                        "order by p.fixedDate", Date.class)
                .setParameter("teamId", teamId)
                .setParameter("status", PlanStatus.FIXED)
                .setParameter("searchMonth", searchMonth)
                .getResultList();
    }

    public List<WaitingPlan> getTeamWaitingPlan(Long teamId) {
        return em.createQuery("select new com.kuit.conet.dto.plan.WaitingPlan(p.id, p.startPeriod, p.endPeriod, p.team.name, p.name) " +
                        "from Plan p join p.team t on t.id=:teamId " +
                        "where p.status=:status " +
                        "and p.startPeriod>=current_date() " +
                        "order by p.startPeriod", WaitingPlan.class)
                .setParameter("teamId", teamId)
                .setParameter("status", PlanStatus.WAITING)
                .getResultList();
    }

    public List<SideMenuFixedPlan> getFixedPastPlans(Long teamId, Long userId) {
        return em.createQuery("select new com.kuit.conet.dto.plan.SideMenuFixedPlan(p.id, p.name, p.fixedDate, p.fixedTime, " +
                        "                                                                       function('DATEDIFF', CURRENT_DATE, p.fixedDate), " + // 약속이 지난 지 며칠 ?
                        "                                                                       (select count(pm)>0 " +
                        "                                                                        from PlanMember pm " +
                        "                                                                        where pm.plan.id=p.id and pm.member.id=:userId)) " +
                        "from Plan p " +
                        "where p.team.id=:teamId " +
                        "and p.status=:status " +
                        "and p.fixedDate < CURRENT_DATE or (p.fixedDate = CURRENT_DATE and p.fixedTime < CURRENT_TIME) " +
                        "order by p.fixedDate desc, p.fixedTime desc ", SideMenuFixedPlan.class)
                .setParameter("teamId", teamId)
                .setParameter("status", PlanStatus.FIXED)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<SideMenuFixedPlan> getFixedFuturePlans(Long teamId, Long userId) {
        return em.createQuery("select new com.kuit.conet.dto.plan.SideMenuFixedPlan(p.id, p.name, p.fixedDate, p.fixedTime, " +
                        "                                                                       function('DATEDIFF', p.fixedDate, CURRENT_DATE), " + // 며칠 남은 약속 ?
                        "                                                                       (select count(pm)>0 " +
                        "                                                                        from PlanMember pm " +
                        "                                                                        where pm.plan.id=p.id and pm.member.id=:userId)) " +
                        "from Plan p " +
                        "where p.team.id=:teamId " +
                        "and p.status=:status " +
                        "and p.fixedDate > CURRENT_DATE or (p.fixedDate = CURRENT_DATE and p.fixedTime >= CURRENT_TIME) " +
                        "order by p.fixedDate, p.fixedTime ", SideMenuFixedPlan.class)
                .setParameter("teamId", teamId)
                .setParameter("status", PlanStatus.FIXED)
                .setParameter("userId", userId)
                .getResultList();
    }
}
