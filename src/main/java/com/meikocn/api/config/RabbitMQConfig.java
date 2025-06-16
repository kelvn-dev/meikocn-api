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
  public static final String USER_INVITATION_EXCHANGE = "x.user-invitation";
  public static final String CONFIRMMATION_INVITATION_QUEUE = "q.confirmation-invitation-email";
  public static final String CONFIRMMATION_INVITATION_ROUTINGKEY = "confirmation-invitation-email";
  public static final String USER_INVITATION_FALLBACK_EXCHANGE = "x.user-invitation-failure";
  public static final String CONFIRMMATION_INVITATION_FALLBACK_QUEUE =
      "q.fallback-confirmation-invitation-email";
  public static final String CONFIRMMATION_INVITATION_FALLBACK_ROUTINGKEY =
      "fallback-confirmation-invitation-email";

  public static final String TASK_REMINDER_EXCHANGE = "x.task-reminder";
  public static final String TASK_REMINDER_QUEUE = "q.task-reminder-email";
  public static final String TASK_REMINDER_ROUTINGKEY = "task-reminder-email";
  public static final String TASK_REMINDER_FALLBACK_EXCHANGE = "x.task-reminder-failure";
  public static final String TASK_REMINDER_FALLBACK_QUEUE = "q.fallback-task-reminder-email";
  public static final String TASK_REMINDER_FALLBACK_ROUTINGKEY = "fallback-task-reminder-email";

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
        new DirectExchange(USER_INVITATION_FALLBACK_EXCHANGE),
        new Queue(CONFIRMMATION_INVITATION_FALLBACK_QUEUE),
        new Binding(
            CONFIRMMATION_INVITATION_FALLBACK_QUEUE,
            Binding.DestinationType.QUEUE,
            USER_INVITATION_FALLBACK_EXCHANGE,
            CONFIRMMATION_INVITATION_FALLBACK_ROUTINGKEY,
            null));
  }

  @Bean
  public Declarables createAccountInvitationSchema() {
    return new Declarables(
        new DirectExchange(USER_INVITATION_EXCHANGE),
        QueueBuilder.durable(CONFIRMMATION_INVITATION_QUEUE)
            .deadLetterExchange(USER_INVITATION_FALLBACK_EXCHANGE)
            .deadLetterRoutingKey(CONFIRMMATION_INVITATION_FALLBACK_ROUTINGKEY)
            .build(),
        // new Queue(...),
        new Binding(
            CONFIRMMATION_INVITATION_QUEUE,
            Binding.DestinationType.QUEUE,
            USER_INVITATION_EXCHANGE,
            CONFIRMMATION_INVITATION_ROUTINGKEY,
            null)
        // new Binding(...)
        );
  }

  @Bean
  public Declarables createDeadLetterTaskReminderSchema() {
    return new Declarables(
        new DirectExchange(TASK_REMINDER_FALLBACK_EXCHANGE),
        new Queue(TASK_REMINDER_FALLBACK_QUEUE),
        new Binding(
            TASK_REMINDER_FALLBACK_QUEUE,
            Binding.DestinationType.QUEUE,
            TASK_REMINDER_FALLBACK_EXCHANGE,
            TASK_REMINDER_FALLBACK_ROUTINGKEY,
            null));
  }

  @Bean
  public Declarables createTaskReminderSchema() {
    return new Declarables(
        new DirectExchange(TASK_REMINDER_EXCHANGE),
        QueueBuilder.durable(TASK_REMINDER_QUEUE)
            .deadLetterExchange(TASK_REMINDER_FALLBACK_EXCHANGE)
            .deadLetterRoutingKey(TASK_REMINDER_FALLBACK_ROUTINGKEY)
            .build(),
        new Binding(
            TASK_REMINDER_QUEUE,
            Binding.DestinationType.QUEUE,
            TASK_REMINDER_EXCHANGE,
            TASK_REMINDER_ROUTINGKEY,
            null));
  }
}
