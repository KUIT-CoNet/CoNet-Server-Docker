package com.kuit.conet.dto.web.response.plan;

import com.kuit.conet.dto.plan.OneMemberAvailableTimeDTO;
import lombok.Getter;

import java.util.List;

import static com.kuit.conet.domain.plan.AvailableTimeRegisteredStatus.*;

@Getter
public class OneMemberAvailableTimeResponseDTO {
    private Long planId;
    private Long memberId;
    private int availableTimeRegisteredStatus;
    private List<OneMemberAvailableTimeDTO> timeSlot;

    public static OneMemberAvailableTimeResponseDTO notRegistered(Long planId, Long memberId) {
        OneMemberAvailableTimeResponseDTO responseDTO = new OneMemberAvailableTimeResponseDTO();
        responseDTO.planId = planId;
        responseDTO.memberId = memberId;
        responseDTO.availableTimeRegisteredStatus = NOT_REGISTERED.getStatus();
        responseDTO.timeSlot = null;

        return responseDTO;
    }

    public static OneMemberAvailableTimeResponseDTO registered(Long planId, Long memberId, Boolean hasAvailableTime, List<OneMemberAvailableTimeDTO> timeSlot) {
        OneMemberAvailableTimeResponseDTO responseDTO = new OneMemberAvailableTimeResponseDTO();
        responseDTO.planId = planId;
        responseDTO.memberId = memberId;
        if (!hasAvailableTime) {
            responseDTO.availableTimeRegisteredStatus = NO_AVAILABLE_TIME.getStatus();
        }
        if (hasAvailableTime) {
            responseDTO.availableTimeRegisteredStatus = HAS_AVAILABLE_TIME.getStatus();
        }
        responseDTO.timeSlot = timeSlot;

        return responseDTO;
    }
}
