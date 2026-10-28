package cl.duoc.vidasalud.notifications.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vidasalud.rabbit")
public record RabbitNames(String exchange, String queue, String statusQueue, String dlx, String dlq, String deadRoutingKey, String createdKey, String statusKey) {}
