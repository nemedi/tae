package supplier;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService
public interface SupplierContractForBank {

	String NAME = "supplierServiceForBank";
	String NAMESPACE = "http://axway.com/" + NAME;
	
	@WebMethod
	default String getServiceName() {
		return NAME;
	}
}
