package br.com.clubedoalbum.ratings.dto;

public record UserRatingSummaryResponse(
    long totalRatings,
    long reviewCount,
    double averageRating) {}
