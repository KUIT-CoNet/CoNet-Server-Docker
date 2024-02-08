package com.kuit.conet.jpa.repository;

import com.kuit.conet.dto.web.response.member.StorageImgResponseDTO;
import com.kuit.conet.dto.web.response.team.GetTeamMemberResponseDTO;
import com.kuit.conet.dto.web.response.team.GetTeamResponseDTO;
import com.kuit.conet.jpa.domain.member.Member;
import com.kuit.conet.jpa.domain.member.MemberStatus;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Getter
public class MemberRepository {
    private final EntityManager em;

    @Value("${spring.user.default-image}")
    private String defaultImg;

    public Member findById(Long id) {
        System.out.println("id " + id);
        return em.find(Member.class, id);
    }

    public StorageImgResponseDTO getImgUrlResponse(Long userId) {
        return em.createQuery("select new com.kuit.conet.dto.web.response.StorageImgResponseDTO(m.name, m.imgUrl) " +
                        "from Member m where m.id=:userId and m.status=:status", StorageImgResponseDTO.class)
                .setParameter("userId", userId)
                .setParameter("status", MemberStatus.ACTIVE)
                .getSingleResult();
    }

    public Boolean isDefaultImage(Long userId) {
        return em.createQuery("select case when " +
                        "(select m.imgUrl from Member m where m.id=:userId and m.status=:status) " +
                        "= :defaultImg " +
                        "then TRUE else FALSE end from Member m", Boolean.class)
                .setParameter("userId", userId)
                .setParameter("status", MemberStatus.ACTIVE)
                .setParameter("defaultImg", defaultImg)
                .getSingleResult();
    }

    public List<GetTeamMemberResponseDTO> getMembersByTeamId(Long teamId) {
        return em.createQuery("select new com.kuit.conet.dto.web.response.team.GetTeamMemberResponseDTO(m.id,m.name,m.imgUrl) " +
                        "from TeamMember tm join tm.member m where tm.team.id=:teamId")
                .setParameter("teamId", teamId)
                .getResultList();
    }

    public List<GetTeamResponseDTO> getBookmarks(Long userId) {
        return em.createQuery("select new com.kuit.conet.dto.web.response.team.GetTeamResponseDTO(t, (select count(tm) from TeamMember tm where tm.team.id=t.id), tm.bookMark) " +
                        "from TeamMember tm join tm.team t where tm.member.id=:userId", GetTeamResponseDTO.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public void deleteUser(Long userId) {
        em.createQuery("update Member m set m.platform='', m.platformId='', m.imgUrl='', m.optionTerm=False, m.serviceTerm=False, m.status='INACTIVE' where m.id=:userId and m.status='ACTIVE'")
                .setParameter("userId", userId)
                .executeUpdate();
        em.flush();
    }
}