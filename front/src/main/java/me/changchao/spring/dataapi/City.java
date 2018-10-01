package me.changchao.spring.dataapi;

import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class City {

	@Id
	private Long id;

	private String name;

	private String country;

}