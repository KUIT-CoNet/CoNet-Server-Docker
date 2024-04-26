package com.kuit.conet.infra.health;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class EnvTest {
    @Test
    public void testActiveProfileEnvironmentVariable() {
        // 환경 변수에서 ACTIVE_PROFILE 값을 가져옴
        String activeProfile = System.getenv("ACTIVE_PROFILE");
        // 환경 변수 값을 출력
        System.out.println("ACTIVE_PROFILE 환경 변수 값: " + activeProfile);
    }
}
