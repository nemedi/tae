package com.example.order2cash.akka.messages;

public final class PaymentInstruction extends AbstractMessage {
    
	private static final long serialVersionUID = 1L;
    
    public PaymentInstruction(String workflowId, String xml) {
        super(workflowId, xml);
    }
}
