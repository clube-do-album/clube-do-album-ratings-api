package br.com.clubedoalbum.ratings.repository;

import br.com.clubedoalbum.ratings.domain.Rating;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, UUID> {

  Optional<Rating> findByAlbumIdAndUserId(String albumId, String userId);

  List<Rating> findByAlbumIdOrderByUpdatedAtDesc(String albumId);

  List<Rating> findByUserIdOrderByUpdatedAtDesc(String userId);
}
