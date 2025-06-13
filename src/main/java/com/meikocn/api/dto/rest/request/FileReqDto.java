package com.meikocn.api.dto.rest.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Data;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

@Data
public class FileReqDto {
  @NotBlank private String key;
  @NotBlank private String url;
  private UUID taskId;
  private String contentType;
  private ObjectCannedACL acl;
  private String description;
}
