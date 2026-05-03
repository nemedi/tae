package com.example.order2cash.akka.messages;

import java.util.UUID;

public class PurchaseOrderFileDetected extends AbstractMessage {

	private static final long serialVersionUID = 1L;
	
	public final String filePath;

	public PurchaseOrderFileDetected(String xml, String filePath) {
		super(UUID.randomUUID().toString(), xml);
		this.filePath = filePath;
	}

}
