package supplier;

import jakarta.activation.DataHandler;
import jakarta.annotation.Resource;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.soap.Addressing;
import jakarta.xml.ws.soap.MTOM;

@WebService(targetNamespace = SupplierContractForLogistics.NAMESPACE,
	serviceName = SupplierContractForLogistics.NAME,
	endpointInterface = "supplier.SupplierContractForLogistics")
@MTOM
@Addressing(enabled = true, required = false)
public class SupplierServiceForLogistics implements SupplierContractForLogistics {

	@Resource
	private WebServiceContext context;
	
	@Override
	public void receiveLogisticServiceResponse(DataHandler logisticServiceResponse) {
	}

	@Override
	public void receiveConsolidatorsFreightBill(DataHandler consolidatorsFreightBill) {
	}

}
