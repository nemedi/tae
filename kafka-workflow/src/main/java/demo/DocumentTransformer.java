package demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.stereotype.Service;

@Service
public class DocumentTransformer {

	public byte[] transform(String actor, String transformation, byte[] data)
			throws TransformerFactoryConfigurationError,
				IOException,
				TransformerException {
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			Transformer transformer = TransformerFactory
					.newInstance()
					.newTransformer(new StreamSource(Paths.get("runtime",
							actor,
							transformation + ".xsl")
							.toFile().getAbsolutePath()));
			transformer.transform(
					new StreamSource(new ByteArrayInputStream(data)),
					new StreamResult(stream));
			return stream.toByteArray();
		}
	}
}
