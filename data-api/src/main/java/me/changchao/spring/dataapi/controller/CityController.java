package me.changchao.spring.dataapi.controller;

import java.time.Duration;
import java.util.function.BiFunction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.r2dbc.client.R2dbc;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import me.changchao.spring.dataapi.City;
import reactor.core.publisher.Flux;

@RestController
public class CityController {

	@Autowired
	PostgresqlConnectionFactory connectionFactory;

	@GetMapping("/")
	public Flux<City> list() {
		R2dbc r2dbc = new R2dbc(connectionFactory);
		return r2dbc.inTransaction(handle -> {
			return handle.select("select * from city").mapRow(f).timeout(Duration.ofMillis(10));
		});
	}

	BiFunction<Row, RowMetadata, City> f = (row, meta) -> {
		City city = new City();
		city.setCountry(row.get("country", String.class));
		city.setId(row.get("id", Long.class));
		city.setName(row.get("name", String.class));
		return city;
	};
}
