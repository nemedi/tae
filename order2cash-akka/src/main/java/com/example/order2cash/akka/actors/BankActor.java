package com.example.order2cash.akka.actors;

import static com.example.order2cash.akka.util.IdGenerator.shortUuid;
import static com.example.order2cash.akka.util.XmlOutputWriter.write;
import static com.example.order2cash.akka.util.XsltTransformer.transform;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import com.example.order2cash.akka.messages.PaymentConfirmation;
import com.example.order2cash.akka.messages.PaymentInstruction;
import com.example.order2cash.akka.remote.RemoteAddresses;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class BankActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private ActorSelection supplierRef;
    private ActorSelection buyerRef;

    public static Props props() {
        return Props.create(BankActor.class);
    }

    @Override
    public void preStart() {
        supplierRef = RemoteAddresses.supplier(getContext());
        buyerRef = RemoteAddresses.buyer(getContext());
        log.info("[Bank] Started.");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PaymentInstruction.class,
                		message -> onPaymentInstruction(message.xml, message.workflowId))
                .build();
    }
    
    // ── Step 8: Bank receives PaymentInstruction → PaymentConfirmation ────────
    private void onPaymentInstruction(String paymentInstructionXml, String workflowId)
    		throws IOException {
        log.info("");
        log.info("┌─ [BANK] Step 8 ─ PaymentInstruction → PaymentConfirmation ──");
        log.info("│  WorkflowId : {}", workflowId);
        log.debug("│  PaymentInstruction XML:\n{}", paymentInstructionXml);
        String transactionId = "TXN-" + shortUuid();
        String confirmationXml = transform(paymentInstructionXml,
            "/xsl/07-payment-instruction-to-payment-confirmation.xsl",
            Map.of("newId",            "PAYCONF-" + shortUuid(),
                   "transactionId",    transactionId,
                   "confirmationDate", LocalDate.now().toString()));
        write(workflowId, "08-PaymentConfirmation.xml", confirmationXml);
        PaymentConfirmation paymentConfirmation = new PaymentConfirmation(workflowId, confirmationXml);
		supplierRef.tell(paymentConfirmation, getSelf());
		buyerRef.tell(paymentConfirmation, getSelf());
        log.info("│  TxnId      : {}", transactionId);
        log.info("│  Status     : COMPLETED");
        log.info("└─────────────────────────────────────────────────────────────");
        log.debug("\n{}", confirmationXml);
    }
}
