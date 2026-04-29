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
        String notification = xslt.transform(shipmentRequestXml,
            "/xslt/04-shipment-request-to-shipment-notification.xslt",
            Map.of("newId",                 "SHIPNOTIF-" + shortUuid(),
                   "shipmentDate",          shipDate.toString(),
                   "estimatedDeliveryDate", shipDate.plusDays(6).toString(),
                   "trackingNumber",        trackingNumber));
        writer.write(workflowId, "05-ShipmentNotification.xml", notification);
        kafka.send(record(KafkaTopics.SHIPMENT_NOTIFICATIONS, workflowId, notification));
        log.info("│  Tracking   : {}", trackingNumber);
        log.info("│  Topic      : {}", KafkaTopics.SHIPMENT_NOTIFICATIONS);
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", notification);
    }
    
}
