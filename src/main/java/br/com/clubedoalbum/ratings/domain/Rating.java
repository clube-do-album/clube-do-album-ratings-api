package br.com.clubedoalbum.ratings.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "ratings",
    uniqueConstraints = @UniqueConstraint(name = "uk_ratings_album_user", columnNames = {"album_id", "user_id"}))
public class Rating {

  @Id
  private UUID id;

  @Column(name = "album_id", nullable = false)
  private String albumId;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "rating_value", nullable = false)
  private Double ratingValue;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  protected Rating() {}

  public Rating(String albumId, String userId, Double ratingValue) {
    this.id = UUID.randomUUID();
    this.albumId = albumId;
    this.userId = userId;
    this.ratingValue = ratingValue;
  }

  @PrePersist
  void onCreate() {
    var now = LocalDateTime.now();
    this.createdAt = now;
    this.updatedAt = now;
  }

  @PreUpdate
  void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  public UUID getId() {
    return id;
  }

  public String getAlbumId() {
    return albumId;
  }

  public String getUserId() {
    return userId;
  }

  public Double getRatingValue() {
    return ratingValue;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void updateRating(Double ratingValue) {
    this.ratingValue = ratingValue;
  }
}
