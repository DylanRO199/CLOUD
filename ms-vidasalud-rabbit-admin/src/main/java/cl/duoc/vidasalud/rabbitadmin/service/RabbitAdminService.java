package cl.duoc.vidasalud.rabbitadmin.service;

import cl.duoc.vidasalud.rabbitadmin.dto.*;
import org.springframework.amqp.core.*;
import org.springframework.stereotype.Service;

@Service
public class RabbitAdminService {
    private final AmqpAdmin admin;

    public RabbitAdminService(AmqpAdmin admin) { this.admin = admin; }

    public void createQueue(QueueRequest request) {
        admin.declareQueue(new Queue(request.name(), request.durable(), false, false));
    }

    public void deleteQueue(String name) { admin.deleteQueue(name); }

    public void createExchange(ExchangeRequest request) {
        Exchange exchange = switch (request.type()) {
            case "direct" -> new DirectExchange(request.name(), true, false);
            case "topic" -> new TopicExchange(request.name(), true, false);
            case "fanout" -> new FanoutExchange(request.name(), true, false);
            default -> throw new IllegalArgumentException("Tipo de exchange inválido");
        };
        admin.declareExchange(exchange);
    }

    public void deleteExchange(String name) { admin.deleteExchange(name); }

    public void createBinding(BindingRequest request) {
        admin.declareBinding(new Binding(request.queue(), Binding.DestinationType.QUEUE,
                request.exchange(), request.routingKey() == null ? "" : request.routingKey(), null));
    }

    public void deleteBinding(BindingRequest request) {
        admin.removeBinding(new Binding(request.queue(), Binding.DestinationType.QUEUE,
                request.exchange(), request.routingKey() == null ? "" : request.routingKey(), null));
    }
}
