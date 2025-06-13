package com.meikocn.api.controller.rest;

import com.meikocn.api.controller.SecuredRestController;
import com.meikocn.api.dto.rest.request.TaskReqDto;
import com.meikocn.api.mapping.rest.TaskMapper;
import com.meikocn.api.model.Task;
import com.meikocn.api.model.TaskEntityGraph;
import com.meikocn.api.service.rest.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/tasks")
@RequiredArgsConstructor
public class TaskController implements SecuredRestController {

  private final TaskService taskService;
  private final TaskMapper taskMapper;

  @PostMapping
  @PreAuthorize("hasAuthority('write:tasks')")
  public ResponseEntity<?> create(@Valid @RequestBody TaskReqDto dto) {
    taskService.create(dto);
    return ResponseEntity.ok(null);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable UUID id) {
    TaskEntityGraph entityGraph =
        TaskEntityGraph.____().assignee().____.project().____.comments().____.files().____.____();
    Task task = taskService.getById(id, entityGraph, false);
    return ResponseEntity.ok(taskMapper.model2Dto(task));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('write:tasks')")
  public ResponseEntity<?> updateById(@Valid @RequestBody TaskReqDto dto, @PathVariable UUID id) {
    taskService.updateById(id, dto);
    return ResponseEntity.ok(null);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('delete:tasks')")
  public ResponseEntity<?> deleteById(@PathVariable UUID id) {
    taskService.deleteById(id);
    return ResponseEntity.ok(null);
  }

  @GetMapping
  public ResponseEntity<?> getList(
      @PageableDefault(
              sort = {"createdAt"},
              direction = Sort.Direction.DESC)
          @ParameterObject
          Pageable pageable,
      @RequestParam(required = false, defaultValue = "") List<String> filter) {
    TaskEntityGraph entityGraph =
        TaskEntityGraph.____().assignee().____.project().____.comments().____.____();
    return ResponseEntity.ok(
        taskMapper.model2Dto(taskService.getList(filter, pageable, entityGraph)));
  }
}
