package com.example.order2cash.akka.messages;

public final class PurchaseOrder extends AbstractMessage {
	
    private static final long serialVersionUID = 1L;
    
    public PurchaseOrder(String workflowId, String xml) {
        super(workflowId, xml);
    }
}
