package com.meikocn.api.controller.rest;

import com.meikocn.api.controller.SecuredRestController;
import com.meikocn.api.service.provider.S3Service;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.ObjectVersion;

@RestController
@RequestMapping("/v1/files")
@RequiredArgsConstructor
public class FileController implements SecuredRestController {

  private final S3Service s3Service;

  @GetMapping
  @PreAuthorize("hasAuthority('read:files')")
  public ResponseEntity<?> listFileVersions(@RequestParam String key) {
    List<ObjectVersion> objectVersions = s3Service.listFileVersions(key);
    return ResponseEntity.ok(objectVersions);
  }
}
