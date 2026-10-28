package cl.duoc.vidasalud.notifications.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@EnableConfigurationProperties(RabbitNames.class)
public class RabbitConfig {
    @Bean
    DirectExchange appointmentsExchange(RabbitNames names) { return new DirectExchange(names.exchange(), true, false); }

    @Bean
    DirectExchange deadLetterExchange(RabbitNames names) { return new DirectExchange(names.dlx(), true, false); }

    @Bean
    Queue notificationsQueue(RabbitNames names) {
        return QueueBuilder.durable(names.queue()).withArguments(Map.of("x-dead-letter-exchange", names.dlx(),
                "x-dead-letter-routing-key", names.deadRoutingKey())).build();
    }

    @Bean
    Queue statusQueue(RabbitNames names) {
        return QueueBuilder.durable(names.statusQueue()).withArguments(Map.of("x-dead-letter-exchange", names.dlx(),
                "x-dead-letter-routing-key", names.deadRoutingKey())).build();
    }

    @Bean
    Queue deadLetterQueue(RabbitNames names) { return QueueBuilder.durable(names.dlq()).build(); }

    @Bean
    Binding createdBinding(RabbitNames names, DirectExchange appointmentsExchange, Queue notificationsQueue) {
        return BindingBuilder.bind(notificationsQueue).to(appointmentsExchange).with(names.createdKey());
    }

    @Bean
    Binding statusBinding(RabbitNames names, DirectExchange appointmentsExchange, Queue statusQueue) {
        return BindingBuilder.bind(statusQueue).to(appointmentsExchange).with(names.statusKey());
    }

    @Bean
    Binding deadBinding(RabbitNames names, DirectExchange deadLetterExchange, Queue deadLetterQueue) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(names.deadRoutingKey());
    }

    @Bean
    Jackson2JsonMessageConverter rabbitJsonConverter() { return new Jackson2JsonMessageConverter(); }
}
