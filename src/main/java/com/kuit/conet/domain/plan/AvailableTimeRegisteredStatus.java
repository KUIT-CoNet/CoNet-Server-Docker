package com.kuit.conet.domain.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AvailableTimeRegisteredStatus {
    NOT_REGISTERED(0),
    NO_AVAILABLE_TIME(1),
    HAS_AVAILABLE_TIME(2);

    private int status;

}
