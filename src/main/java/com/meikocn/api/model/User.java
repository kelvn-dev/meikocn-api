package com.meikocn.api.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class User {
  @Id
  @Column(name = "id", nullable = false, unique = true, updatable = false)
  private String id;

  @Column(name = "email", nullable = false, updatable = false)
  private String email;

  @Column(name = "nickname", nullable = false)
  private String nickname;

  @Column(name = "avatar")
  private String avatar;

  @Column(name = "language_code")
  private String languageCode;

  @Column(name = "invite_token", unique = true)
  private String inviteToken;

  @Column(name = "invite_token_verified")
  private boolean emailVerified;

  @Column(name = "created_at", updatable = false)
  private long createdAt;

  @Column(name = "updated_at")
  private long updatedAt;

  @CreatedBy
  @Column(name = "created_by", updatable = false)
  private String createdBy;

  @LastModifiedBy
  @Column(name = "updated_by")
  private String updatedBy;

  // @Column(name = "is_deleted")
  // private boolean isDeleted;

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (!(object instanceof User that)) return false;
    return this.id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now().getEpochSecond();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = Instant.now().getEpochSecond();
  }
}
