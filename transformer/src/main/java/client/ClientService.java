package client;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import common.ClientContract;
import jakarta.activation.DataHandler;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlMimeType;
import jakarta.xml.ws.soap.Addressing;
import jakarta.xml.ws.soap.MTOM;

@WebService(serviceName = "clientService",
	targetNamespace = "http://axway.com",
	endpointInterface = "common.ClientContract")
@MTOM
@Addressing(required = true)
public class ClientService implements ClientContract {
	
	public static final String FOLDER = "data/client/";

	@Override
	public void onTransform(@XmlMimeType("application/octet-stream") DataHandler handler) {
		try {
			String output = FOLDER + UUID.randomUUID().toString() + ".xml";
			handler.writeTo(new FileOutputStream(output));
			System.out.println("Result was saved to: " + output);
		} catch (IOException e) {
		}
	}

	@Override
	public void onFault(String message) {
		System.out.println("Error: " + message);
	}

}
