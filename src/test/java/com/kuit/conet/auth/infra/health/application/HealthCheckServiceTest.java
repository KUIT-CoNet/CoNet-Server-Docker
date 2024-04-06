package com.kuit.conet.auth.infra.health.application;

import com.kuit.conet.infra.health.application.HealthCheckService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HealthCheckServiceTest {
    @Autowired
    HealthCheckService healthCheckService;

    @Test
    void countAllResult3AfterInit(){
        //when
        Integer resultInt = healthCheckService.testConnection();
        //then
        Assertions.assertThat(resultInt).isEqualTo(3);
    }
}