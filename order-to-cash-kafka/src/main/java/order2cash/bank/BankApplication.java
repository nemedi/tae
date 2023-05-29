package order2cash.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BankApplication {

	public static void main(String[] args) {
		System.setProperty("spring.config.name", "bank");
		SpringApplication.run(BankApplication.class, args);
	}

}
