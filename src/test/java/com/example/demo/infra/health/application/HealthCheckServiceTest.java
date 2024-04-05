package com.example.demo.infra.health.application;

import com.example.demo.infra.health.dao.HealthRepository;
import com.example.demo.infra.health.entity.User;
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