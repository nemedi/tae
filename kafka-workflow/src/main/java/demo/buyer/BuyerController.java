package demo.buyer;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import demo.MainDispatcher;

@RestController
public class BuyerController {
	
	@Autowired
	private MainDispatcher dispatcher;

	@PostMapping(value = "/purchase-orders",
			consumes = {MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<Void> sendPurchaseOrder(@RequestBody String data) {
		try {
			dispatcher.dispatch("order", data);
			return ResponseEntity.noContent().build();
		} catch (InterruptedException | ExecutionException e) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
		}
	}
}
