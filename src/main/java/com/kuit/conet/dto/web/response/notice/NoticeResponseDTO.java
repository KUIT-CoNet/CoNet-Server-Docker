package com.kuit.conet.dto.web.response.notice;

import com.kuit.conet.domain.notice.Notice;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NoticeResponseDTO {
    private List<Notice> notices;

}
