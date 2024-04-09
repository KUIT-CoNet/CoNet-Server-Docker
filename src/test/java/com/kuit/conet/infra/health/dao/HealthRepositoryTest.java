package com.kuit.conet.infra.health.dao;

import com.kuit.conet.infra.health.dao.HealthRepository;
import com.kuit.conet.infra.health.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class HealthRepositoryTest {
    @Autowired
    HealthRepository healthRepository;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("강연주");
        healthRepository.save(user);

        User user2 = new User();
        user.setName("정경은");
        healthRepository.save(user2);

        User user3 = new User();
        user.setName("정소민");
        healthRepository.save(user3);
    }

    @Test
    void countAllResult3AfterInit(){
        Long resultInt = healthRepository.count();
        //then
        Assertions.assertThat(resultInt).isEqualTo(3L);
    }
}