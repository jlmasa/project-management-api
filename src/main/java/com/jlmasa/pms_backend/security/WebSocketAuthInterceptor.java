package com.jlmasa.pms_backend.security;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;
    private final MembershipAuthenticationConverter membershipAuthenticationConverter;

    public WebSocketAuthInterceptor(JwtDecoder jwtDecoder, MembershipAuthenticationConverter membershipAuthenticationConverter){
        this.jwtDecoder = jwtDecoder;
        this.membershipAuthenticationConverter = membershipAuthenticationConverter;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if(StompCommand.CONNECT.equals(accessor.getCommand())){
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if(authHeader == null || !authHeader.startsWith("Bearer ")){
                throw new BadCredentialsException("Missing bearer token on CONNECT");
            }
            try{
                Jwt jwt = jwtDecoder.decode(authHeader.substring("Bearer ".length()));
                var authentication = membershipAuthenticationConverter.convert(jwt);
                accessor.setUser(authentication);
            }catch (Exception ex){
                throw new BadCredentialsException("Invalid or expired token.", ex);
            }
        }
        return message;
    }
}
