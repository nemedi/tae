package com.example.order2cash.ws.services;

import jakarta.activation.DataHandler;
import jakarta.jws.Oneway;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService
public interface ISupplierService {

	@WebMethod
	@Oneway
	void onPurchaseOrder(DataHandler purchaseOrderHandler, String workflowId);
	
	@WebMethod
	@Oneway
	void onInventoryResponse(DataHandler inventoryResponseHandler, String workflowId);
	
	@WebMethod
	@Oneway
	void onShipmentNotification(DataHandler shipmentNotificationHandler, String workflowId);

	@WebMethod
	@Oneway
	void onPaymentConfirmation(DataHandler paymentConfirmationHandler, String workflowId);
}
