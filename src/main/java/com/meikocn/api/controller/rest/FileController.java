package com.meikocn.api.controller.rest;

import com.meikocn.api.controller.SecuredRestController;
import com.meikocn.api.dto.rest.response.FileVersionResDto;
import com.meikocn.api.mapping.provider.S3Mapper;
import com.meikocn.api.service.provider.S3Service;
import java.util.List;
import java.util.stream.Collectors;
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
  private final S3Mapper s3Mapper;

  @GetMapping
  @PreAuthorize("hasAuthority('read:files')")
  public ResponseEntity<?> listFileVersions(@RequestParam String key) {
    List<ObjectVersion> objectVersions = s3Service.listFileVersions(key);
    List<FileVersionResDto> fileVersionResDtos =
        objectVersions.stream()
            .map(
                version -> {
                  // TODO: move this to mapstruct + solution to get metadata of all version in 1
                  // search for modified-by
                  // Get metadata for each version
                  //                                HeadObjectRequest headRequest =
                  // HeadObjectRequest.builder()
                  //                      .bucket(awsPropConfig.getS3().getBucket())
                  //                      .key(version.key())
                  //                      .versionId(version.versionId())
                  //                      .build();

                  //              HeadObjectResponse headResponse =
                  // s3Client.headObject(headRequest);
                  //              Map<String, String> metadata = headResponse.metadata();
                  FileVersionResDto fileVersionResDto = new FileVersionResDto();
                  fileVersionResDto.setVersionId(version.versionId());
                  fileVersionResDto.setLastModified(version.lastModified());
                  fileVersionResDto.setIsLatest(version.isLatest());
                  return fileVersionResDto;
                })
            .collect(Collectors.toList());
    return ResponseEntity.ok(fileVersionResDtos);
  }
}
