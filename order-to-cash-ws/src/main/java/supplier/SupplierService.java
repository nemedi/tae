package supplier;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import common.Inbox;
import jakarta.activation.DataHandler;
import jakarta.jws.WebService;
import jakarta.xml.ws.soap.Addressing;
import jakarta.xml.ws.soap.MTOM;

import static common.Message.dataHandler;

@WebService(targetNamespace = SupplierContract.NAMESPACE,
	serviceName = SupplierContract.NAME,
	endpointInterface = "supplier.SupplierContract")
@MTOM
@Addressing(enabled = true, required = false)
public class SupplierService implements SupplierContract {

	@Override
	public List<String> getDocumentsOfSupplierFromBuyer(String supplier, String buyer) {
		return Arrays.stream(Inbox.ofSupplier(supplier)
				.fromBuyer(buyer)
				.listFiles(f -> f.isFile()))
				.map(f -> f.getName())
				.collect(Collectors.toList());
	}

	@Override
	public List<String> getDocumentsOfSupplierFromLogistics(String supplier, String logistics) {
		return Arrays.stream(Inbox.ofSupplier(supplier)
				.fromLogistics(logistics)
				.listFiles(f -> f.isFile()))
				.map(f -> f.getName())
				.collect(Collectors.toList());
	}

	@Override
	public List<String> getDocumentsOfSupplierFromBank(String supplier, String bank) {
		return Arrays.stream(Inbox.ofSupplier(supplier)
				.fromBank(bank)
				.listFiles(f -> f.isFile()))
				.map(f -> f.getName())
				.collect(Collectors.toList());
	}

	@Override
	public DataHandler getDocumentOfSupplierFromBuyer(String supplier, String buyer, String document) {
		return dataHandler(Arrays.stream(Inbox.ofSupplier(supplier)
				.fromBuyer(buyer)
				.listFiles(f -> f.isFile() && document.equals(f.getName())))
				.findAny().get());	
	}

	@Override
	public DataHandler getDocumentOfSupplierFromLogistics(String supplier, String logistics, String document) {
		return dataHandler(Arrays.stream(Inbox.ofSupplier(supplier)
				.fromLogistics(logistics)
				.listFiles(f -> f.isFile() && document.equals(f.getName())))
				.findAny().get());
	}

	@Override
	public DataHandler getDocumentOfSupplierFromBank(String supplier, String bank, String document) {
		return dataHandler(Arrays.stream(Inbox.ofSupplier(supplier)
				.fromBank(bank)
				.listFiles(f -> f.isFile() && document.equals(f.getName())))
				.findAny().get());
	}

	@Override
	public void sendPurchaseOrderAcknowledgementToBuyer(String supplier, String buyer, String purchaseOrderId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendInvoiceToBuyer(String supplier, String buyer, String purchaseOrderId) {
		// TODO Auto-generated method stub

	}

}
