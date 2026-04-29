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
@Profile("inventory")
public class InventoryService extends AbstractService {

    public InventoryService(KafkaTemplate<String, String> kafka, XsltTransformer xslt, XmlOutputWriter writer) {
    	super(kafka, xslt, writer);
    }

    // ── Step 3: Inventory receives InventoryRequest → InventoryResponse ───────
    @KafkaListener(topics = KafkaTopics.INVENTORY_REQUESTS, groupId = "inventory-group")
    public void onInventoryRequest(String inventoryRequestXml, @Header("workflow-id") String workflowId) {
        log.info("");
        log.info("┌─ [INVENTORY] Step 3 ─ InventoryRequest → InventoryResponse ──");
        log.info("│  WorkflowId : {}", workflowId);
        log.debug("│  InventoryRequest XML:\n{}", inventoryRequestXml);
        String response = xslt.transform(inventoryRequestXml,
            "/xslt/02-inventory-request-to-inventory-response.xslt",
            Map.of("newId",       "INVRESP-" + shortUuid(),
                   "currentDate", LocalDate.now().toString()));
        writer.write(workflowId, "03-InventoryResponse.xml", response);
        kafka.send(record(KafkaTopics.INVENTORY_RESPONSES, workflowId, response));
        log.info("│  Status     : AVAILABLE");
        log.info("│  Topic      : {}", KafkaTopics.INVENTORY_RESPONSES);
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", response);
    }

}
