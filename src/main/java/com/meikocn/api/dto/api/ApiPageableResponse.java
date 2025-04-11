package com.meikocn.api.dto.api;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiPageableResponse {
  private int currentPage;
  private int pageSize;
  private int totalPages;
  private long totalElements;
  private boolean isLast;
  private boolean isFirst;
  private List<?> data;
}
