package com.meikocn.api.component.interceptor;

import com.meikocn.api.exception.UnauthorizedException;
import java.util.Collections;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {
  private final JwtDecoder jwtDecoder;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    if (Objects.nonNull(accessor) && StompCommand.CONNECT.equals(accessor.getCommand())) {
      // Extract JWT token from header, validate it and extract user authorities
      String authHeader = accessor.getFirstNativeHeader("Authorization");
      if (Objects.isNull(authHeader) || !authHeader.startsWith("Bearer" + " ")) {
        // If there is no token present then we should interrupt handshake process
        throw new UnauthorizedException();
      }
      String token = authHeader.substring("Bearer".length() + 1);
      Jwt jwt;
      try {
        // Validate JWT token with any resource server
        jwt = jwtDecoder.decode(token);
      } catch (JwtException ex) {
        throw new UnauthorizedException(ex.getMessage());
      }
      JwtAuthenticationToken authentication =
          new JwtAuthenticationToken(jwt, Collections.emptyList());
      accessor.setUser(authentication);
    }
    return message;
  }
}
