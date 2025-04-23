package com.meikocn.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.MethodInvocationRecoverer;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {
  private final CachingConnectionFactory cachingConnectionFactory;

  @Bean
  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public RetryOperationsInterceptor retryInterceptor() {
    return RetryInterceptorBuilder.stateless()
        .maxAttempts(3)
        .backOffOptions(2000, 2.0, 10000)
        .recoverer(
            (MethodInvocationRecoverer<Void>)
                (args, cause) -> {
                  throw new AmqpRejectAndDontRequeueException(cause);
                })
        .build();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
    rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
    return rabbitTemplate;
  }

  @Bean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
      SimpleRabbitListenerContainerFactoryConfigurer configurer) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    configurer.configure(factory, cachingConnectionFactory);
    factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
    factory.setAdviceChain(retryInterceptor());
    return factory;
  }

  @Bean
  public Declarables createDeadLetterAccountInvitationSchema() {
    return new Declarables(
        new DirectExchange("x.user-invitation-failure"),
        new Queue("q.fallback-confirmation-invitation-email"),
        new Binding(
            "q.fallback-confirmation-invitation-email",
            Binding.DestinationType.QUEUE,
            "x.user-invitation-failure",
            "fallback-confirmation-invitation-email",
            null));
  }

  @Bean
  public Declarables createAccountInvitationSchema() {
    return new Declarables(
        new FanoutExchange("x.user-invitation"),
        QueueBuilder.durable("q.confirmation-invitation-email")
            .deadLetterExchange("x.user-invitation-failure")
            .deadLetterRoutingKey("fallback-confirmation-invitation-email")
            .build(),
        // new Queue(...),
        new Binding(
            "q.confirmation-invitation-email",
            Binding.DestinationType.QUEUE,
            "x.user-invitation",
            "confirmation-invitation-email",
            null)
        // new Binding(...)
        );
  }
}
