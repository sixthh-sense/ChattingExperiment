package com.example.demo.config;

import com.example.demo.handler.StompHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.ChannelRegistration;
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

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub");
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp").setAllowedOriginPatterns("*") // AllowedOriginPatterns와 AllowedOrigin은 무슨 차이가 있는 걸까? 아무튼 patterns가 좀더 융통성이 있는 듯하니 그쪽으로 go!
                .withSockJS();
    }

    @Override
    // 메세지를 받았을때 최초에 stompHandler 가 인터셉트 하도록 설정
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);

    }
}