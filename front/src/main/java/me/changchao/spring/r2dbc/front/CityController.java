package me.changchao.spring.r2dbc.front;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import lombok.SneakyThrows;
import reactor.core.publisher.Flux;

@RestController
public class CityController {
	@Autowired
	private ObjectMapper om;

	@GetMapping("/")
	public Flux<City> list() {
		return RSocketFactory.connect().transport(TcpClientTransport.create("localhost", 7000)).start()
				.flatMapMany(socket -> socket.requestStream(DefaultPayload.create("Hello")).map(Payload::getDataUtf8)
						.map(str -> new City()).take(10).doFinally(signal -> socket.dispose()));

	}

	@SneakyThrows
	private City mapToCity(Payload response) {
		return om.readValue(response.getData().array(), City.class);
	}
}
