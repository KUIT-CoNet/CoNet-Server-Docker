package com.kuit.conet.infra.health.application;

import com.kuit.conet.infra.health.dao.HealthRepository;
import com.kuit.conet.infra.health.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class HealthCheckServiceTest {
    @InjectMocks
    private HealthCheckService healthCheckService;
    @Mock
    private HealthRepository healthRepository;

    @BeforeEach
    public void set_up(){
        User user = new User();
        user.setName("강연주");
        healthCheckService.addUser(user);

        User user2 = new User();
        user.setName("정경은");
        healthCheckService.addUser(user2);

        User user3 = new User();
        user.setName("정소민");
        healthCheckService.addUser(user3);
    }

    @Test
    public void count_all(){
        long count = healthCheckService.countAll();
        assertEquals(3L,count);
    }
}