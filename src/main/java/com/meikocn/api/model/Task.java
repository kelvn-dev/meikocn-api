package com.meikocn.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.meikocn.api.enums.TaskPriority;
import com.meikocn.api.enums.TaskStatus;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "task")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class Task extends BaseModel {
  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "start_date")
  private Long startDate;

  @Column(name = "end_date")
  private Long endDate;

  @Column(name = "priority")
  @Enumerated(EnumType.STRING)
  private TaskPriority priority;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private TaskStatus status;

  @Column(name = "assignee_id")
  private String assigneeId;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assignee_id", insertable = false, updatable = false)
  private User assignee;

  @Column(name = "project_id", columnDefinition = "uuid")
  private UUID projectId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", insertable = false, updatable = false)
  @JsonBackReference
  private Project project;
}
