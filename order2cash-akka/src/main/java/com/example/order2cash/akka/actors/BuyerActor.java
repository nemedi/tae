package com.example.order2cash.akka.actors;

import static com.example.order2cash.akka.util.IdGenerator.shortUuid;
import static com.example.order2cash.akka.util.XmlOutputWriter.write;
import static com.example.order2cash.akka.util.XsltTransformer.transform;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.example.order2cash.akka.messages.Invoice;
import com.example.order2cash.akka.messages.PaymentConfirmation;
import com.example.order2cash.akka.messages.PaymentInstruction;
import com.example.order2cash.akka.messages.PurchaseOrder;
import com.example.order2cash.akka.messages.PurchaseOrderFileDetected;
import com.example.order2cash.akka.remote.RemoteAddresses;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class BuyerActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    
    private final String dataPath = System.getenv().getOrDefault("DATA_PATH", "data");

    private ActorSelection supplierRef;
    private ActorSelection bankRef;

    public static Props props() {
        return Props.create(BuyerActor.class);
    }

    @Override
    public void preStart() throws IOException {
        supplierRef = RemoteAddresses.supplier(getContext());
        bankRef     = RemoteAddresses.bank(getContext());

        Path inputDir = Path.of(dataPath, "input");
        Files.createDirectories(inputDir);
        var scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "input-watcher");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleWithFixedDelay(() -> scanInputDirectory(inputDir), 2, 2, TimeUnit.SECONDS);
        log.info("");
        log.info("╔══════════════════════════════════════════════════════════╗");
        log.info("║  ORDER-TO-CASH  —  watching for PurchaseOrder XML files  ║");
        log.info("║  Drop any .xml file into:  {}/input  ║", dataPath);
        log.info("╚══════════════════════════════════════════════════════════╝");
    }
    
    private void scanInputDirectory(Path inputDir) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(inputDir, "*.xml")) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                	String purchaseOrderXml = Files.readString(file, StandardCharsets.UTF_8);
                	getSelf().tell(new PurchaseOrderFileDetected(purchaseOrderXml, file.toFile().getAbsolutePath()), getSelf());
                    Files.deleteIfExists(file);
                }
            }
        } catch (IOException e) {
            log.error("[BUYER] Error scanning input directory: {}", e.getMessage());
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PurchaseOrderFileDetected.class,
                		message -> onPurchaseOrderFileDetected(message.xml, message.workflowId, message.filePath))
                .match(Invoice.class,
                		message -> onInvoice(message.xml, message.workflowId))
                .match(PaymentConfirmation.class,
                		message -> onPaymentConfirmation(message.xml, message.workflowId))
                .build();
    }
    
    private void onPurchaseOrderFileDetected(String purchaseOrderXml, String workflowId, String filePath)
    		throws IOException {
        write(workflowId, "01-PurchaseOrder.xml", purchaseOrderXml);
        supplierRef.tell(new PurchaseOrder(workflowId, purchaseOrderXml), getSelf());
        log.info("");
        log.info("┌─ [BUYER] Step 1 ─ PurchaseOrder published ──────────────────");
        log.info("│  Source     : {}", filePath);
        log.info("│  WorkflowId : {}", workflowId);
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", purchaseOrderXml);
    }
    
    // ── Step 7: Buyer receives Invoice → transforms to PaymentInstruction ─────
    private void onInvoice(String invoiceXml, String workflowId)
    		throws IOException {
        log.info("");
        log.info("┌─ [BUYER] Step 7 ─ Invoice received → PaymentInstruction ───────");
        log.info("│  WorkflowId : {}", workflowId);
        log.debug("│  Invoice XML:\n{}", invoiceXml);
        String paymentInstructionXml = transform(invoiceXml,
            "/xsl/06-invoice-to-payment-instruction.xsl",
            Map.of("newId",       "PAYINST-" + shortUuid(),
                   "currentDate", LocalDate.now().toString()));
        write(workflowId, "07-PaymentInstruction.xml", paymentInstructionXml);
        bankRef.tell(new PaymentInstruction(workflowId, paymentInstructionXml), getSelf());
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", paymentInstructionXml);
    }

    // ── Step 9: Buyer receives PaymentConfirmation ────────────────────────────
    private void onPaymentConfirmation(String confirmationXml, String workflowId) {
        log.info("");
        log.info("┌─ [BUYER] Step 9 ─ PaymentConfirmation received ─────────────");
        log.info("│  WorkflowId : {}", workflowId);
        log.info("│  ✓ Order-to-Cash flow COMPLETE for buyer");
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", confirmationXml);
    }


}
