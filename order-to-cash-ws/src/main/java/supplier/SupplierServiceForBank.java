package supplier;

import jakarta.annotation.Resource;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.soap.Addressing;
import jakarta.xml.ws.soap.MTOM;

@WebService(targetNamespace = SupplierContractForBank.NAMESPACE,
	serviceName = SupplierContractForBank.NAME,
	endpointInterface = "supplier.SupplierContractForBank")
@MTOM
@Addressing(enabled = true, required = false)
public class SupplierServiceForBank implements SupplierContractForBank {

	@Resource
	private WebServiceContext context;
	
}
