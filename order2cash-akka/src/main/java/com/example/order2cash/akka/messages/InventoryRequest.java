package com.example.order2cash.akka.messages;

public final class InventoryRequest extends AbstractMessage {
	
    private static final long serialVersionUID = 1L;

	public InventoryRequest(String workflowId, String xml) {
    	super(workflowId, xml);
    }
}
