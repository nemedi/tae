package com.example.order2cash.akka.messages;

public final class ShipmentNotification extends AbstractMessage {
	
    private static final long serialVersionUID = 1L;
    
    public ShipmentNotification(String workflowId, String xml) {
        super(workflowId, xml);
    }
}
