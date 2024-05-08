package server;

import java.net.InetAddress;
import java.util.ResourceBundle;
import java.util.Scanner;

import jakarta.xml.ws.Endpoint;

public class Server {
	
	private static final String PORT = "server.port";

	public static void main(String[] args) {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("settings");
			int port = bundle.containsKey(PORT)
					? Integer.parseInt(bundle.getString(PORT))
					: 6969;
			String endpoint = String.format("http://%s:%d/transformerServerService",
					InetAddress.getLocalHost().getHostAddress(), port);
			Endpoint.publish(endpoint, new ServerService());
			System.out.println(String.format("Server is listening on %s, type 'exit' to stop it.", endpoint));
			try (Scanner scanner = new Scanner(System.in)) {
				while (true) {
					if (scanner.hasNextLine()
							&& "exit".equalsIgnoreCase(scanner.nextLine())) {
						break;
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
