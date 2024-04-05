package com.example.demo.infra.health.dao;

import com.example.demo.infra.health.entity.User;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class HealthRepository{
    private Map<Long,User> users = new HashMap<>();
    private static HealthRepository healthRepository;

    public static HealthRepository getInstance() {
        if (healthRepository == null) {
            healthRepository = new HealthRepository();
            return healthRepository;
        }
        return healthRepository;
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    public Integer countAll(){
        return users.size();
    }
}
