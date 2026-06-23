package br.com.clubedoalbum.ratings.controller;

import br.com.clubedoalbum.ratings.dto.CreateOrUpdateRatingRequest;
import br.com.clubedoalbum.ratings.dto.PaginatedResponse;
import br.com.clubedoalbum.ratings.dto.RatingResponse;
import br.com.clubedoalbum.ratings.dto.UserRatingSummaryResponse;
import br.com.clubedoalbum.ratings.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/ratings")
public class RatingController {

  private final RatingService ratingService;

  public RatingController(RatingService ratingService) {
    this.ratingService = ratingService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public RatingResponse createOrUpdate(
      @Valid @RequestBody CreateOrUpdateRatingRequest request,
      @RequestHeader(value = "X-User-Id", required = false) String authenticatedUserId
  ) {
    return ratingService.createOrUpdate(request, requireAuthenticatedUser(authenticatedUserId));
  }

  @GetMapping("/albums/{albumId}")
  public PaginatedResponse<RatingResponse> listByAlbum(
      @PathVariable String albumId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int limit
  ) {
    return ratingService.listByAlbum(albumId, pageable(page, limit));
  }

  @GetMapping("/users/{userId}")
  public PaginatedResponse<RatingResponse> listByUser(
      @PathVariable String userId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "15") int limit,
      @RequestHeader(value = "X-User-Id", required = false) String authenticatedUserId
  ) {
    String currentUserId = requireAuthenticatedUser(authenticatedUserId);
    if (!currentUserId.equals(userId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden.");
    }

    return ratingService.listByUser(userId, pageable(page, limit));
  }

  @GetMapping("/users/{userId}/summary")
  public UserRatingSummaryResponse summarizeByUser(
      @PathVariable String userId,
      @RequestHeader(value = "X-User-Id", required = false) String authenticatedUserId
  ) {
    String currentUserId = requireAuthenticatedUser(authenticatedUserId);
    if (!currentUserId.equals(userId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden.");
    }

    return ratingService.summarizeByUser(userId);
  }

  @GetMapping("/users/{userId}/public")
  public PaginatedResponse<RatingResponse> listPublicByUser(
      @PathVariable String userId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "15") int limit
  ) {
    return ratingService.listByUser(userId, pageable(page, limit));
  }

  private String requireAuthenticatedUser(String authenticatedUserId) {
    if (authenticatedUserId == null || authenticatedUserId.isBlank()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "authentication required.");
    }

    return authenticatedUserId;
  }

  private PageRequest pageable(int page, int limit) {
    int safePage = Math.max(1, page);
    int safeLimit = Math.min(Math.max(1, limit), 100);

    return PageRequest.of(safePage - 1, safeLimit);
  }
}
