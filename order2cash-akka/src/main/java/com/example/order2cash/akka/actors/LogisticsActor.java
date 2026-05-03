package com.example.order2cash.akka.actors;

import static com.example.order2cash.akka.util.IdGenerator.shortUuid;
import static com.example.order2cash.akka.util.XmlOutputWriter.write;
import static com.example.order2cash.akka.util.XsltTransformer.transform;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import com.example.order2cash.akka.messages.ShipmentNotification;
import com.example.order2cash.akka.messages.ShipmentRequest;
import com.example.order2cash.akka.remote.RemoteAddresses;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class LogisticsActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    
    private ActorSelection supplierRef;

    public static Props props() {
        return Props.create(LogisticsActor.class);
    }

    @Override
    public void preStart() {
    	supplierRef = RemoteAddresses.supplier(getContext());
        log.info("[Logistics] Started.");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ShipmentRequest.class,
                		message -> onShipmentRequest(message.xml, message.workflowId))
                .build();
    }
    
    // ── Step 5: Logistics receives ShipmentRequest → ShipmentNotification ─────
    private void onShipmentRequest(String shipmentRequestXml, String workflowId)
    		throws IOException {
        log.info("");
        log.info("┌─ [LOGISTICS] Step 5 ─ ShipmentRequest → ShipmentNotification ─");
        log.info("│  WorkflowId : {}", workflowId);
        log.debug("│  ShipmentRequest XML:\n{}", shipmentRequestXml);
        String trackingNumber = "TRACK-" + shortUuid();
        LocalDate shipDate = LocalDate.now();
        String shipmentNotificationXml = transform(shipmentRequestXml,
            "/xsl/04-shipment-request-to-shipment-notification.xsl",
            Map.of("newId",                 "SHIPNOTIF-" + shortUuid(),
                   "shipmentDate",          shipDate.toString(),
                   "estimatedDeliveryDate", shipDate.plusDays(6).toString(),
                   "trackingNumber",        trackingNumber));
        write(workflowId, "05-ShipmentNotification.xml", shipmentNotificationXml);
        supplierRef.tell(new ShipmentNotification(workflowId, shipmentNotificationXml), getSelf());
        log.info("│  Tracking   : {}", trackingNumber);
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", shipmentNotificationXml);
    }
}
