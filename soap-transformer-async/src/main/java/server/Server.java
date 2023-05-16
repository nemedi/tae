package server;

import java.util.Scanner;

import common.Settings;
import jakarta.xml.ws.Endpoint;

public class Server {

	public static void main(String[] args) {
		Endpoint.publish(Settings.SERVER_ENDPOINT, new ServerService());
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				if (scanner.hasNextLine()
						&& "exit".equals(scanner.nextLine())) {
					System.exit(0);
				}
			}
		}
	}

}
