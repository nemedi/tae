package com.example.order2cash.akka.messages;

public final class InventoryResponse extends AbstractMessage {
	
    private static final long serialVersionUID = 1L;
    
    public InventoryResponse(String workflowId, String xml) {
    	super(workflowId, xml);
    }
}
