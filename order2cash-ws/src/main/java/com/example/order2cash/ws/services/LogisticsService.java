package com.example.order2cash.ws.services;

import static com.example.order2cash.ws.util.XmlOutputWriter.write;
import static com.example.order2cash.ws.util.XsltTransformer.transform;

import java.time.LocalDate;
import java.util.Map;

import jakarta.activation.DataHandler;
import jakarta.jws.WebService;
import jakarta.xml.ws.soap.MTOM;

@WebService(targetNamespace = "http://example.com", serviceName = "logistics",
	endpointInterface = "com.example.order2cash.ws.services.ILogisticsService")
@MTOM
public class LogisticsService extends AbstractService implements ILogisticsService {

	public LogisticsService(Map<String, String> endpoints) {
		super(endpoints);
	}

	@Override
	public void onShipmentRequest(DataHandler shipmentRequestHandler, String workflowId) {
		try {
			onShipmentRequest(convertToString(shipmentRequestHandler), workflowId);
		} catch (Exception e) {
			log.error("[LOGISTICS] Error processing ShipmentRequest: {}", e.getMessage());
		}
	}

	// ── Step 5: Logistics receives ShipmentRequest → ShipmentNotification ─────
	private void onShipmentRequest(String shipmentRequestXml, String workflowId)
			throws Exception {
		log.info("");
		log.info("┌─ [LOGISTICS] Step 5 ─ ShipmentRequest → ShipmentNotification ─");
		log.info("│  WorkflowId : {}", workflowId);
		log.debug("│  ShipmentRequest XML:\n{}", shipmentRequestXml);
		String trackingNumber = "TRACK-" + shortUuid();
		LocalDate shipDate = LocalDate.now();
		String shipmentNotificationXml = transform(shipmentRequestXml,
				"/xsl/04-shipment-request-to-shipment-notification.xsl",
				Map.of("newId", "SHIPNOTIF-" + shortUuid(),
						"shipmentDate", shipDate.toString(),
						"estimatedDeliveryDate", shipDate.plusDays(6).toString(),
						"trackingNumber", trackingNumber));
		write(workflowId, "05-ShipmentNotification.xml", shipmentNotificationXml);
		getServiceStub("supplier", ISupplierService.class)
			.onShipmentNotification(convertToDataHandler(shipmentNotificationXml), workflowId);
		log.info("│  Tracking   : {}", trackingNumber);
		log.info("└─────────────────────────────────────────────────────────────");
		log.debug("\n{}", shipmentNotificationXml);
	}
}
