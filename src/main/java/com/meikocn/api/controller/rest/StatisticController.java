package com.meikocn.api.controller.rest;

import com.meikocn.api.controller.SecuredRestController;
import com.meikocn.api.enums.TaskStatus;
import com.meikocn.api.service.rest.StatisticService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/statistics")
@RequiredArgsConstructor
public class StatisticController implements SecuredRestController {

  private final StatisticService statisticService;

  @GetMapping("/task-count")
  public ResponseEntity<?> getTaskCount(
      @RequestParam(name = "project-id") UUID projectId,
      @RequestParam long start,
      @RequestParam long end,
      @RequestParam(name = "task-status") TaskStatus taskStatus) {
    return ResponseEntity.ok(statisticService.getTaskCount(projectId, start, end, taskStatus));
  }

  @GetMapping("/task-count-for-all-users")
  public ResponseEntity<?> getTaskCountForAllUser(
      @RequestParam(name = "project-id") UUID projectId) {
    return ResponseEntity.ok(statisticService.getTaskCountForAllUser(projectId));
  }

  @GetMapping("/task-count-for-each-user")
  public ResponseEntity<?> getTaskCountForEachUser(
      @RequestParam(name = "project-id") UUID projectId,
      @RequestParam(name = "user-id") String userId) {
    return ResponseEntity.ok(statisticService.getTaskCountForEachUser(projectId, userId));
  }
}
