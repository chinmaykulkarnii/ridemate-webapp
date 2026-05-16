package com.ridemate;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import static org.springframework.boot.SpringApplication.*;

//exclude = {DataSourceAutoConfiguration.class}
@SpringBootApplication()
public class RideMateApplication {

	public static void main(String[] args) {
		System.out.println("Hi");
        run(RideMateApplication.class, args);
    }
}
