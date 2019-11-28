package com.runtrend.realtimecomm;


import com.runtrend.realtimecomm.netty.TCPServer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class RealtimecommApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealtimecommApplication.class, args);
	}

	private final TCPServer tcpServer;

	@SuppressWarnings({"Convert2Lambda"})
	@Bean
	public ApplicationListener<ApplicationReadyEvent> readyEventApplicationListener() {
		return new ApplicationListener<ApplicationReadyEvent>() {
			@Override
			public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
				tcpServer.start();
			}
		};
	}
}
