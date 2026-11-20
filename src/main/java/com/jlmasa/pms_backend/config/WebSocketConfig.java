package com.jlmasa.pms_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

    private final WebsocketAuthInterceptor authInterceptor;
    private final WebSocketAuthorizationInterceptor authorizationInterceptor;

    public WebSocketConfig(WebSocketAuthInterceptor authInterceptor, WebSocketAuthorizationInterceptor authorizationInterceptor){
        this.authInterceptor = authInterceptor;
        this.authorizationInterceptor = authorizationInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        String originsEnv = System.getenv().getOrDefault("PMS_CORS_ORIGINS", "");
        registry.addEndpoint("/ws")
                .setAllowedOrigins(originsEnv.isBlank() ? new String[0] : originsEnv.split(","));
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration){
        registration.interceptors(authInterceptor, authorizationInterceptor);
    }
}
