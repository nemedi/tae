package order2cache.buyer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BuyerApplication {

	public static void main(String[] args) {
		System.setProperty("spring.config.name", "buyer");
		SpringApplication.run(BuyerApplication.class, args);
	}

}
