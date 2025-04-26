package com.meikocn.api.model;

import jakarta.persistence.*;
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

  @Lob
  @Column(name = "description")
  private String description;
}
