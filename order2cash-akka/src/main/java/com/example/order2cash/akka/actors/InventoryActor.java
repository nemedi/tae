package com.example.order2cash.akka.actors;

import static com.example.order2cash.akka.util.IdGenerator.shortUuid;
import static com.example.order2cash.akka.util.XmlOutputWriter.write;
import static com.example.order2cash.akka.util.XsltTransformer.transform;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import com.example.order2cash.akka.messages.InventoryRequest;
import com.example.order2cash.akka.messages.InventoryResponse;
import com.example.order2cash.akka.remote.RemoteAddresses;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class InventoryActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    
    private ActorSelection supplierRef;

    public static Props props() {
        return Props.create(InventoryActor.class);
    }

    @Override
    public void preStart() {
    	supplierRef = RemoteAddresses.supplier(getContext());
        log.info("[Inventory] Started.");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(InventoryRequest.class,
                		message -> onInventoryRequest(message.xml, message.workflowId))
                .build();
    }
    
    // ── Step 3: Inventory receives InventoryRequest → InventoryResponse ───────
    private void onInventoryRequest(String inventoryRequestXml, String workflowId)
    		throws IOException {
        log.info("");
        log.info("┌─ [INVENTORY] Step 3 ─ InventoryRequest → InventoryResponse ──");
        log.info("│  WorkflowId : {}", workflowId);
        log.debug("│  InventoryRequest XML:\n{}", inventoryRequestXml);
        String inventoryResponseXml = transform(inventoryRequestXml,
            "/xsl/02-inventory-request-to-inventory-response.xsl",
            Map.of("newId",       "INVRESP-" + shortUuid(),
                   "currentDate", LocalDate.now().toString()));
        write(workflowId, "03-InventoryResponse.xml", inventoryResponseXml);
        supplierRef.tell(new InventoryResponse(workflowId, inventoryResponseXml), getSelf());
        log.info("│  Status     : AVAILABLE");
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", inventoryResponseXml);
    }
}
