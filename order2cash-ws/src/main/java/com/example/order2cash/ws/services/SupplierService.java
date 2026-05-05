package com.example.order2cash.ws.services;

import static com.example.order2cash.ws.util.XmlOutputWriter.write;
import static com.example.order2cash.ws.util.XsltTransformer.transform;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import jakarta.activation.DataHandler;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlMimeType;
import jakarta.xml.ws.soap.MTOM;

@WebService(targetNamespace = "http://example.com", serviceName = "supplier",
	endpointInterface = "com.example.order2cash.ws.services.ISupplierService")
@MTOM
public class SupplierService extends AbstractService implements ISupplierService {

	public SupplierService(Map<String, String> endpoints) {
		super(endpoints);
	}

	@Override
	public void onPurchaseOrder(@XmlMimeType("application/octet-stream") DataHandler purchaseOrderHandler, String workflowId) {
		try {
			onPurchaseOrder(convertToString(purchaseOrderHandler), workflowId);
		} catch (Exception e) {
			log.error("[SUPPLIER] Error processing PurchaseOrder: {}", e.getMessage());
		}
	}

	@Override
	public void onInventoryResponse(@XmlMimeType("application/octet-stream") DataHandler inventoryResponseHandler, String workflowId) {
		try {
			onInventoryResponse(convertToString(inventoryResponseHandler), workflowId);
		} catch (Exception e) {
			log.error("[SUPPLIER] Error processing InventoryResponse: {}", e.getMessage());
		}
	}

	@Override
	public void onShipmentNotification(@XmlMimeType("application/octet-stream") DataHandler shipmentNotificationHandler, String workflowId) {
		try {
			onShipmentNotification(convertToString(shipmentNotificationHandler), workflowId);
		} catch (Exception e) {
			log.error("[SUPPLIER] Error processing ShipmentNotification: {}", e.getMessage());
		}
	}

	@Override
	public void onPaymentConfirmation(@XmlMimeType("application/octet-stream") DataHandler paymentConfirmationHandler, String workflowId) {
		try {
			onPaymentConfirmation(convertToString(paymentConfirmationHandler), workflowId);
		} catch (IOException e) {
			log.error("[SUPPLIER] Error processing PaymentConfirmation: {}", e.getMessage());
		}
	}

	// ── Step 2: Supplier receives PurchaseOrder → InventoryRequest ────────────
	private void onPurchaseOrder(String purchaseOrderXml, String workflowId)
			throws Exception {
		log.info("");
		log.info("┌─ [SUPPLIER] Step 2 ─ PurchaseOrder received → InventoryRequest");
		log.info("│  WorkflowId : {}", workflowId);
		log.debug("│  PurchaseOrder XML:\n{}", purchaseOrderXml);
		String inventoryRequestXml = transform(purchaseOrderXml,
				"/xsl/01-purchase-order-to-inventory-request.xsl",
				Map.of("newId", "INVREQ-" + shortUuid(),
						"currentDate", LocalDate.now().toString()));
		write(workflowId, "02-InventoryRequest.xml", inventoryRequestXml);
		getServiceStub("inventory", IInventoryService.class)
				.onInventoryRequest(convertToDataHandler(inventoryRequestXml), workflowId);
		log.info("└─────────────────────────────────────────────────────────────");
		log.debug("\n{}", inventoryRequestXml);
	}

	// ── Step 4: Supplier receives InventoryResponse → ShipmentRequest ─────────
	private void onInventoryResponse(String inventoryResponseXml, String workflowId)
			throws Exception {
		log.info("");
		log.info("┌─ [SUPPLIER] Step 4 ─ InventoryResponse received → ShipmentRequest");
		log.info("│  WorkflowId : {}", workflowId);
		log.debug("│  InventoryResponse XML:\n{}", inventoryResponseXml);
		String shipmentRequestXml = transform(inventoryResponseXml,
				"/xsl/03-inventory-response-to-shipment-request.xsl",
				Map.of("newId", "SHIPREQ-" + shortUuid(),
						"currentDate", LocalDate.now().toString(),
						"requestedDeliveryDate", LocalDate.now().plusDays(7).toString()));
		write(workflowId, "04-ShipmentRequest.xml", shipmentRequestXml);
		getServiceStub("logistics", ILogisticsService.class)
				.onShipmentRequest(convertToDataHandler(shipmentRequestXml), workflowId);
		log.info("└─────────────────────────────────────────────────────────────");
		log.debug("\n{}", shipmentRequestXml);
	}

	// ── Step 6: Supplier receives ShipmentNotification → Invoice ─────────────
	private void onShipmentNotification(String shipmentNotificationXml, String workflowId)
			throws Exception {
		log.info("");
		log.info("┌─ [SUPPLIER] Step 6 ─ ShipmentNotification received → Invoice ─");
		log.info("│  WorkflowId : {}", workflowId);
		log.debug("│  ShipmentNotification XML:\n{}", shipmentNotificationXml);
		LocalDate invoiceDate = LocalDate.now();
		String invoiceXml = transform(shipmentNotificationXml,
				"/xsl/05-shipment-notification-to-invoice.xsl",
				Map.of("newId", "INV-" + shortUuid(),
						"invoiceDate", invoiceDate.toString(),
						"dueDate", invoiceDate.plusDays(30).toString()));
		write(workflowId, "06-Invoice.xml", invoiceXml);
		getServiceStub("buyer", IBuyerService.class)
			.onInvoice(convertToDataHandler(invoiceXml), workflowId);
		log.info("└─────────────────────────────────────────────────────────────");
		log.debug("\n{}", invoiceXml);
	}

	// ── Step 9: Supplier receives PaymentConfirmation ─────────────────────────
	private void onPaymentConfirmation(String confirmationXml, String workflowId) {
		log.info("");
		log.info("┌─ [SUPPLIER] Step 9 ─ PaymentConfirmation received ──────────");
		log.info("│  WorkflowId : {}", workflowId);
		log.info("│  ✓ Payment received — Order-to-Cash flow COMPLETE for supplier");
		log.info("└─────────────────────────────────────────────────────────────");
		log.debug("\n{}", confirmationXml);
	}
}
