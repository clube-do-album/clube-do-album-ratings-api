package br.com.clubedoalbum.ratings.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record PaginatedResponse<T>(
    List<T> items,
    int page,
    int limit,
    long total,
    int totalPages,
    boolean hasNextPage) {

  public static <T> PaginatedResponse<T> from(Page<T> page) {
    return new PaginatedResponse<>(
        page.getContent(),
        page.getNumber() + 1,
        page.getSize(),
        page.getTotalElements(),
        Math.max(1, page.getTotalPages()),
        page.hasNext());
  }
}
