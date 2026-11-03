package com.jlmasa.pms_backend.identity.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import static org.apache.tomcat.util.http.RequestUtil.normalize;

@Entity
@Table(name = "users")
public class UserAccount {

    @Id @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", nullable = false) private UUID id;
    @Column(name = "email", nullable = false, unique = true) private String email;
    @Column(name = "password_hash", nullable =true) String passwordHash;
    @Column(name = "display_name", nullable = false) String displayName;
    @Column(name = "avatar_url", nullable = true) String avatarUrl;
    @Column(name = "is_platform_admin", nullable = false) Boolean platformAdmin;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "disabled_at")
    private Instant disabledAt;

  protected UserAccount() {
  }

  public UserAccount(String email, String displayName, Instant now){
      this.email = normalize(email);
      this.displayName = Objects.requireNonNull(displayName, "displayName");
      this.platformAdmin = false;
      this.createdAt = now;
      this.updatedAt = now;
  }

  private static String normalize(String email){
      return Objects.requireNonNull(email, "email").trim().toLowerCase(Locale.ROOT);
  }

  public void changePasswordHash(String newHash, Instant  now){
      this.passwordHash = Objects.requireNonNull(newHash, "newHash");
      this.updatedAt = now;
  }

    public void disabled(Instant now){this.disabledAt = now; this.updatedAt = now;}
    public void enable(Instant now){this.disabledAt = null; this.updatedAt = now;}
    public boolean isEnabled() {return disabledAt == null;}

    public UUID getId(){return id;}
    public String getEmail() {return email;}
    public String getPasswordHash() {return passwordHash;}
    public String getDisplayName() {return displayName; }
    public String getAvatarUrl() {return avatarUrl;}
    public boolean isPlatformAdmin() {return platformAdmin;}
    public Instant getCreatedAt() {return createdAt;}
    public Instant getUpdatedAt() {return updatedAt;}
    public Instant getDisabledAt() {return disabledAt;}

    @Override
    public boolean equals(Object o){
      if(this == o) return true;
      if(!(o instanceof UserAccount other)) return false;
      return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {return getClass().hashCode();}
}
