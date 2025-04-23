package com.meikocn.api.dto.provider.response;

import java.util.Map;
import lombok.Data;

@Data
public class PresignedObjectRequestDto {
  private String url;
  private long expiration;
  private Map<String, String> signedHeaders;
}
