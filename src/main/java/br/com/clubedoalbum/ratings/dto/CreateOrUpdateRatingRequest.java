package br.com.clubedoalbum.ratings.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrUpdateRatingRequest(
    @NotBlank String albumId,
    @NotBlank String userId,
    @NotNull @DecimalMin("0.5") @DecimalMax("5.0") Double rating) {}
