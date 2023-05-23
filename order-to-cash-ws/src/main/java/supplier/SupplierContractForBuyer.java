package supplier;

import jakarta.activation.DataHandler;
import jakarta.jws.Oneway;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService
public interface SupplierContractForBuyer {
	
	String NAME = "supplierServiceForBuyer";
	String NAMESPACE = "http://axway.com/" + NAME;
	
	@WebMethod
	default String getServiceName() {
		return NAME;
	}

	@WebMethod
	@Oneway
	void receivePurchaseOrder(DataHandler purchaseOrder);
}
