package com.vgrental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class VideoGameRentalSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoGameRentalSpringApplication.class, args);
	}

}
