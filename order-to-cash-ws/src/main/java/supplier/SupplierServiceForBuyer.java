package supplier;

import jakarta.activation.DataHandler;
import jakarta.annotation.Resource;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlMimeType;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.soap.Addressing;
import jakarta.xml.ws.soap.MTOM;

@WebService(targetNamespace = SupplierContractForBuyer.NAMESPACE,
	serviceName = SupplierContractForBuyer.NAME,
	endpointInterface = "supplier.SupplierContractForBuyer")
@MTOM
@Addressing(enabled = true, required = false)
public class SupplierServiceForBuyer implements SupplierContractForBuyer {
	
	@Resource
	private WebServiceContext context;

	@Override
	public void receivePurchaseOrder(@XmlMimeType("application/octet-stream") DataHandler purchaseOrder) {
	}

}
