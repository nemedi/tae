package com.example.order2cash.akka.messages;

public final class ShipmentRequest extends AbstractMessage {
	
    private static final long serialVersionUID = 1L;
    
    public ShipmentRequest(String workflowId, String xml) {
        super(workflowId, xml);
    }
}
