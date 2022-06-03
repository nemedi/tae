package order2cache.supplier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SupplierApplication {

	public static void main(String[] args) {
		System.setProperty("spring.config.name", "supplier");
		SpringApplication.run(SupplierApplication.class, args);
	}

}
