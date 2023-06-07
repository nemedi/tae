package order2cash.buyer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import order2cache.Topics;

@RestController
public class BuyerController {
	
	@Autowired
	private BuyerDispatcher dispatcher;

	@PostMapping(value = "/purchase-order",
			consumes = {MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<Void> sendPurchaseOrder(@RequestBody String data) {
		try {
			dispatcher.dispatch(Topics.PURCHASE_ORDER.getName(), data);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
		}
	}
}
