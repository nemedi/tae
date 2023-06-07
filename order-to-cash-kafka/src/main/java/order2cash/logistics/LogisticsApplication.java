package order2cash.logistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LogisticsApplication {

	public static void main(String[] args) {
		System.setProperty("spring.config.name", "logistics");
		SpringApplication.run(LogisticsApplication.class, args);
	}

}
