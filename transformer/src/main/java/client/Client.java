package client;

import java.net.URL;
import java.util.Scanner;

import javax.xml.namespace.QName;

import com.sun.xml.ws.api.addressing.OneWayFeature;
import com.sun.xml.ws.api.addressing.WSEndpointReference;

import common.ServerContract;
import common.Settings;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.xml.ws.Endpoint;
import jakarta.xml.ws.EndpointReference;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.soap.AddressingFeature;

public class Client {

	public static void main(String[] args) {
		try {
			EndpointReference reference = Endpoint
					.publish(Settings.CLIENT_ENDPOINT,
							new ClientService())
				.getEndpointReference();
			ServerContract proxy = Service.create(new URL(Settings.SERVER_ENDPOINT + "?wsdl"),
						new QName("http://axway.com", "serverService"))
					.getPort(ServerContract.class,
							new AddressingFeature(),
							new OneWayFeature(true, new WSEndpointReference(reference)));
			System.out.println("Enter a transformation name and file or 'exit' to quit.");
			try (Scanner scanner = new Scanner(System.in)) {
				while (true) {
					if (scanner.hasNextLine()) {
						String command = scanner.nextLine();
						if ("exit".equalsIgnoreCase(command)) {
							System.exit(0);
						} else {
							String[] arguments = command.split("\\s+");
							if (arguments.length == 2) {
								proxy.transform(arguments[0],
										new DataHandler(new FileDataSource(
												ClientService.FOLDER + arguments[1] + ".xml")));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
