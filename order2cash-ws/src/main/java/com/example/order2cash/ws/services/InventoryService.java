package com.example.order2cash.ws.services;

import static com.example.order2cash.ws.util.XmlOutputWriter.write;
import static com.example.order2cash.ws.util.XsltTransformer.transform;

import java.time.LocalDate;
import java.util.Map;

import jakarta.activation.DataHandler;
import jakarta.jws.WebService;
import jakarta.xml.ws.soap.MTOM;

@WebService(targetNamespace = "http://example.com", serviceName = "inventory",
	endpointInterface = "com.example.order2cash.ws.services.IInventoryService")
@MTOM
public class InventoryService extends AbstractService implements IInventoryService {

	public InventoryService(Map<String, String> endpoints) {
		super(endpoints);
	}

	@Override
	public void onInventoryRequest(DataHandler inventoryRequestHandler, String workflowId) {
		try {
			onInventoryRequest(convertToString(inventoryRequestHandler), workflowId);
		} catch (Exception e) {
			log.error("[INVENTORY] Error processing InventoryRequest: {}", e.getMessage());
		}
	}

// ── Step 3: Inventory receives InventoryRequest → InventoryResponse ───────
	private void onInventoryRequest(String inventoryRequestXml, String workflowId)
			throws Exception {
		log.info("");
		log.info("┌─ [INVENTORY] Step 3 ─ InventoryRequest → InventoryResponse ──");
		log.info("│  WorkflowId : {}", workflowId);
		log.debug("│  InventoryRequest XML:\n{}", inventoryRequestXml);
		String inventoryResponseXml = transform(inventoryRequestXml,
				"/xsl/02-inventory-request-to-inventory-response.xsl",
				Map.of("newId", "INVRESP-" + shortUuid(),
						"currentDate", LocalDate.now().toString()));
		write(workflowId, "03-InventoryResponse.xml", inventoryResponseXml);
		getServiceStub("supplier", ISupplierService.class)
				.onInventoryResponse(convertToDataHandler(inventoryResponseXml), workflowId);
		log.info("│  Status : AVAILABLE");
		log.info("└─────────────────────────────────────────────────────────────");
		log.debug("\n{}", inventoryResponseXml);
	}
}
