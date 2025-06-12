package com.meikocn.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "project")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class Project extends BaseModel {
  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "start_date")
  private Long startDate;

  @Column(name = "end_date")
  private Long endDate;

  @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, orphanRemoval = true)
  @JsonManagedReference
  private Set<Task> tasks;
}
