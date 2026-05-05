package com.example.order2cash.ws.services;

import static com.example.order2cash.ws.util.XmlOutputWriter.write;
import static com.example.order2cash.ws.util.XsltTransformer.transform;

import java.time.LocalDate;
import java.util.Map;

import jakarta.activation.DataHandler;
import jakarta.jws.WebService;
import jakarta.xml.ws.soap.MTOM;

@WebService(targetNamespace = "http://example.com", serviceName = "bank",
	endpointInterface = "com.example.order2cash.ws.services.IBankService")
@MTOM
public class BankService extends AbstractService implements IBankService {

	public BankService(Map<String, String> endpoints) {
		super(endpoints);
	}

	@Override
	public void onPaymentInstruction(DataHandler paymentInstructionHandler, String workflowId) {
		try {
			onPaymentInstruction(convertToString(paymentInstructionHandler), workflowId);
		} catch (Exception e) {
			log.error("[BANK] Error processing PaymentInstruction: {}", e.getMessage());
		}
	}

	// ── Step 8: Bank receives PaymentInstruction → PaymentConfirmation ────────
	private void onPaymentInstruction(String paymentInstructionXml, String workflowId)
			throws Exception {
		log.info("");
		log.info("┌─ [BANK] Step 8 ─ PaymentInstruction → PaymentConfirmation ──");
		log.info("│  WorkflowId : {}", workflowId);
		log.debug("│  PaymentInstruction XML:\n{}", paymentInstructionXml);
		String transactionId = "TXN-" + shortUuid();
		String confirmationXml = transform(paymentInstructionXml,
				"/xsl/07-payment-instruction-to-payment-confirmation.xsl",
				Map.of("newId", "PAYCONF-" + shortUuid(),
						"transactionId", transactionId,
						"confirmationDate", LocalDate.now().toString()));
		write(workflowId, "08-PaymentConfirmation.xml", confirmationXml);
		getServiceStub("supplier", ISupplierService.class)
				.onPaymentConfirmation(convertToDataHandler(confirmationXml), workflowId);
		getServiceStub("buyer", IBuyerService.class)
				.onPaymentConfirmation(convertToDataHandler(confirmationXml), workflowId);
		log.info("│  TxnId  : {}", transactionId);
		log.info("│  Status : COMPLETED");
		log.info("└─────────────────────────────────────────────────────────────");
		log.debug("\n{}", confirmationXml);
	}
}
