package supplier;

import java.util.List;

import jakarta.activation.DataHandler;
import jakarta.jws.Oneway;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService
public interface SupplierContract {

	String NAME = "supplierService";
	String NAMESPACE = "http://axway.com/" + NAME;
	
	@WebMethod
	default String getServiceName() {
		return NAME;
	}
	
	@WebMethod
	List<String> getDocumentsOfSupplierFromBuyer(String supplier, String buyer);
	
	@WebMethod
	List<String> getDocumentsOfSupplierFromLogistics(String supplier, String logistics);
	
	@WebMethod
	List<String> getDocumentsOfSupplierFromBank(String supplier, String bank);
	
	@WebMethod
	DataHandler getDocumentOfSupplierFromBuyer(String supplier, String buyer, String document);
	
	@WebMethod
	DataHandler getDocumentOfSupplierFromLogistics(String supplier, String logistics, String document);
	
	@WebMethod
	DataHandler getDocumentOfSupplierFromBank(String supplier , String bank, String document);
	
	@WebMethod
	@Oneway
	void sendPurchaseOrderAcknowledgementToBuyer(String supplier,
			String buyer,
			String purchaseOrderId);
	
	@WebMethod
	@Oneway
	void sendInvoiceToBuyer(String supplier,
			String buyer,
			String purchaseOrderId);
}
