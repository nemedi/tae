package com.example.order2cash.ws.services;

import jakarta.activation.DataHandler;
import jakarta.jws.Oneway;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService
public interface IBankService {

	@WebMethod
	@Oneway
	void onPaymentInstruction(DataHandler paymentInstructionHandler, String workflowId);
}
