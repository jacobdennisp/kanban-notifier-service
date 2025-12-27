package com.kanban.todo.notifier.config;
//AMQP - Advanced Message Queuing Protocol
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration
 * 
 * Configures RabbitMQ connection, queues, exchanges, and message converters.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    /**
     * Declare the task queue
     */
    @Bean
    public Queue taskQueue() {
        return QueueBuilder.durable(queueName)
                .withArgument("x-message-ttl", 86400000) // 24 hours
                .build();
    }

    /**
     * Declare the topic exchange
     */
    @Bean
    public TopicExchange taskExchange() {
        return new TopicExchange(exchangeName);
    }

    /**
     * Bind queue to exchange with routing key
     */
    @Bean
    public Binding binding(Queue taskQueue, TopicExchange taskExchange) {
        return BindingBuilder
                .bind(taskQueue)
                .to(taskExchange)
                .with(routingKey);
    }

    /**
     * JSON message converter for RabbitMQ
     */
    @SuppressWarnings("removal")
	@Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitMQ template with JSON converter
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * Configure listener container factory
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(1);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }
}