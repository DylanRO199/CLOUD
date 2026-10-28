package cl.duoc.vidasalud.notifications.consumer;

import cl.duoc.vidasalud.notifications.service.NotificationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AppointmentConsumer {
    private static final Logger log = LoggerFactory.getLogger(AppointmentConsumer.class);
    private final NotificationService service;
    private final ObjectMapper mapper;

    public AppointmentConsumer(NotificationService service, ObjectMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @RabbitListener(queues = "${vidasalud.rabbit.queue}", ackMode = "MANUAL")
    public void onCreated(Message message, Channel channel) throws IOException { consume(message, channel); }

    @RabbitListener(queues = "${vidasalud.rabbit.status-queue}", ackMode = "MANUAL")
    public void onStatus(Message message, Channel channel) throws IOException { consume(message, channel); }

    private void consume(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            JsonNode event = mapper.readTree(message.getBody());
            service.process(event);
            channel.basicAck(tag, false);
        } catch (Exception ex) {
            log.error("Mensaje rechazado y dirigido a DLQ: {}", ex.getMessage(), ex);
            channel.basicNack(tag, false, false);
        }
    }
}
