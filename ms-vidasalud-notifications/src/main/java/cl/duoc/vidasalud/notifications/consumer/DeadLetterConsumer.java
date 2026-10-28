package cl.duoc.vidasalud.notifications.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DeadLetterConsumer {
    private static final Logger log = LoggerFactory.getLogger(DeadLetterConsumer.class);

    @RabbitListener(queues = "${vidasalud.rabbit.dlq}", autoStartup = "false")
    public void inspect(Message message) {
        log.error("Mensaje en DLQ: {}", new String(message.getBody(), java.nio.charset.StandardCharsets.UTF_8));
    }
}
