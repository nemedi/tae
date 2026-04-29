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
@Profile("bank")
public class BankService extends AbstractService {

    public BankService(KafkaTemplate<String, String> kafka, XsltTransformer xslt, XmlOutputWriter writer) {
        super(kafka, xslt, writer);
    }

    // ── Step 8: Bank receives PaymentInstruction → PaymentConfirmation ────────
    @KafkaListener(topics = KafkaTopics.PAYMENT_INSTRUCTIONS, groupId = "bank-group")
    public void onPaymentInstruction(String paymentInstructionXml, @Header("workflow-id") String workflowId) {
        log.info("");
        log.info("┌─ [BANK] Step 8 ─ PaymentInstruction → PaymentConfirmation ──");
        log.info("│  WorkflowId : {}", workflowId);
        log.debug("│  PaymentInstruction XML:\n{}", paymentInstructionXml);
        String transactionId = "TXN-" + shortUuid();
        String confirmation = xslt.transform(paymentInstructionXml,
            "/xslt/07-payment-instruction-to-payment-confirmation.xslt",
            Map.of("newId",            "PAYCONF-" + shortUuid(),
                   "transactionId",    transactionId,
                   "confirmationDate", LocalDate.now().toString()));
        writer.write(workflowId, "08-PaymentConfirmation.xml", confirmation);
        kafka.send(record(KafkaTopics.PAYMENT_CONFIRMATIONS, workflowId, confirmation));
        log.info("│  TxnId      : {}", transactionId);
        log.info("│  Status     : COMPLETED");
        log.info("│  Topic      : {}", KafkaTopics.PAYMENT_CONFIRMATIONS);
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", confirmation);
    }

}
