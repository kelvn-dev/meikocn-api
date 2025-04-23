package com.meikocn.api.dto.rest.response;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class PageResDto<M> implements Serializable {
  private int pageIndex;
  private int totalPages;
  private long totalItems;
  private boolean last;
  private boolean first;
  private List<M> items;
}
