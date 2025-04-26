package com.meikocn.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

@Entity
@Table(name = "file")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class File extends BaseModel {
  @Column(name = "key", nullable = false)
  private String key;

  @Column(name = "url", nullable = false)
  private String url;

  @Column(name = "content_type")
  private String contentType;

  @Column(name = "acl")
  @Enumerated(EnumType.STRING)
  private ObjectCannedACL acl;

  @Lob
  @Column(name = "description")
  private String description;
}
