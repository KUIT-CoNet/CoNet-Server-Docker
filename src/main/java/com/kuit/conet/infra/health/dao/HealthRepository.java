package com.kuit.conet.infra.health.dao;

import com.kuit.conet.infra.health.entity.User;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public interface HealthRepository extends JpaRepository<User,Long> {
}
