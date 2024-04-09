package com.kuit.conet.domain.plan;

import com.kuit.conet.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Getter
@NoArgsConstructor
public class PlanMemberTime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_member_time_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id") // 다대다(다대일, 일대다) 양방향 연관 관계 / 연관 관계의 주인
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // 다대다(다대일, 일대다) 양방향 연관 관계 / 연관 관계의 주인
    private Member member;

    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(length = 500)
    private String availableTime;

    public static PlanMemberTime createPlanMemberTime(Plan plan, Member member, Date date, String availableTime) {
        PlanMemberTime planMemberTime = new PlanMemberTime();
        planMemberTime.plan = plan;
        planMemberTime.member = member;
        planMemberTime.date = date;
        planMemberTime.availableTime = availableTime;

        plan.addPlanMemberTime(planMemberTime);

        return planMemberTime;
    }

}
