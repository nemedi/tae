package com.example.order2cash.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.order2cash.ws.services.BankService;
import com.example.order2cash.ws.services.BuyerService;
import com.example.order2cash.ws.services.InventoryService;
import com.example.order2cash.ws.services.LogisticsService;
import com.example.order2cash.ws.services.SupplierService;

import jakarta.xml.ws.Endpoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class Order2CashMain {

	private static final Logger log = LoggerFactory.getLogger(Order2CashMain.class);

	public static void main(String[] args) throws InterruptedException {
		String serviceType = System.getenv().getOrDefault("SERVICE_TYPE", "unknown").toLowerCase();
		try {
			final ResourceBundle bundle = ResourceBundle.getBundle("application");
			final Map<String, String> endpoints = new HashMap<String, String>();
			endpoints.put("buyer", bundle.containsKey("BUYER_ENDPOINT")
					? bundle.getString("BUYER_ENDPOINT") : System.getenv().get("BUYER_ENDPOINT"));
			endpoints.put("supplier", bundle.containsKey("SUPPLIER_ENDPOINT")
					? bundle.getString("SUPPLIER_ENDPOINT") : System.getenv().get("SUPPLIER_ENDPOINT"));
			endpoints.put("inventory", bundle.containsKey("INVENTORY_ENDPOINT")
					? bundle.getString("INVENTORY_ENDPOINT") : System.getenv().get("INVENTORY_ENDPOINT"));
			endpoints.put("logistics", bundle.containsKey("LOGISTICS_ENDPOINT")
					? bundle.getString("LOGISTICS_ENDPOINT") : System.getenv().get("LOGISTICS_ENDPOINT"));
			endpoints.put("bank", bundle.containsKey("BANK_ENDPOINT")
					? bundle.getString("BANK_ENDPOINT") : System.getenv().get("BANK_ENDPOINT"));
			log.info("Starting Order2Cash service: {}", serviceType);
			Object service = null;
			switch (serviceType) {
			case "buyer":
				service = new BuyerService(endpoints);
				break;
			case "supplier":
				service = new SupplierService(endpoints);
				break;
			case "inventory":
				service = new InventoryService(endpoints);
				break;
			case "logistics":
				service = new LogisticsService(endpoints);
				break;
			case "bank":
				service = new BankService(endpoints);
				break;
			default:
				log.error("Unknown SERVICE_TYPE '{}'. Valid values: buyer, supplier, inventory, logistics, bank",
						serviceType);
				System.exit(1);
			}
			Endpoint.publish(endpoints.get(serviceType), service);
			log.info("Service '{}' running. Ctrl+C to stop.", serviceType);
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				log.info("Shutting down service: {}", serviceType);
			}));
			new CountDownLatch(1).await();
		} catch (IOException | InterruptedException e) {
			log.error("[{}] Error: {}", serviceType.toUpperCase(), e.getMessage());
		}
	}
}
