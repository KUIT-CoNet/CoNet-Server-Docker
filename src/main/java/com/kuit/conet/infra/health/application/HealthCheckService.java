package com.kuit.conet.infra.health.application;

import com.kuit.conet.infra.health.dao.HealthRepository;
import com.kuit.conet.infra.health.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HealthCheckService {
    private final HealthRepository healthRepository;

    public Integer testConnection() {
        healthRepository.addUser(new User(1L, "강연주"));
        healthRepository.addUser(new User(2L, "정소민"));
        healthRepository.addUser(new User(3L, "정경은"));

        return healthRepository.countAll();
    }
}
