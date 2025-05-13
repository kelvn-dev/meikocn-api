package com.meikocn.api.controller.rest;

import com.meikocn.api.controller.SecuredRestController;
import com.meikocn.api.dto.rest.request.ProjectReqDto;
import com.meikocn.api.mapping.rest.ProjectMapper;
import com.meikocn.api.model.Project;
import com.meikocn.api.model.ProjectEntityGraph;
import com.meikocn.api.service.rest.ProjectService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/projects")
@RequiredArgsConstructor
public class ProjectController implements SecuredRestController {

  private final ProjectService projectService;
  private final ProjectMapper projectMapper;

  @PostMapping
  @PreAuthorize("hasAuthority('write:projects')")
  public ResponseEntity<?> create(@Valid @RequestBody ProjectReqDto dto) {
    projectService.create(dto);
    return ResponseEntity.ok(null);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable UUID id) {
    ProjectEntityGraph entityGraph = ProjectEntityGraph.____().tasks().____.____();
    Project project = projectService.getById(id, entityGraph, false);
    return ResponseEntity.ok(projectService.mapTaskData(project));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('write:projects')")
  public ResponseEntity<?> updateById(
      @Valid @RequestBody ProjectReqDto dto, @PathVariable UUID id) {
    projectService.updateById(id, dto);
    return ResponseEntity.ok(null);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('delete:projects')")
  public ResponseEntity<?> deleteById(@PathVariable UUID id) {
    projectService.deleteById(id);
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
    ProjectEntityGraph entityGraph = ProjectEntityGraph.____().tasks().____.____();
    Page<Project> projectPage = projectService.getList(filter, pageable, entityGraph);
    return ResponseEntity.ok(projectService.mapTaskData(projectPage));
  }
}
