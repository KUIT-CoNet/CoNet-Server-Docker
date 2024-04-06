package com.kuit.conet.infra.health.dao;

import com.kuit.conet.infra.health.entity.User;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
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
