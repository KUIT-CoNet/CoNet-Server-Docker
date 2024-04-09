package com.kuit.conet.infra.health.application;

import com.kuit.conet.infra.health.dao.HealthRepository;
import com.kuit.conet.infra.health.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HealthCheckService {
    private final HealthRepository healthRepository;

    public void addUser(User user){
        User savedUser = healthRepository.save(user);
    }

    public Long countAll(){
        return healthRepository.count();
    }
}
