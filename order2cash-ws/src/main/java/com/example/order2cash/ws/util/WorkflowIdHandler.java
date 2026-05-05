package com.example.order2cash.ws.util;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import javax.xml.namespace.QName;

import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.soap.SOAPHeaderElement;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;

public class WorkflowIdHandler implements SOAPHandler<SOAPMessageContext> {

	private static final String NS = "http://example.com/headers";
	private static final QName QNAME = new QName(NS, "workflowId", "wf");
	public static final String CONTEXT_KEY = "workflowId";

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		try {
			boolean outbound = (Boolean) context.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY);
			SOAPMessage message = context.getMessage();
			SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
			SOAPHeader header = envelope.getHeader();
			if (header == null) {
				header = envelope.addHeader();
			}
			if (outbound) {
				String workflowId = (String) context.get(CONTEXT_KEY);
				if (workflowId == null) {
					workflowId = UUID.randomUUID().toString();
				}
				SOAPHeaderElement element = header.addHeaderElement(QNAME);
				element.addTextNode(workflowId);
			} else {
				Iterator<?> iterator = header.getChildElements(QNAME);
				if (iterator != null && iterator.hasNext()) {
					SOAPHeaderElement el = (SOAPHeaderElement) iterator.next();
					String workflowId = el.getValue();
					context.put(CONTEXT_KEY, workflowId);
					context.setScope(CONTEXT_KEY, SOAPMessageContext.Scope.APPLICATION);
					System.out.println("Received workflowId: " + workflowId);
				}
			}
			message.saveChanges();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	@Override
	public void close(MessageContext context) {
	}
}