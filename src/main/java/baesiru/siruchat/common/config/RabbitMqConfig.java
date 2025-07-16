package baesiru.siruchat.common.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value("${spring.rabbitmq.port}")
    private int port;
    @Value("${spring.rabbitmq.chat.exchange}")
    private String exchange;
    @Value("${spring.rabbitmq.chat.save.queue}")
    private String saveQueue;
    @Value("${spring.rabbitmq.chat.save.routing-key}")
    private String saveRoutingKey;


    @Bean
    ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(4);
        factory.setMaxConcurrentConsumers(8);
        factory.setPrefetchCount(150);
        factory.setMessageConverter(messageConverter());
        return factory;
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange roomExchange() {
        return new TopicExchange(exchange);
    }


    @Bean
    public Queue saveQueue() {
        return QueueBuilder.durable(saveQueue)
                .withArgument("x-dead-letter-exchange", exchange)
                .withArgument("x-dead-letter-routing-key", saveRoutingKey + ".dlq")
                .build();
    }

    @Bean
    public Binding saveBinding() {
        return BindingBuilder.bind(saveQueue())
                .to(roomExchange())
                .with(saveRoutingKey);
    }


    @Bean
    public Queue saveDlqQueue() {
        return QueueBuilder.durable(saveQueue + ".dlq").build();
    }

    @Bean
    public Binding saveDlqBinding() {
        return BindingBuilder.bind(saveDlqQueue())
                .to(roomExchange())
                .with(saveRoutingKey + ".dlq");
    }
}
