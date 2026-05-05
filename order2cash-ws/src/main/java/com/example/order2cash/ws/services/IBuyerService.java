package com.example.order2cash.ws.services;

import jakarta.activation.DataHandler;
import jakarta.jws.Oneway;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService
public interface IBuyerService {

	@WebMethod
	@Oneway
	void onInvoice(DataHandler invoiceHandler, String workflowId);
	
	@WebMethod
	@Oneway
	void onPaymentConfirmation(DataHandler paymentConfirmationHandler, String workflowId);

}
