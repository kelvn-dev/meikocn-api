package com.meikocn.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.UUID;
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

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "project_id", columnDefinition = "uuid")
  private UUID projectId;

  @Column(name = "task_id", columnDefinition = "uuid")
  private UUID taskId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_id", insertable = false, updatable = false)
  @JsonBackReference
  private Task task;
}
