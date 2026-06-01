package br.com.clubedoalbum.ratings.dto;

import br.com.clubedoalbum.ratings.domain.Rating;
import java.time.LocalDateTime;
import java.util.UUID;

public record RatingResponse(
    UUID id,
    String albumId,
    String userId,
    Double rating,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public static RatingResponse from(Rating rating) {
    return new RatingResponse(
        rating.getId(),
        rating.getAlbumId(),
        rating.getUserId(),
        rating.getRatingValue(),
        rating.getCreatedAt(),
        rating.getUpdatedAt());
  }
}
