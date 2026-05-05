package com.example.order2cash.ws.services;

import static com.example.order2cash.ws.util.XmlOutputWriter.write;
import static com.example.order2cash.ws.util.XsltTransformer.transform;

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

import jakarta.activation.DataHandler;
import jakarta.jws.WebService;
import jakarta.xml.ws.soap.MTOM;

@WebService(targetNamespace = "http://example.com", serviceName = "buyer",
	endpointInterface = "com.example.order2cash.ws.services.IBuyerService")
@MTOM
public class BuyerService extends AbstractService implements IBuyerService {
	
	private final String dataPath = System.getenv().getOrDefault("DATA_PATH", "data");

	public BuyerService(Map<String, String> endpoints) throws IOException {
		super(endpoints);
		Path inputDir = Path.of(dataPath, "input");
		Files.createDirectories(inputDir);
		var scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread t = new Thread(r, "input-watcher");
			t.setDaemon(true);
			return t;
		});
		scheduler.scheduleWithFixedDelay(() -> {
			try {
				scanInputDirectory(inputDir);
			} catch (Exception e) {
				log.error("[BUYER] Error scanning input directory: {}", e.getMessage());
			}
		}, 2, 2, TimeUnit.SECONDS);
		log.info("");
		log.info("╔══════════════════════════════════════════════════════════╗");
		log.info("║  ORDER-TO-CASH  —  watching for PurchaseOrder XML files  ║");
		log.info("║  Drop any .xml file into:  {}/input  ║", dataPath);
		log.info("╚══════════════════════════════════════════════════════════╝");
	}
	
	private void scanInputDirectory(Path inputDir) throws Exception {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(inputDir, "*.xml")) {
			for (Path file : stream) {
				try {
					if (Files.isRegularFile(file)) {
						String purchaseOrderXml = Files.readString(file, StandardCharsets.UTF_8);
						onPurchaseOrderFileDetected(purchaseOrderXml,
								UUID.randomUUID().toString(), file.toFile().getAbsolutePath());
					}
				} catch (Exception e) {
					log.error("[BUYER] Error processing file {}: {}", file.toFile().getAbsolutePath(), e.getMessage());
				} finally {
					Files.deleteIfExists(file);
				}
			}
		} catch (IOException e) {
			log.error("[BUYER] Error scanning input directory: {}", e.getMessage());
		}
	}

	@Override
	public void onInvoice(DataHandler invoiceHandler, String workflowId) {
		try {
			onInvoice(convertToString(invoiceHandler), workflowId);
		} catch (Exception e) {
			log.error("[BUYER] Error processing Invoice: {}", e.getMessage());
		}
	}

	@Override
	public void onPaymentConfirmation(DataHandler paymentConfirmationHandler, String workflowId) {
		try {
			onPaymentConfirmation(convertToString(paymentConfirmationHandler), workflowId);
		} catch (IOException e) {
			log.error("[BUYER] Error processing PaymentConfirmation: {}", e.getMessage());
		}
	}
	
	private void onPurchaseOrderFileDetected(String purchaseOrderXml, String workflowId, String filePath)
			throws Exception {
		write(workflowId, "01-PurchaseOrder.xml", purchaseOrderXml);
		getServiceStub("supplier", ISupplierService.class)
			.onPurchaseOrder(convertToDataHandler(purchaseOrderXml), workflowId);
		log.info("");
		log.info("┌─ [BUYER] Step 1 ─ PurchaseOrder published ──────────────────");
		log.info("│  Source     : {}", filePath);
		log.info("│  WorkflowId : {}", workflowId);
		log.info("└─────────────────────────────────────────────────────────────");
		log.debug("\n{}", purchaseOrderXml);
	}	

	// ── Step 7: Buyer receives Invoice → transforms to PaymentInstruction ─────
	private void onInvoice(String invoiceXml, String workflowId)
			throws Exception {
		log.info("");
		log.info("┌─ [BUYER] Step 7 ─ Invoice received → PaymentInstruction ───────");
		log.info("│  WorkflowId : {}", workflowId);
		log.debug("│  Invoice XML:\n{}", invoiceXml);
		String paymentInstructionXml = transform(invoiceXml,
				"/xsl/06-invoice-to-payment-instruction.xsl",
				Map.of("newId", "PAYINST-" + shortUuid(),
						"currentDate", LocalDate.now().toString()));
		write(workflowId, "07-PaymentInstruction.xml", paymentInstructionXml);
		getServiceStub("bank", IBankService.class)
				.onPaymentInstruction(convertToDataHandler(paymentInstructionXml), workflowId);
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
