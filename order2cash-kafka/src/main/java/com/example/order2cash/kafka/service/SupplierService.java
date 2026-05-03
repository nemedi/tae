package com.example.order2cash.kafka.service;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.example.order2cash.kafka.config.KafkaTopics;
import com.example.order2cash.kafka.util.XmlOutputWriter;
import com.example.order2cash.kafka.util.XsltTransformer;

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
        String inventoryRequestXml = xslt.transform(purchaseOrderXml,
            "/xsl/01-purchase-order-to-inventory-request.xsl",
            Map.of("newId",       "INVREQ-" + shortUuid(),
                   "currentDate", LocalDate.now().toString()));
        writer.write(workflowId, "02-InventoryRequest.xml", inventoryRequestXml);
        kafka.send(record(KafkaTopics.INVENTORY_REQUESTS, workflowId, inventoryRequestXml));
        log.info("│  Topic      : {}", KafkaTopics.INVENTORY_REQUESTS);
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", inventoryRequestXml);
    }

    // ── Step 4: Supplier receives InventoryResponse → ShipmentRequest ─────────
    @KafkaListener(topics = KafkaTopics.INVENTORY_RESPONSES, groupId = "supplier-group")
    public void onInventoryResponse(String inventoryResponseXml, @Header("workflow-id") String workflowId) {
        log.info("");
        log.info("┌─ [SUPPLIER] Step 4 ─ InventoryResponse received → ShipmentRequest");
        log.info("│  WorkflowId : {}", workflowId);
        log.debug("│  InventoryResponse XML:\n{}", inventoryResponseXml);
        String shipmentRequestXml = xslt.transform(inventoryResponseXml,
            "/xsl/03-inventory-response-to-shipment-request.xsl",
            Map.of("newId",                 "SHIPREQ-" + shortUuid(),
                   "currentDate",           LocalDate.now().toString(),
                   "requestedDeliveryDate", LocalDate.now().plusDays(7).toString()));
        writer.write(workflowId, "04-ShipmentRequest.xml", shipmentRequestXml);
        kafka.send(record(KafkaTopics.SHIPMENT_REQUESTS, workflowId, shipmentRequestXml));
        log.info("│  Topic      : {}", KafkaTopics.SHIPMENT_REQUESTS);
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", shipmentRequestXml);
    }

    // ── Step 6: Supplier receives ShipmentNotification → Invoice ─────────────
    @KafkaListener(topics = KafkaTopics.SHIPMENT_NOTIFICATIONS, groupId = "supplier-group")
    public void onShipmentNotification(String shipmentNotificationXml, @Header("workflow-id") String workflowId) {
        log.info("");
        log.info("┌─ [SUPPLIER] Step 6 ─ ShipmentNotification received → Invoice ─");
        log.info("│  WorkflowId : {}", workflowId);
        log.debug("│  ShipmentNotification XML:\n{}", shipmentNotificationXml);
        LocalDate invoiceDate = LocalDate.now();
        String invoiceXml = xslt.transform(shipmentNotificationXml,
            "/xsl/05-shipment-notification-to-invoice.xsl",
            Map.of("newId",       "INV-" + shortUuid(),
                   "invoiceDate", invoiceDate.toString(),
                   "dueDate",     invoiceDate.plusDays(30).toString()));
        writer.write(workflowId, "06-Invoice.xml", invoiceXml);
        kafka.send(record(KafkaTopics.INVOICES, workflowId, invoiceXml));
        log.info("│  Topic      : {}", KafkaTopics.INVOICES);
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", invoiceXml);
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
