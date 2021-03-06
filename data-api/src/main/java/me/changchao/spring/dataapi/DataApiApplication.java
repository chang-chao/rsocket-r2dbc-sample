package me.changchao.spring.dataapi;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import com.google.gson.Gson;

import io.rsocket.AbstractRSocket;
import io.rsocket.Closeable;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.SocketAcceptor;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.SneakyThrows;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class DataApiApplication implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	CityRepository cityRepository;

	@SneakyThrows
	public static void main(String[] args) {
		SpringApplication.run(DataApiApplication.class, args);
	}

	private Gson gson = new Gson();

	@Override
	public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
		SocketAcceptor sa = (connectionSetupPayload, rSocket) -> Mono.just(new AbstractRSocket() {

			@Override
			public Flux<Payload> requestStream(Payload payload) {
				return cityRepository.findAll().map(c -> DefaultPayload.create(gson.toJson(c)));
			}
		});

		Closeable server = RSocketFactory.receive().acceptor(sa).transport(TcpServerTransport.create("localhost", 7000))
				.start().block();
		server.onClose().block(Duration.ofDays(3650));

	}
}
