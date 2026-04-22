package transformer;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Program {

	public static void main(String[] args) {
		try {
			byte[] input = Files.readAllBytes(Paths.get("./src/main/resources/airlines.xml"));
			byte[] output = XsltTransformer.transform("./src/main/resources/flights.xsl", input);
			Files.write(Paths.get("./src/main/resources/flights.xml"), output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
