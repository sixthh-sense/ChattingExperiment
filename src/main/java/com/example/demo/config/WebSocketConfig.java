package com.example.demo.config;

//import com.example.demo.handler.ChatHandler;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.*;
//
//@Configuration
//@RequiredArgsConstructor
//@EnableWebSocket
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/example").withSockJS();
//        // SockJS client가 websocket handshake connection을 생성할 경로
//    }
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        config.setApplicationDestinationPrefixes("/test");
//        // @MessageMapping method로 routing
//        config.enableSimpleBroker("/topic", "/queue");
//    }



    // WebSocketConfigurer

    //private final ChatHandler chatHandler;

//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(chatHandler, "/ws/chat")
//                .setAllowedOriginPatterns("http://*:8080", "http://*.*.*.*:8080")
// //               .setAllowedOrigins("http://localhost:8080")
////                .setAllowedOrigins("*") -> 보안상 문제가 큼. 하나씩 지정하는 게 나음
//                .withSockJS()
//                .setClientLibraryUrl("http://localhost:8080/myapp/js/sock-client.js");
    //}
//}
