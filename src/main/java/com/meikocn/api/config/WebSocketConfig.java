package com.meikocn.api.config;

import com.meikocn.api.component.interceptor.AuthChannelInterceptor;
import javax.inject.Inject;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  @Inject private AuthChannelInterceptor authChannelInterceptor;
  @Inject private RabbitMQPropConfig rabbitMQPropConfig;

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(authChannelInterceptor);
    WebSocketMessageBrokerConfigurer.super.configureClientInboundChannel(registration);
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config
        .enableStompBrokerRelay("/queue")
        .setRelayHost(rabbitMQPropConfig.getHost())
        .setRelayPort(rabbitMQPropConfig.getStompPort())
        .setSystemLogin(rabbitMQPropConfig.getUsername())
        .setSystemPasscode(rabbitMQPropConfig.getPassword())
        .setClientLogin(rabbitMQPropConfig.getUsername())
        .setClientPasscode(rabbitMQPropConfig.getPassword());
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/stomp").setAllowedOriginPatterns("*");
  }
}
