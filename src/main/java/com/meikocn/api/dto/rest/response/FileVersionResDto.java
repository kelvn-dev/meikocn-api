package com.meikocn.api.dto.rest.response;

import java.time.Instant;
import lombok.Data;

@Data
public class FileVersionResDto {
  private String key;
  private String versionId;
  private Instant lastModified;
  private Boolean isLatest;
  private String modifiedBy;
}
