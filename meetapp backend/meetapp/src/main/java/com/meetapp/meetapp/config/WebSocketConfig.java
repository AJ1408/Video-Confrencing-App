package com.meetapp.meetapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

		@Override
		public void configureMessageBroker(MessageBrokerRegistry registry) {

				//messages whose destination starts with /app will be routed to message-handling methods (i.e., @MessageMapping)
				registry.setApplicationDestinationPrefixes("/app");

				registry.enableSimpleBroker("/topic");
		}

		@Override
		public void registerStompEndpoints(StompEndpointRegistry registry) {

				// this is the door that frontend will use to connect to backend
				// allowing all origins to connect
				registry.addEndpoint("/ws")
								.setAllowedOrigins("http://localhost:5173")
								.withSockJS();
		}
}
