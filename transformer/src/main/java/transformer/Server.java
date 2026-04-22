package transformer;

import java.net.InetAddress;
import java.util.Scanner;

import jakarta.xml.ws.Endpoint;

public class Server {

	public static void main(String[] args) {
		try {
			String endpoint = String.format("http://%s:%d/transformer",
					InetAddress.getLocalHost().getHostAddress(), 8080);
			Endpoint.publish(endpoint, new Service());
			System.out.println(String.format("Server is lisrening on '%s', type 'exit' to close it.", endpoint));
			try (Scanner scanner = new Scanner(System.in)) {
				while (true) {
					if (scanner.hasNextLine() && "exit".equalsIgnoreCase(scanner.nextLine())) {
						break;
					}
				}
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
