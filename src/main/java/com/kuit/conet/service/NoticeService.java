package com.kuit.conet.service;

import com.kuit.conet.domain.notice.Notice;
import com.kuit.conet.dto.web.response.notice.NoticeResponseDTO;
import com.kuit.conet.repository.MemberRepository;
import com.kuit.conet.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    public NoticeResponseDTO getNotice() {
        log.info("test 1");
        List<Notice> notices = noticeRepository.findAll();
        log.info("test 2: " + notices.size());
        return new NoticeResponseDTO(notices);
    }
}
