package com.meikocn.api.controller.rest;

import com.meikocn.api.controller.SecuredRestController;
import com.meikocn.api.dto.rest.request.FileReqDto;
import com.meikocn.api.mapping.rest.FileMapper;
import com.meikocn.api.model.File;
import com.meikocn.api.service.rest.FileService;
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
@RequestMapping("/v1/files")
@RequiredArgsConstructor
public class FileController implements SecuredRestController {

  private final FileService fileService;
  private final FileMapper fileMapper;

  @PostMapping
  @PreAuthorize("hasAuthority('write:files')")
  public ResponseEntity<?> create(@Valid @RequestBody FileReqDto dto) {
    fileService.create(dto);
    return ResponseEntity.ok(null);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable UUID id) {
    File file = fileService.getById(id, false);
    return ResponseEntity.ok(fileMapper.model2Dto(file));
  }

  @GetMapping("/versions")
  @PreAuthorize("hasAuthority('read:files')")
  public ResponseEntity<?> getFileVersions(@RequestParam String key) {
    return ResponseEntity.ok(fileService.getFileVersions(key));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('write:files')")
  public ResponseEntity<?> updateById(@Valid @RequestBody FileReqDto dto, @PathVariable UUID id) {
    fileService.updateById(id, dto);
    return ResponseEntity.ok(null);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('delete:files')")
  public ResponseEntity<?> deleteById(@PathVariable UUID id) {
    fileService.deleteById(id);
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
    return ResponseEntity.ok(fileMapper.model2Dto(fileService.getList(filter, pageable)));
  }
}
