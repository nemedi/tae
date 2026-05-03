package com.example.order2cash.akka.actors;

import static com.example.order2cash.akka.util.IdGenerator.shortUuid;
import static com.example.order2cash.akka.util.XmlOutputWriter.write;
import static com.example.order2cash.akka.util.XsltTransformer.transform;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import com.example.order2cash.akka.messages.InventoryRequest;
import com.example.order2cash.akka.messages.InventoryResponse;
import com.example.order2cash.akka.messages.Invoice;
import com.example.order2cash.akka.messages.PaymentConfirmation;
import com.example.order2cash.akka.messages.PurchaseOrder;
import com.example.order2cash.akka.messages.ShipmentNotification;
import com.example.order2cash.akka.messages.ShipmentRequest;
import com.example.order2cash.akka.remote.RemoteAddresses;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class SupplierActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    
    private ActorSelection inventoryRef;
    private ActorSelection logisticsRef;
    private ActorSelection buyerRef;

    public static Props props() {
        return Props.create(SupplierActor.class);
    }

    @Override
    public void preStart() {
        inventoryRef = RemoteAddresses.inventory(getContext());
        logisticsRef = RemoteAddresses.logistics(getContext());
        buyerRef     = RemoteAddresses.buyer(getContext());
        log.info("[Supplier] Started.");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PurchaseOrder.class,
                		message -> onPurchaseOrder(message.xml, message.workflowId))
                .match(InventoryResponse.class,
                		message -> onInventoryResponse(message.xml, message.workflowId))
                .match(ShipmentNotification.class,
                		message -> onShipmentNotification(message.xml, message.workflowId))
                .match(PaymentConfirmation.class,
                		message -> onPaymentConfirmation(message.xml, message.workflowId))
                .build();
    }
    
    // ── Step 2: Supplier receives PurchaseOrder → InventoryRequest ────────────
    private void onPurchaseOrder(String purchaseOrderXml, String workflowId)
    		throws IOException {
        log.info("");
        log.info("┌─ [SUPPLIER] Step 2 ─ PurchaseOrder received → InventoryRequest");
        log.info("│  WorkflowId : {}", workflowId);
        log.debug("│  PurchaseOrder XML:\n{}", purchaseOrderXml);
        String inventoryRequestXml = transform(purchaseOrderXml,
            "/xsl/01-purchase-order-to-inventory-request.xsl",
            Map.of("newId",       "INVREQ-" + shortUuid(),
                   "currentDate", LocalDate.now().toString()));
        write(workflowId, "02-InventoryRequest.xml", inventoryRequestXml);
        inventoryRef.tell(new InventoryRequest(workflowId, inventoryRequestXml), getSelf());
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", inventoryRequestXml);
    }

    // ── Step 4: Supplier receives InventoryResponse → ShipmentRequest ─────────
    private void onInventoryResponse(String inventoryResponseXml, String workflowId)
    		throws IOException {
        log.info("");
        log.info("┌─ [SUPPLIER] Step 4 ─ InventoryResponse received → ShipmentRequest");
        log.info("│  WorkflowId : {}", workflowId);
        log.debug("│  InventoryResponse XML:\n{}", inventoryResponseXml);
        String shipmentRequestXml = transform(inventoryResponseXml,
            "/xsl/03-inventory-response-to-shipment-request.xsl",
            Map.of("newId",                 "SHIPREQ-" + shortUuid(),
                   "currentDate",           LocalDate.now().toString(),
                   "requestedDeliveryDate", LocalDate.now().plusDays(7).toString()));
        write(workflowId, "04-ShipmentRequest.xml", shipmentRequestXml);
        logisticsRef.tell(new ShipmentRequest(workflowId, shipmentRequestXml), getSelf());
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", shipmentRequestXml);
    }

    // ── Step 6: Supplier receives ShipmentNotification → Invoice ─────────────
    private void onShipmentNotification(String shipmentNotificationXml, String workflowId)
    		throws IOException {
        log.info("");
        log.info("┌─ [SUPPLIER] Step 6 ─ ShipmentNotification received → Invoice ─");
        log.info("│  WorkflowId : {}", workflowId);
        log.debug("│  ShipmentNotification XML:\n{}", shipmentNotificationXml);
        LocalDate invoiceDate = LocalDate.now();
        String invoiceXml = transform(shipmentNotificationXml,
            "/xsl/05-shipment-notification-to-invoice.xsl",
            Map.of("newId",       "INV-" + shortUuid(),
                   "invoiceDate", invoiceDate.toString(),
                   "dueDate",     invoiceDate.plusDays(30).toString()));
        write(workflowId, "06-Invoice.xml", invoiceXml);
        buyerRef.tell(new Invoice(workflowId, invoiceXml), getSelf());
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
