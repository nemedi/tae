package supplier;

import jakarta.activation.DataHandler;
import jakarta.jws.Oneway;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService
public interface SupplierContractForLogistics {
	
	String NAME = "supplierServiceForLogistics";
	String NAMESPACE = "http://axway.com/" + NAME;
	
	@WebMethod
	default String getServiceName() {
		return NAME;
	}

	@WebMethod
	@Oneway
	void receiveLogisticServiceResponse(DataHandler logisticServiceResponse);
	
	@WebMethod
	@Oneway
	void receiveConsolidatorsFreightBill(DataHandler consolidatorsFreightBill);
}
