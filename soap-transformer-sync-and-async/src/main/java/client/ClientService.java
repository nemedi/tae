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

@WebService(
		targetNamespace = "http://axway.com",
		serviceName = "transformerClientService",
		endpointInterface = "common.ClientContract"
)
@MTOM
@Addressing(required = true)
public class ClientService implements ClientContract {
	
	private static final String FOLDER = "runtime/client/output";

	@Override
	public void onTransformationCompleted(String transformation,
			@XmlMimeType("application/octet-stream") DataHandler handler) {
		try {
			String name = String.format("%s/%s-%s.xml",
					FOLDER,
					transformation,
					UUID.randomUUID().toString());
			handler.writeTo(new FileOutputStream(name));
			System.out.println(String.format("Result of the transformation '%s' was saved to '%s'.",
					transformation, name));
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	@Override
	public void onTransformationFailed(String message) {
		System.err.println("Error: " + message);
	}

}
