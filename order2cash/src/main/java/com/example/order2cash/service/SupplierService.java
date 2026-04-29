package com.example.order2cash.service;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.example.order2cash.config.KafkaTopics;
import com.example.order2cash.util.XmlOutputWriter;
import com.example.order2cash.util.XsltTransformer;

@Service
@Profile("supplier")
public class SupplierService extends AbstractService {

    public SupplierService(KafkaTemplate<String, String> kafka, XsltTransformer xslt, XmlOutputWriter writer) {
    	super(kafka, xslt, writer);
    }

    // ── Step 2: Supplier receives PurchaseOrder → InventoryRequest ────────────
    @KafkaListener(topics = KafkaTopics.PURCHASE_ORDERS, groupId = "supplier-group")
    public void onPurchaseOrder(String purchaseOrderXml, @Header("workflow-id") String workflowId) {
        log.info("");
        log.info("┌─ [SUPPLIER] Step 2 ─ PurchaseOrder received → InventoryRequest");
        log.info("│  WorkflowId : {}", workflowId);
        log.debug("│  PurchaseOrder XML:\n{}", purchaseOrderXml);
        String inventoryRequest = xslt.transform(purchaseOrderXml,
            "/xslt/01-purchase-order-to-inventory-request.xslt",
            Map.of("newId",       "INVREQ-" + shortUuid(),
                   "currentDate", LocalDate.now().toString()));
        writer.write(workflowId, "02-InventoryRequest.xml", inventoryRequest);
        kafka.send(record(KafkaTopics.INVENTORY_REQUESTS, workflowId, inventoryRequest));
        log.info("│  Topic      : {}", KafkaTopics.INVENTORY_REQUESTS);
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", inventoryRequest);
    }

    // ── Step 4: Supplier receives InventoryResponse → ShipmentRequest ─────────
    @KafkaListener(topics = KafkaTopics.INVENTORY_RESPONSES, groupId = "supplier-group")
    public void onInventoryResponse(String inventoryResponseXml, @Header("workflow-id") String workflowId) {
        log.info("");
        log.info("┌─ [SUPPLIER] Step 4 ─ InventoryResponse received → ShipmentRequest");
        log.info("│  WorkflowId : {}", workflowId);
        log.debug("│  InventoryResponse XML:\n{}", inventoryResponseXml);
        String shipmentRequest = xslt.transform(inventoryResponseXml,
            "/xslt/03-inventory-response-to-shipment-request.xslt",
            Map.of("newId",                 "SHIPREQ-" + shortUuid(),
                   "currentDate",           LocalDate.now().toString(),
                   "requestedDeliveryDate", LocalDate.now().plusDays(7).toString()));
        writer.write(workflowId, "04-ShipmentRequest.xml", shipmentRequest);
        kafka.send(record(KafkaTopics.SHIPMENT_REQUESTS, workflowId, shipmentRequest));
        log.info("│  Topic      : {}", KafkaTopics.SHIPMENT_REQUESTS);
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", shipmentRequest);
    }

    // ── Step 6: Supplier receives ShipmentNotification → Invoice ─────────────
    @KafkaListener(topics = KafkaTopics.SHIPMENT_NOTIFICATIONS, groupId = "supplier-group")
    public void onShipmentNotification(String shipmentNotificationXml, @Header("workflow-id") String workflowId) {
        log.info("");
        log.info("┌─ [SUPPLIER] Step 6 ─ ShipmentNotification received → Invoice ─");
        log.info("│  WorkflowId : {}", workflowId);
        log.debug("│  ShipmentNotification XML:\n{}", shipmentNotificationXml);
        LocalDate invoiceDate = LocalDate.now();
        String invoice = xslt.transform(shipmentNotificationXml,
            "/xslt/05-shipment-notification-to-invoice.xslt",
            Map.of("newId",       "INV-" + shortUuid(),
                   "invoiceDate", invoiceDate.toString(),
                   "dueDate",     invoiceDate.plusDays(30).toString()));
        writer.write(workflowId, "06-Invoice.xml", invoice);
        kafka.send(record(KafkaTopics.INVOICES, workflowId, invoice));
        log.info("│  Topic      : {}", KafkaTopics.INVOICES);
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", invoice);
    }

    // ── Step 9: Supplier receives PaymentConfirmation ─────────────────────────
    @KafkaListener(topics = KafkaTopics.PAYMENT_CONFIRMATIONS, groupId = "supplier-payment-group")
    public void onPaymentConfirmation(String confirmationXml, @Header("workflow-id") String workflowId) {
        log.info("");
        log.info("┌─ [SUPPLIER] Step 9 ─ PaymentConfirmation received ──────────");
        log.info("│  WorkflowId : {}", workflowId);
        log.info("│  ✓ Payment received — Order-to-Cash flow COMPLETE for supplier");
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", confirmationXml);
    }

}
