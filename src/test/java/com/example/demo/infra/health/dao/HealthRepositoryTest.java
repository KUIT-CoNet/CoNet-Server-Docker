package com.example.demo.infra.health.dao;

import com.example.demo.infra.health.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class HealthRepositoryTest {
    HealthRepository healthRepository = HealthRepository.getInstance();
    @Test
    void countAllResult3AfterInit(){
        //when
        healthRepository.addUser(new User(1L, "강연주"));
        healthRepository.addUser(new User(2L, "정소민"));
        healthRepository.addUser(new User(3L, "정경은"));

        Integer resultInt = healthRepository.countAll();
        //then
        Assertions.assertThat(resultInt).isEqualTo(3);
    }
}