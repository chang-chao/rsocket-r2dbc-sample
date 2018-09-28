package me.changchao.spring.dataapi;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.function.DatabaseClient;
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactory;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;

import com.google.gson.Gson;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.SocketAcceptor;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@Log4j2
public class DataApiApplication implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	CityRepository cityRepository;

	@SneakyThrows
	public static void main(String[] args) {
		SpringApplication.run(DataApiApplication.class, args);
		System.in.read();
	}

	@Bean
	CityRepository cityRepository(R2dbcRepositoryFactory factory) {
		return factory.getRepository(CityRepository.class);
	}

	@Bean
	R2dbcRepositoryFactory repositoryFactory(DatabaseClient client) {

		RelationalMappingContext context = new RelationalMappingContext();
		context.afterPropertiesSet();

		return new R2dbcRepositoryFactory(client, context);
	}

	@Bean
	DatabaseClient databaseClient(ConnectionFactory factory) {

		return DatabaseClient.builder() //
				.connectionFactory(factory) //
				.build();
	}

	@Bean
	PostgresqlConnectionFactory connectionFactory() {

		PostgresqlConnectionConfiguration config = PostgresqlConnectionConfiguration.builder() //
				.host("localhost") //
				.port(5432) //
				.database("postgres") //
				.username("postgres") //
				.password("") //
				.build();
		return new PostgresqlConnectionFactory(config);
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

		RSocketFactory.receive().acceptor(sa).transport(TcpServerTransport.create("localhost", 7000)).start()
				.onTerminateDetach()
				.subscribe(nettyContextCloseable -> log.info("started the server @ " + Instant.now().toString()));
	}
}
