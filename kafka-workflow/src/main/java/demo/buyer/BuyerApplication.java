package demo.buyer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"demo", "demo.buyer"})
public class BuyerApplication {

	public static void main(String[] args) {
		System.setProperty("spring.config.name", "buyer");
		SpringApplication.run(BuyerApplication.class, args);
	}

}
