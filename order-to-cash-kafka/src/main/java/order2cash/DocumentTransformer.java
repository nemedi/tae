package order2cash;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class DocumentTransformer {
	
	private static final String ROOT_FOLDER = "templates" + File.separator;

	public static byte[] transform(String xsl, byte[] data)
			throws IOException, TransformerException {
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			Transformer transformer = TransformerFactory
					.newInstance()
					.newTransformer(new StreamSource(new File(ROOT_FOLDER + xsl + File.separator + ".xsl")));
			transformer.transform(new StreamSource(
						new ByteArrayInputStream(data)),
					new StreamResult(stream));
			return stream.toByteArray();
		}
	}
}
