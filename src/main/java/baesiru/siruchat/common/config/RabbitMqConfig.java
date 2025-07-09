package baesiru.siruchat.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
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
    @Value("${spring.rabbitmq.chat.send.queue}")
    private String sendQueue;
    @Value("${spring.rabbitmq.chat.send.routing-key}")
    private String sendRoutingKey;


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
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange imageExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue sendQueue() {
        return new Queue(sendQueue);
    }

    @Bean
    public Binding sendBinding() {
        return BindingBuilder.bind(sendQueue())
                .to(imageExchange())
                .with(sendRoutingKey);
    }

}
