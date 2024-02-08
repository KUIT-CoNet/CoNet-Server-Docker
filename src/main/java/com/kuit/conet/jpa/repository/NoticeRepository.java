package com.kuit.conet.jpa.repository;

import com.kuit.conet.jpa.domain.notice.Notice;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NoticeRepository {
    private final EntityManager em;

    public Notice findById(Long id) {
        return em.find(Notice.class, id);
    }
}
