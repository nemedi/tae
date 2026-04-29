package com.example.order2cash.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.example.order2cash.config.KafkaTopics;
import com.example.order2cash.util.XmlOutputWriter;
import com.example.order2cash.util.XsltTransformer;

import jakarta.annotation.PostConstruct;

@Service
@Profile("buyer")
public class BuyerService extends AbstractService {

    @Value("${app.data-path}")
    private String dataPath;

    public BuyerService(KafkaTemplate<String, String> kafka, XsltTransformer xslt, XmlOutputWriter writer) {
    	super(kafka, xslt, writer);
    }

    // ── Step 1: Poll data/input every 2 s for new XML files ──────────────────
    @PostConstruct
    public void startInputWatcher() throws IOException {
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
                    publishPurchaseOrder(file);
                }
            }
        } catch (IOException e) {
            log.error("[BUYER] Error scanning input directory: {}", e.getMessage());
        }
    }

    private void publishPurchaseOrder(Path filePath) {
        String po;
        try {
            po = Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("[BUYER] Cannot read {}: {}", filePath, e.getMessage());
            return;
        }
        String workflowId = UUID.randomUUID().toString();
        for (int attempt = 1; attempt <= 5; attempt++) {
            try {
                kafka.send(record(KafkaTopics.PURCHASE_ORDERS, workflowId, po)).get(10, TimeUnit.SECONDS);
                writer.write(workflowId, "01-PurchaseOrder.xml", po);
                Files.deleteIfExists(filePath);
                log.info("");
                log.info("┌─ [BUYER] Step 1 ─ PurchaseOrder published ──────────────────");
                log.info("│  Source     : {}", filePath);
                log.info("│  WorkflowId : {}", workflowId);
                log.info("│  Topic      : {}", KafkaTopics.PURCHASE_ORDERS);
                log.info("└─────────────────────────────────────────────────────────────");
                log.debug("\n{}", po);
                return;
            } catch (Exception e) {
                log.warn("[BUYER] Publish attempt {}/5 failed: {}", attempt, e.getMessage());
                try { Thread.sleep(2_000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); return; }
            }
        }
        log.error("[BUYER] Failed to publish {} after 5 attempts", filePath.getFileName());
    }

    // ── Step 7: Buyer receives Invoice → transforms to PaymentInstruction ─────
    @KafkaListener(topics = KafkaTopics.INVOICES, groupId = "buyer-group")
    public void onInvoice(String invoiceXml, @Header("workflow-id") String workflowId) {
        log.info("");
        log.info("┌─ [BUYER] Step 7 ─ Invoice received → PaymentInstruction ───────");
        log.info("│  WorkflowId : {}", workflowId);
        log.debug("│  Invoice XML:\n{}", invoiceXml);
        String paymentInstruction = xslt.transform(invoiceXml,
            "/xslt/06-invoice-to-payment-instruction.xslt",
            Map.of("newId",       "PAYINST-" + shortUuid(),
                   "currentDate", LocalDate.now().toString()));
        writer.write(workflowId, "07-PaymentInstruction.xml", paymentInstruction);
        kafka.send(record(KafkaTopics.PAYMENT_INSTRUCTIONS, workflowId, paymentInstruction));
        log.info("│  Topic      : {}", KafkaTopics.PAYMENT_INSTRUCTIONS);
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", paymentInstruction);
    }

    // ── Step 9: Buyer receives PaymentConfirmation ────────────────────────────
    @KafkaListener(topics = KafkaTopics.PAYMENT_CONFIRMATIONS, groupId = "buyer-confirmation-group")
    public void onPaymentConfirmation(String confirmationXml, @Header("workflow-id") String workflowId) {
        log.info("");
        log.info("┌─ [BUYER] Step 9 ─ PaymentConfirmation received ─────────────");
        log.info("│  WorkflowId : {}", workflowId);
        log.info("│  ✓ Order-to-Cash flow COMPLETE for buyer");
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", confirmationXml);
    }

}
