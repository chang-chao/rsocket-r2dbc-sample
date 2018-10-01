package me.changchao.spring.dataapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
public class CityController {
	@Autowired
	CityRepository cityRepository;

	@GetMapping("/")
	public Flux<City> getAllTweets() {
		return cityRepository.findAll();
	}
}
