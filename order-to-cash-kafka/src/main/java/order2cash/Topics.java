package order2cash;

import java.util.Optional;
import java.util.stream.Stream;

public enum Topics {

	PURCHASE_ORDER("purchase-order"),
	PURCHASE_ORDER_ACKNOWLEDGEMENT("purchase-order-acknowledgement"),
	INVOICE("invoice"),
	SHIPMENT_NOTICE("shipment-notice"),
	SHIPMENT_SCHEDULE("shipment-schedule"),
	LOGISTIC_SERVICE_REQUEST("logistic-service-request"),
	LOGISTIC_SERVICE_RESPONSE("logistic-service-response"),
	CONSOLIDATORS_FREIGHT_BILL("consolidators-freight-bill"),
	SHIPMENT_INFORMATION("shipment-information"),
	PAYMENT_ADVICE("payment-advice");

	private String name;

	private Topics(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static Optional<Topics> findTopicByName(String name) {
		return Stream.of(values())
				.filter(t -> name.equals(t.getName()))
				.findAny();
	}
}
