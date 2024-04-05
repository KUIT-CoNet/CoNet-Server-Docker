package com.example.demo.infra.health.api;

import com.example.demo.infra.health.application.HealthCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@RequiredArgsConstructor
public class HealthCheckApi {
    private final HealthCheckService healthCheckService;

    @GetMapping("/api/test/connection")
    public ResponseEntity<Integer> testConnection() {
        return ResponseEntity.ok(healthCheckService.testConnection());
    }

}
