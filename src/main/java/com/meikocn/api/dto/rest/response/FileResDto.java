package com.meikocn.api.dto.rest.response;

import java.util.UUID;
import lombok.Data;

@Data
public class FileResDto {
  private UUID id;
  private String key;
  private String url;
  private String contentType;
  private String acl;
  private String description;
}
