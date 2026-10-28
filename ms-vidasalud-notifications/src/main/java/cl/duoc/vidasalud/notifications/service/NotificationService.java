package cl.duoc.vidasalud.notifications.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void process(JsonNode event) {
        if (!event.hasNonNull("eventId") || !event.hasNonNull("eventType") || !event.path("appointment").hasNonNull("id")) {
            throw new IllegalArgumentException("Evento de cita inválido");
        }
        if (event.path("simulateFailure").asBoolean(false)) {
            throw new IllegalArgumentException("Evento rechazado para prueba de DLQ");
        }
        log.info("Notificación generada evento={} tipo={} cita={}", event.path("eventId").asText(),
                event.path("eventType").asText(), event.path("appointment").path("id").asLong());
    }
}
