package cl.duoc.vidasalud.appointments.messaging;

import cl.duoc.vidasalud.appointments.dto.AppointmentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "vidasalud.messaging.enabled", havingValue = "true")
public class AppointmentEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(AppointmentEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;
    private final String exchange;

    public AppointmentEventPublisher(RabbitTemplate rabbitTemplate,
                                     @Value("${vidasalud.messaging.exchange}") String exchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    public void publishAfterCommit(AppointmentResponse appointment, String routingKey) {
        Runnable send = () -> {
            try {
                Map<String, Object> event = Map.of(
                        "eventId", UUID.randomUUID().toString(),
                        "eventType", routingKey,
                        "occurredAt", Instant.now().toString(),
                        "appointment", appointment
                );
                rabbitTemplate.convertAndSend(exchange, routingKey, event, message -> {
                    message.getMessageProperties().setMessageId((String) event.get("eventId"));
                    message.getMessageProperties().setContentType("application/json");
                    message.getMessageProperties().setDeliveryMode(org.springframework.amqp.core.MessageDeliveryMode.PERSISTENT);
                    return message;
                });
                log.info("Evento publicado {} cita {}", routingKey, appointment.id());
            } catch (Exception ex) {
                log.error("No fue posible publicar evento de cita {}", appointment.id(), ex);
            }
        };
        if (TransactionSynchronizationManager.isActualTransactionActive() && TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() { send.run(); }
            });
        } else {
            send.run();
        }
    }
}
