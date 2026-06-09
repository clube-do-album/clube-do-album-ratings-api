package br.com.clubedoalbum.ratings.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateOrUpdateRatingRequest(
    @NotBlank String albumId,
    @NotNull @DecimalMin("0.5") @DecimalMax("5.0") Double rating,
    @Size(max = 1000) String review) {}
