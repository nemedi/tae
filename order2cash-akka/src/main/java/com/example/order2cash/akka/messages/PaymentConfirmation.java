package com.example.order2cash.akka.messages;

public final class PaymentConfirmation extends AbstractMessage {
    
	private static final long serialVersionUID = 1L;
	
    public PaymentConfirmation(String workflowId, String xml) {
        super(workflowId, xml);
    }
}
