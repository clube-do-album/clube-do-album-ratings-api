package br.com.clubedoalbum.ratings.service;

import br.com.clubedoalbum.ratings.domain.Rating;
import br.com.clubedoalbum.ratings.dto.CreateOrUpdateRatingRequest;
import br.com.clubedoalbum.ratings.dto.PaginatedResponse;
import br.com.clubedoalbum.ratings.dto.RatingResponse;
import br.com.clubedoalbum.ratings.dto.UserRatingSummaryResponse;
import br.com.clubedoalbum.ratings.messaging.AlbumRatedPublisher;
import br.com.clubedoalbum.ratings.repository.RatingRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RatingService {

  private final RatingRepository ratingRepository;
  private final AlbumRatedPublisher albumRatedPublisher;

  public RatingService(RatingRepository ratingRepository, AlbumRatedPublisher albumRatedPublisher) {
    this.ratingRepository = ratingRepository;
    this.albumRatedPublisher = albumRatedPublisher;
  }

  @Transactional
  public RatingResponse createOrUpdate(CreateOrUpdateRatingRequest request, String userId) {
    var review = normalizeReview(request.review());
    var rating =
        ratingRepository
            .findByAlbumIdAndUserId(request.albumId(), userId)
            .map(
                existingRating -> {
                  existingRating.updateRating(request.rating(), review);
                  return existingRating;
                })
            .orElseGet(() -> new Rating(request.albumId(), userId, request.rating(), review));

    var savedRating = ratingRepository.save(rating);
    albumRatedPublisher.publish(savedRating);

    return RatingResponse.from(savedRating);
  }

  public PaginatedResponse<RatingResponse> listByAlbum(String albumId, Pageable pageable) {
    return PaginatedResponse.from(
        ratingRepository.findByAlbumIdOrderByUpdatedAtDesc(albumId, pageable)
            .map(RatingResponse::from));
  }

  public PaginatedResponse<RatingResponse> listByUser(String userId, Pageable pageable) {
    return PaginatedResponse.from(
        ratingRepository.findByUserIdOrderByRatingValueDescUpdatedAtDesc(userId, pageable)
            .map(RatingResponse::from));
  }

  public UserRatingSummaryResponse summarizeByUser(String userId) {
    long totalRatings = ratingRepository.countByUserId(userId);
    long reviewCount = ratingRepository.countByUserIdAndReviewIsNotNull(userId);
    double averageRating = ratingRepository.averageRatingByUserId(userId);

    return new UserRatingSummaryResponse(totalRatings, reviewCount, averageRating);
  }

  private String normalizeReview(String review) {
    if (review == null || review.isBlank()) {
      return null;
    }

    return review.trim();
  }
}
