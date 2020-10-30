package com.example.springrsocketclient;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.rsocket.ClientRSocketFactoryConfigurer;
import org.springframework.messaging.rsocket.MetadataExtractor;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

@SpringBootApplication
public class SpringRsocketClientApplication {

	@Bean
	RSocket rSocket(){
		return RSocketFactory
				.connect()
				.dataMimeType(MimeTypeUtils.APPLICATION_JSON_VALUE)
				.frameDecoder(PayloadDecoder.ZERO_COPY)
				.transport(TcpClientTransport.create(8000))
				.start().block();
	}

	@Bean
	RSocketRequester requester(RSocketStrategies strategies) {
		return RSocketRequester.builder()
				.rsocketFactory(clientRSocketFactory ->
						clientRSocketFactory
								.dataMimeType(MimeTypeUtils.ALL_VALUE)
								.frameDecoder(PayloadDecoder.ZERO_COPY))
				.rsocketStrategies(strategies)
				.connect(TcpClientTransport.create(8000))
				.retry().block();
	}


	public static void main(String[] args) {
		SpringApplication.run(SpringRsocketClientApplication.class, args);
	}

}
