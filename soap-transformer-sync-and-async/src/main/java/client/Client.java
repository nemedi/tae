package client;

import java.net.InetAddress;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

import javax.xml.namespace.QName;

import com.sun.xml.ws.api.addressing.OneWayFeature;
import com.sun.xml.ws.api.addressing.WSEndpointReference;

import common.ServerContract;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.xml.ws.Endpoint;
import jakarta.xml.ws.EndpointReference;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.soap.AddressingFeature;

public class Client {
	
	private static final String PORT = "client.port";
	private static final String SERVER_ENDPOINT = "server.endpoint";
	private static final String FOLDER = "runtime/client/input";
	
	public static void main(String[] args) {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("settings");
			int port = bundle.containsKey(PORT)
					? Integer.parseInt(bundle.getString(PORT))
					: 9696;
			String serverEndpoint = bundle.getString(SERVER_ENDPOINT);
			String endpoint = String.format("http://%s:%d/transformerClientService",
					InetAddress.getLocalHost().getHostAddress(), port);
			EndpointReference reference = Endpoint.publish(endpoint, new ClientService())
					.getEndpointReference();
			ServerContract proxy = Service.create(new URL(serverEndpoint + "?wsdl"),
					new QName("http://axway.com", "transformerServerService"))
					.getPort(ServerContract.class,
							new AddressingFeature(),
							new OneWayFeature(true, new WSEndpointReference(reference)));
			System.out.println("Enter a transformation name and file or 'exit' to quit.");
			try (Scanner scanner = new Scanner(System.in)) {
				while (true) {
					if (scanner.hasNextLine()) {
						String command = scanner.nextLine();
						if ("exit".equalsIgnoreCase(command)) {
							break;
						} else {
							String[] arguments = command.split("\\s+");
							if (arguments.length == 2) {
								DataHandler handler = new DataHandler(
										new FileDataSource(
												String.format("%s/%s.xml", FOLDER, arguments[1])));
								proxy.transformAsync(arguments[0], handler);
							}
						}
					}
				}
			} finally {
				System.exit(0);
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

}
