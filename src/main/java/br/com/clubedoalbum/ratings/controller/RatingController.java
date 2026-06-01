package br.com.clubedoalbum.ratings.controller;

import br.com.clubedoalbum.ratings.dto.CreateOrUpdateRatingRequest;
import br.com.clubedoalbum.ratings.dto.RatingResponse;
import br.com.clubedoalbum.ratings.service.RatingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ratings")
public class RatingController {

  private final RatingService ratingService;

  public RatingController(RatingService ratingService) {
    this.ratingService = ratingService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public RatingResponse createOrUpdate(@Valid @RequestBody CreateOrUpdateRatingRequest request) {
    return ratingService.createOrUpdate(request);
  }

  @GetMapping("/albums/{albumId}")
  public List<RatingResponse> listByAlbum(@PathVariable String albumId) {
    return ratingService.listByAlbum(albumId);
  }

  @GetMapping("/users/{userId}")
  public List<RatingResponse> listByUser(@PathVariable String userId) {
    return ratingService.listByUser(userId);
  }
}
