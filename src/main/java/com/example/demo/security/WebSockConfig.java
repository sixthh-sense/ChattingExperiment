package com.example.demo.security;


import com.example.demo.handler.StompHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSockConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;
    // StompHandler는 무슨 역할을 하는 걸까?

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //구독용("sub"scribe)
        registry.enableSimpleBroker("/sub");
        //발행용("pub"lish)
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setHeartbeatTime(25000);
        // .setHeartbeatTime(25000); 도 필요한 걸까? 조금 더 알아볼 것.
        // .setAllowedOrigins와 .setAllowedOriginPatterns는 뭐가 다를까?
    }


    // 메세지를 받았을 때 최초에 stompHandler가 intercept하게끔 설정
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }

}