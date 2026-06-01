package br.com.clubedoalbum.ratings.messaging;

import br.com.clubedoalbum.ratings.domain.Rating;
import java.time.Instant;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AlbumRatedPublisher {

  private final RabbitTemplate rabbitTemplate;
  private final TopicExchange exchange;
  private final String routingKey;

  public AlbumRatedPublisher(
      RabbitTemplate rabbitTemplate,
      TopicExchange exchange,
      @Value("${app.rabbitmq.album-rated-routing-key}") String routingKey) {
    this.rabbitTemplate = rabbitTemplate;
    this.exchange = exchange;
    this.routingKey = routingKey;
  }

  public void publish(Rating rating) {
    var event =
        new AlbumRatedEvent(
            "ALBUM_RATED",
            rating.getAlbumId(),
            rating.getUserId(),
            rating.getRatingValue(),
            Instant.now().toString());

    rabbitTemplate.convertAndSend(exchange.getName(), routingKey, event);
  }
}
