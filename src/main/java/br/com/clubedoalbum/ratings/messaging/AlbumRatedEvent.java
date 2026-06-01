package br.com.clubedoalbum.ratings.messaging;

public record AlbumRatedEvent(
    String event,
    String albumId,
    String userId,
    Double rating,
    String occurredAt) {}
