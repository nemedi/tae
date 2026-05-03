package com.example.order2cash.akka.messages;

public final class Invoice extends AbstractMessage {
    
	private static final long serialVersionUID = 1L;
    	
	public Invoice(String workflowId, String xml) {
    	super(workflowId, xml);
    }
}
