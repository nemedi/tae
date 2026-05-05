package com.example.order2cash.ws.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.activation.DataHandler;
import jakarta.annotation.Resource;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebServiceContext;

public abstract class AbstractService {
	
	protected static final Logger log = LoggerFactory.getLogger(AbstractService.class);

	@Resource
	private WebServiceContext context;

	private Map<String, String> endpoints;
	
	public AbstractService(Map<String, String> endpoints) {
		this.endpoints = endpoints;
	}

	protected final <T> T getServiceStub(String serviceType, Class<T> type)
			throws URISyntaxException, MalformedURLException {
		return Service.create(new URI(endpoints.get(serviceType) + "?wsdl").toURL(),
				new QName("http://example.com", serviceType))
				.getPort(type);
	}
	
	protected final String convertToString(DataHandler handler)
			throws IOException {
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			handler.writeTo(stream);
			return new String(stream.toByteArray());
		}
	}
	
	protected final DataHandler convertToDataHandler(String payload) 
			throws IOException {
		return new DataHandler(
				new ByteArrayDataSource(payload.getBytes(StandardCharsets.UTF_8),
						"application/xml"));
	}
	
    protected final String shortUuid() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

}