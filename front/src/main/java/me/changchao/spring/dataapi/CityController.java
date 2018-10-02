package me.changchao.spring.dataapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;

@RestController
public class CityController {

	@GetMapping("/")
	public Flux<String> getAllCities() {
		return RSocketFactory.connect().transport(TcpClientTransport.create("localhost", 7000)).start()
				.flatMapMany(socket -> socket.requestStream(DefaultPayload.create("Hello")).map(Payload::getDataUtf8)
						.doFinally(signal -> socket.dispose()));
	}
}
