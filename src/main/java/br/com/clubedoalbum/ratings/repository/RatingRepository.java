package br.com.clubedoalbum.ratings.repository;

import br.com.clubedoalbum.ratings.domain.Rating;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RatingRepository extends JpaRepository<Rating, UUID> {

  Optional<Rating> findByAlbumIdAndUserId(String albumId, String userId);

  Page<Rating> findByAlbumIdOrderByUpdatedAtDesc(String albumId, Pageable pageable);

  Page<Rating> findByUserIdOrderByRatingValueDescUpdatedAtDesc(String userId, Pageable pageable);

  long countByUserId(String userId);

  long countByUserIdAndReviewIsNotNull(String userId);

  @Query("select coalesce(avg(r.ratingValue), 0) from Rating r where r.userId = :userId")
  Double averageRatingByUserId(String userId);
}
