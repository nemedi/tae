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
@Profile("logistics")
public class LogisticsService extends AbstractService {

    public LogisticsService(KafkaTemplate<String, String> kafka, XsltTransformer xslt, XmlOutputWriter writer) {
    	super(kafka, xslt, writer);
    }

    // ── Step 5: Logistics receives ShipmentRequest → ShipmentNotification ─────
    @KafkaListener(topics = KafkaTopics.SHIPMENT_REQUESTS, groupId = "logistics-group")
    public void onShipmentRequest(String shipmentRequestXml, @Header("workflow-id") String workflowId) {
        log.info("");
        log.info("┌─ [LOGISTICS] Step 5 ─ ShipmentRequest → ShipmentNotification ─");
        log.info("│  WorkflowId : {}", workflowId);
        log.debug("│  ShipmentRequest XML:\n{}", shipmentRequestXml);
        String trackingNumber = "TRACK-" + shortUuid();
        LocalDate shipDate = LocalDate.now();
        String shipmentNotificationXml = xslt.transform(shipmentRequestXml,
            "/xsl/04-shipment-request-to-shipment-notification.xsl",
            Map.of("newId",                 "SHIPNOTIF-" + shortUuid(),
                   "shipmentDate",          shipDate.toString(),
                   "estimatedDeliveryDate", shipDate.plusDays(6).toString(),
                   "trackingNumber",        trackingNumber));
        writer.write(workflowId, "05-ShipmentNotification.xml", shipmentNotificationXml);
        kafka.send(record(KafkaTopics.SHIPMENT_NOTIFICATIONS, workflowId, shipmentNotificationXml));
        log.info("│  Tracking   : {}", trackingNumber);
        log.info("│  Topic      : {}", KafkaTopics.SHIPMENT_NOTIFICATIONS);
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", shipmentNotificationXml);
    }
    
}
