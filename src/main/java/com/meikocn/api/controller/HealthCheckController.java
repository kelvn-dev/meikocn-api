package com.meikocn.api.controller;

import com.meikocn.api.config.ServerConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health-check")
@RequiredArgsConstructor
public class HealthCheckController {

  private final ServerConfig serverConfig;

  @GetMapping("/livez")
  public ResponseEntity<?> getHeath() {
    return ResponseEntity.ok(serverConfig.getLiveMessage());
  }
}
