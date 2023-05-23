package supplier;

import java.util.Scanner;

import common.Settings;
import jakarta.xml.ws.Endpoint;
import jakarta.xml.ws.EndpointReference;

public class SupplierServer {
	
	private static SupplierContract supplierService;
	private static EndpointReference supplierServiceForBuyerReference;
	private static EndpointReference supplierServiceForLogisticsReference;
	private static EndpointReference supplierServiceForBankReference;

	public static void main(String[] args) {
		supplierServiceForBuyerReference = Endpoint.publish(
				String.format("http://localhost:%d/%s", Settings.SUPPLIER_PORT, SupplierContractForBuyer.NAME),
				new SupplierServiceForBuyer())
				.getEndpointReference();
		supplierServiceForLogisticsReference = Endpoint.publish(
				String.format("http://localhost:%d/%s", Settings.SUPPLIER_PORT, SupplierContractForLogistics.NAME),
				new SupplierServiceForLogistics())
				.getEndpointReference();
		supplierServiceForBankReference = Endpoint.publish(
				String.format("http://localhost:%d/%s", Settings.SUPPLIER_PORT, SupplierContractForBank.NAME),
				new SupplierServiceForBank())
				.getEndpointReference();		
		Endpoint.publish(
				String.format("http://localhost:%d/%s", Settings.SUPPLIER_PORT, SupplierContract.NAME),
				supplierService = new SupplierService());
		System.out.println("Supplier server is running, type 'exit' to close it.");
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				if (scanner.hasNextLine() && "exit".equals(scanner.nextLine())) {
					System.exit(0);
				}
			}
		}
	}

}
