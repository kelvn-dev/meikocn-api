package com.meikocn.api.controller.rest;

import com.meikocn.api.controller.SecuredRestController;
import com.meikocn.api.dto.rest.request.CommentReqDto;
import com.meikocn.api.service.rest.CommentService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/comments")
@RequiredArgsConstructor
public class CommentController implements SecuredRestController {

  private final CommentService commentService;

  @PostMapping
  @PreAuthorize("hasAuthority('write:tasks')")
  public ResponseEntity<?> create(@Valid @RequestBody CommentReqDto dto) {
    commentService.create(dto);
    return ResponseEntity.ok(null);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('write:tasks')")
  public ResponseEntity<?> updateById(
      @Valid @RequestBody CommentReqDto dto, @PathVariable UUID id) {
    commentService.updateById(id, dto);
    return ResponseEntity.ok(null);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('delete:tasks')")
  public ResponseEntity<?> deleteById(@PathVariable UUID id) {
    commentService.deleteById(id);
    return ResponseEntity.ok(null);
  }
}
