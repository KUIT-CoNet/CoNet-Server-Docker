package com.kuit.conet.repository;

import com.kuit.conet.domain.notice.Notice;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoticeRepository {
    private final EntityManager em;

    public List<Notice> findAll() {
        TypedQuery<Notice> query = em.createQuery("SELECT n FROM Notice n", Notice.class);
        return query.getResultList();
    }
}
