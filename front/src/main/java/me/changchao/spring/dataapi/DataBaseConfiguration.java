package me.changchao.spring.dataapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.function.DatabaseClient;
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactory;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;

@Configuration
public class DataBaseConfiguration {
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

}
