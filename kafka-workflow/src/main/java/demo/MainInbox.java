package demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.kafka.common.Uuid;
import org.springframework.stereotype.Service;

@Service
public class MainInbox {

	public void write(String actor, String document, String data) throws IOException {
		String id = Uuid.randomUuid().toString();
		Files.write(Paths.get("runtime",
				actor,
				"inbox",
				document,
				id + ".xml"),
				data.getBytes());
	}
}
